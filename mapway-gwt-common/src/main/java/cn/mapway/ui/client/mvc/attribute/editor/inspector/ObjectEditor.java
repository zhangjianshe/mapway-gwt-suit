package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.mvc.Size;
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
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.client.widget.panel.MessagePanel;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象编辑器
 */
public class ObjectEditor extends CommonEventComposite implements IData<IAttributesProvider>, IAttributeReadyCallback, AttributeStateChangeEventHandler {
    private static final ObjectEditorUiBinder ourUiBinder = GWT.create(ObjectEditorUiBinder.class);
    private static Dialog<ObjectEditor> dialog;
    Size size;
    @Getter
    @UiField
    SaveBar saveBar;
    @UiField
    FlexTable table;
    @UiField
    MessagePanel messageBar;
    @Setter
    int columns = 2;
    @Setter
    int labelWidth = 0;
    @Setter
    String labelStyle = "";
    @Setter
    boolean enableGroup = true;
    @UiField
    SStyle style;
    @UiField
    DockLayoutPanel root;
    @UiField
    HTMLPanel tipPanel;
    @UiField
    MessagePanel messagePanel;
    HandlerRegistration stateChangeHandler = null;
    Map<String, AttributeItemEditorProxy> editorMaps = new HashMap<>();
    boolean tipPanelVisible = false;
    private final CommonEventHandler proxyHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isInfo()) {
                if (tipPanelVisible) {
                    messagePanel.setHtml(event.getValue());
                } else if (saveBar.isVisible()) {
                    saveBar.msg(event.getValue());
                } else {
                    fireEvent(event);
                }
            }
        }
    };
    private IAttributesProvider provider;

    public ObjectEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        size = new Size(Window.getClientWidth() - 400, Window.getClientHeight() - 400);
        //缺省不显示　保存栏　当使用getDialog()获取时　缺省显示保存栏
        setSaveBarVisible(false);
    }

    public static Dialog<ObjectEditor> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }

    private static Dialog<ObjectEditor> createOne() {
        ObjectEditor editor = new ObjectEditor();
        editor.setSaveBarVisible(true);
        return new Dialog<>(editor, "对象编辑器");
    }

    @Override
    public IAttributesProvider getData() {
        return provider;
    }

    @Override
    public void setData(IAttributesProvider obj) {
        if (provider != null) {
            provider.removeAttributeReadyCallback(this);
        }
        if (stateChangeHandler != null) {
            stateChangeHandler.removeHandler();
        }
        provider = obj;
        if (provider != null) {
            //异步加载属性
            provider.addAttributeReadyCallback(this);
            stateChangeHandler = provider.addAttributeStateChangeHandler(this);
        }
        updateUI();
    }

    public void setTipPanelVisible(boolean visible) {
        tipPanelVisible = visible;
        if (tipPanelVisible) {
            root.setWidgetSize(tipPanel, 50);
        } else {
            root.setWidgetSize(tipPanel, 0);
        }
    }

    public void setSaveBarVisible(boolean visible) {
        root.setWidgetHidden(saveBar, !visible);
    }

    private void updateUI() {
        table.removeAllRows();
        if (provider == null) {
            messageBar.setText("没有设定编辑对象");
            messageBar.setVisible(true);
        } else {
            fireEvent(CommonEvent.titleEvent(provider.getAttributeTitle()));
            Map<String, List<IAttribute>> groups = new HashMap<>();
            editorMaps.clear();
            if (enableGroup) {
                for (IAttribute attribute : provider.getAttributes()) {
                    String groupName = attribute.getGroup();
                    if (StringUtil.isBlank(groupName)) {
                        groupName = "基本属性";
                    }
                    List<IAttribute> list = groups.get(groupName);
                    if (list == null) {
                        list = new ArrayList<>();
                        groups.put(groupName, list);
                    }
                    list.add(attribute);
                }
            } else {
                groups.put("基本属性", provider.getAttributes());
            }

            int row = -1;
            FlexTable.FlexCellFormatter formatter = table.getFlexCellFormatter();
            boolean showGroupLabel = enableGroup && groups.size() > 1;
            int col = 0;
            for (String groupName : groups.keySet()) {
                List<IAttribute> list = groups.get(groupName);
                if (list.isEmpty()) {
                    continue;
                }
                row++;
                col = 0;
                //添加一个分组名称行
                if (showGroupLabel) {
                    table.setWidget(row, col, new Header(groupName));
                    formatter.setColSpan(row, col, columns);
                    row++;
                }
                col = 0;
                for (IAttribute attribute : list) {
                    if (col >= columns) {
                        //需要换到新的一行
                        row++;
                        col = 0;
                    }
                    AttributeItemEditorProxy itemEditorProxy = new AttributeItemEditorProxy();

                    itemEditorProxy.addCommonHandler(proxyHandler);
                    itemEditorProxy.createEditorInstance(attribute);
                    table.setWidget(row, col++, itemEditorProxy);
                    if (labelWidth > 0) {
                        itemEditorProxy.setLabelWidth(labelWidth);
                    }
                    if (StringUtil.isNotBlank(labelStyle)) {
                        itemEditorProxy.getLabel().setStyleName(labelStyle);
                    }
                    editorMaps.put(attribute.getName(), itemEditorProxy);
                }
            }

        }
    }

    public void setLabelWidth(int width) {
        labelWidth = width;
        if (labelWidth > 0) {
            for (AttributeItemEditorProxy itemEditorProxy : editorMaps.values()) {
                itemEditorProxy.setLabelWidth(labelWidth);
            }
        }
    }

    /**
     * 更新属性值
     *
     * @param values
     */
    public void updateValue(List<AttributeValue> values) {
        if (values == null) {
            for (AttributeItemEditorProxy proxy : editorMaps.values()) {
                proxy.updateUI();
            }
        } else {
            for (AttributeItemEditorProxy proxy : editorMaps.values()) {
                String attributeName = proxy.getAttribute().getName();
                for (AttributeValue attributeValue : values) {
                    if (attributeValue.getName().equals(attributeName)) {
                        proxy.getAttribute().setValue(attributeValue.getValue());
                        proxy.updateUI();
                        break;
                    }
                }
            }
        }
    }

    public void setSize(int width, int height) {
        size.set(width, height);
    }

    @Override
    public Size requireDefaultSize() {
        return size;
    }

    @Override
    public void onAttributeReady(IAttributesProvider attributeProvider) {
        updateUI();
    }

    @Override
    public void onAttributeStateChange(AttributeStateChangeEvent event) {
        AttributeItemEditorProxy proxy = editorMaps.get(event.attribute.getName());
        if (proxy == null) {
            return;
        }
        switch (event.state) {
            case ATTRIBUTE_STATE_HIDE:
                proxy.setVisible(false);
                break;
            case ATTRIBUTE_STATE_VISIBLE:
                proxy.setVisible(true);
                break;
            default:
        }
    }

    @Override
    public boolean isGroupInitExpand(String groupName) {
        return super.isGroupInitExpand(groupName);
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            fireEvent(CommonEvent.saveEvent(provider));
        } else {
            fireEvent(event);
        }
    }

    interface SStyle extends CssResource {

        String table();

        String header();

        String tipPanel();
    }

    interface ObjectEditorUiBinder extends UiBinder<DockLayoutPanel, ObjectEditor> {
    }
}

