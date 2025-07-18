package cn.mapway.ui.client.mvc.attribute.editor.inspector;


import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributeReadyCallback;
import cn.mapway.ui.client.mvc.attribute.IAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.editor.proxy.AttributeItemEditorProxy;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEvent;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEventHandler;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ObjectInspector
 * 对象属性检查器
 *
 * @author zhang
 */
public class ObjectInspector extends CommonEventComposite implements IData<IAttributesProvider>, IAttributeReadyCallback, RequiresResize, AttributeStateChangeEventHandler {
    private static final ObjectInspectorUiBinder ourUiBinder = GWT.create(ObjectInspectorUiBinder.class);
    private static final ObjectInspector2UiBinder ourUiBinder2 = GWT.create(ObjectInspector2UiBinder.class);
    Map<String, AttributeGroup> groups = new HashMap<>();
    @UiField
    HTMLPanel panel;
    @UiField
    HTML htmlSummary;
    private final CommonEventHandler groupHandler = event -> {
        if (event.isInfo()) {
            String information = event.getValue();
            showInfo(information);
        }
    };
    @UiField
    ScrollPanel right;
    @UiField
    DockLayoutPanel root;
    @UiField
    Label lbName;
    @UiField
    FontIcon btnSave;
    @UiField
    HTML messagePanel;
    @UiField
    HTMLPanel namePanel;
    @UiField
    FontIcon btnInfo;
    List<AttributeValue> values;
    int labelWidth = 150;
    HandlerRegistration stateChangeHandler;
    boolean vertical;
    private IAttributesProvider data;

    @UiConstructor()
    public ObjectInspector(boolean vertical) {
        this.vertical = vertical;
        if (vertical) {
            initWidget(ourUiBinder2.createAndBindUi(this));
        } else {
            initWidget(ourUiBinder.createAndBindUi(this));
        }
        namePanel.addDomHandler(event -> {
            if (data != null) {
                showInfo(data.getAttributeSummary());
            }
        }, MouseOverEvent.getType());
        btnInfo.setIconUnicode(Fonts.INFO);
        btnSave.setIconUnicode(Fonts.SAVE);
    }

    private void showInfo(String infoHtml) {
        htmlSummary.setHTML(infoHtml);
    }

    @Override
    public IAttributesProvider getData() {
        return data;
    }

    @Override
    public void setData(IAttributesProvider obj) {
        if(Objects.equals(data, obj)) {
            return;
        }
        if (data != null) {
            data.removeAttributeReadyCallback(this);
        }
        if (stateChangeHandler != null) {
            stateChangeHandler.removeHandler();
        }
        data = obj;
        if (data != null) {
            //异步加载属性
            data.addAttributeReadyCallback(this);
            stateChangeHandler = data.addAttributeStateChangeHandler(this);
        }
        //同步加载属性
        toUI();
        updateValue(this.values);
    }

    /**
     * 更新属性界面的值
     * 根据altName字段进行匹配
     *
     * @param values
     */
    public void updateValue(List<AttributeValue> values) {
        this.values = values;
        if (this.values != null && groups != null) {
            for (AttributeGroup group : groups.values()) {
                group.updateValue(values);
            }
        }
    }


    public void setName(String name) {
        lbName.setText(name);
    }

    public void setSaveButtonEnable(boolean enableSave) {
        btnSave.setEnabled(enableSave);
    }

    public void setSaveButtonVisible(boolean showSave) {
        btnSave.setVisible(showSave);
    }

    /**
     * 构造属性编辑界面
     */
    private void toUI() {
        //清空界面
        clear();
        setSaveButtonEnable(isSavable());

        if (data == null) {
            htmlSummary.setText("");
            return;
        }
        htmlSummary.setHTML(data.getAttributeSummary());
        lbName.setText(data.getAttributeTitle());

        //分组并排序
        Map<String, List<IAttribute>> sortedHash = new HashMap<>();
        for (IAttribute attribute : data.getAttributes()) {
            String groupName = attribute.getGroup();
            if (groupName == null) {
                groupName = "";
            }
            List<IAttribute> list = sortedHash.computeIfAbsent(groupName, k -> new ArrayList<>());
            list.add(attribute);
        }
        for (List<IAttribute> list : sortedHash.values()) {
            list.sort(Comparator.comparingInt(IAttribute::getRank));
        }

        // group 怎么排序?　假定　group 的名字前面添加　数字序号　比如　１. 2. 通过提取前面的数字序号　进行排序　如果
        // 没有序号，则按照字典序排序
        List<String> stringList = sortedHash.keySet().stream().sorted().collect(Collectors.toList());
        for (String key : stringList) {
            String groupName = key.replaceFirst("\\d+\\.", "");
            AttributeGroup group = sureGroup(groupName);
            for (IAttribute attribute : sortedHash.get(key)) {
                group.appendAttribute(attribute);
            }
        }

        for (AttributeGroup group : groups.values()) {
            group.updateUI();
            //初始化分组的显示和隐藏
            if(!data.isGroupInitExpand(group.getGroupName())){
                group.setExpand(false);
            }
        }

        if (data.getAttributes().size() == 0) {
            setMessage("<b>没有属性可编辑</b>", 100, false);
        } else {
            setMessage("", 0, false);
        }
    }

    boolean isSavable() {
        return data != null && data.getAttributes() != null && data.getAttributes().size() > 0;
    }

    /**
     * 设置标签宽度
     *
     * @param width
     */
    public void setLabelWidth(int width) {
        labelWidth = width;
        for (AttributeGroup group : groups.values()) {
            group.setLabelWidth(width);
        }
    }

    public void clear() {
        groups.clear();
        htmlSummary.setHTML("");
        panel.clear();
    }

    private AttributeGroup sureGroup(String groupName) {
        AttributeGroup attributeGroup = groups.get(groupName);
        if (attributeGroup == null) {
            attributeGroup = new AttributeGroup();
            attributeGroup.setLabelWidth(labelWidth);
            attributeGroup.addCommonHandler(groupHandler);
            attributeGroup.setGroupName(groupName);
            groups.put(groupName, attributeGroup);
            panel.add(attributeGroup);
        }
        return attributeGroup;
    }


    @Override
    public void onAttributeReady(IAttributesProvider attributeProvider) {
        toUI();
        updateValue(this.values);
    }

    @UiHandler("btnSave")
    public void btnSaveClick(ClickEvent event) {
        if (data != null) {
            fromUI();
        }
        fireEvent(CommonEvent.saveEvent(data));
    }

    /**
     * 保存数据到对象
     */
    public void saveDataToObject() {
        fromUI();
    }

    @UiHandler("btnInfo")
    public void btnInfoClick(ClickEvent event) {
        int height = root.getOffsetHeight();
        int width = root.getOffsetWidth();
        double widgetSize = root.getWidgetSize(right);
        if (widgetSize < 1.0) {
            //之前是隐藏的 现在显示他
            if (vertical) {
                int summaryHeight = height / 3;
                if (summaryHeight > 200) {
                    summaryHeight = 200;
                } else if (summaryHeight < 80) {
                    summaryHeight = 80;
                }

                root.setWidgetSize(right, summaryHeight);
            } else {
                int summaryHeight = width / 4;
                if (summaryHeight > 200) {
                    summaryHeight = 200;
                } else if (summaryHeight < 80) {
                    summaryHeight = 80;
                }

                root.setWidgetSize(right, summaryHeight);
            }

        } else {
            //现在隐藏
            root.setWidgetSize(right, 0);
        }

    }

    /**
     * 验证属性输入是否正确,如果正确
     */
    public List<AttributeValue> validate() {
        //将数据转移到属性对象中
        List<AttributeValue> attributeValues = data.flatten();
        return attributeValues;
    }


    /**
     * 从UI获取数据到 对象实体中
     */
    protected void fromUI() {
        for (AttributeGroup group : groups.values()) {
            group.fromUI();
        }
    }

    @Override
    public void onResize() {
        root.onResize();
    }


    /**
     * 设置Sumamry Height
     *
     * @param height
     */
    public void setSummaryHeight(int height) {
        if (height < 0) {
            height = 0;
        }

        root.setWidgetSize(right, height);
    }

    public void setMessage(String html, int height, boolean autoHide) {
        if (StringUtil.isBlank(html)) {
            root.setWidgetSize(messagePanel, 0);
        } else {
            if (height <= 0) {
                height = 50;
            }
            root.setWidgetSize(messagePanel, height);
            messagePanel.setHTML(html);
            if (autoHide) {
                //5秒钟后自动隐藏
                Scheduler.get().scheduleFixedDelay(() -> {
                    setMessage("", 0, false);
                    return false;
                }, 5000);
            }
        }
    }

    /**
     * 属性变更发生了
     *
     * @param attributeStateChangeEvent
     */
    @Override
    public void onAttributeStateChange(AttributeStateChangeEvent attributeStateChangeEvent) {
        AttributeItemEditorProxy proxy = findEditorProxy(attributeStateChangeEvent.attribute);
        if (proxy == null) {
            return;
        }
        switch (attributeStateChangeEvent.state) {
            case ATTRIBUTE_STATE_HIDE:
                proxy.setVisible(false);
                break;
            case ATTRIBUTE_STATE_VISIBLE:
                proxy.setVisible(true);
                break;
            default:
        }
    }


    /**
     * 根据属性 查找对应ITEM
     *
     * @param attribute
     * @return
     */
    private AttributeItemEditorProxy findEditorProxy(IAttribute attribute) {
        for (AttributeGroup group : groups.values()) {
            AttributeItemEditorProxy proxy = group.findEditorProxy(attribute);
            if (proxy != null) {
                return proxy;
            }
        }
        return null;
    }

    interface ObjectInspectorUiBinder extends UiBinder<DockLayoutPanel, ObjectInspector> {
    }

    @UiTemplate("ObjectInspector2.ui.xml")
    interface ObjectInspector2UiBinder extends UiBinder<DockLayoutPanel, ObjectInspector> {
    }
}
