package cn.mapway.ui.client.mvc.attribute.editor.inspector;


import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributeReadyCallback;
import cn.mapway.ui.client.mvc.attribute.IAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.proxy.AttributeItemEditorProxy;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEvent;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEventHandler;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单的对象属性检查器
 * 接受一个 IAttributesProvider
 *
 * @author zhang
 */
public class SimpleObjectInspector extends Composite implements IData<IAttributesProvider>, IAttributeReadyCallback, AttributeStateChangeEventHandler, HasCommonHandlers {
    private static final AttributeEditorUiBinder ourUiBinder = GWT.create(AttributeEditorUiBinder.class);
    private final CommonEventHandler groupHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isInfo()) {
                fireEvent(event);
            }
        }
    };
    Map<String, SimpleAttributeGroup> groups = new HashMap<>();
    @UiField
    HTMLPanel panel;
    @UiField
    Label lbHeader;
    int labelWidth = 150;
    HandlerRegistration stateChangeHandler;
    private IAttributesProvider data;


    @UiConstructor()
    public SimpleObjectInspector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public IAttributesProvider getData() {
        return data;
    }

    @Override
    public void setData(IAttributesProvider obj) {
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
    }

    /**
     * 更新属性界面的值
     * 根据altName字段进行匹配
     *
     * @param values null 从 IAttribute中getValue获取数据更新UI
     */
    public void updateValue(List<AttributeValue> values) {
        lbHeader.setTitle(getData().getAttributeTitle());
        if (groups == null || groups.size() == 0) {
            return;
        }
        for (SimpleAttributeGroup group : groups.values()) {
            group.updateValue(values);
        }

    }


    /**
     * 构造属性编辑界面
     */
    private void toUI() {
        //清空界面
        clear();
        for (IAttribute attribute : data.getAttributes()) {
            SimpleAttributeGroup group = sureGroup(attribute);
            // attribute 中包含了设计器的组件参数
            group.appendAttribute(attribute);
        }
    }

    /**
     * 设置标签宽度
     *
     * @param width
     */
    public void setLabelWidth(int width) {
        labelWidth = width;
        for (SimpleAttributeGroup group : groups.values()) {
            group.setLabelWidth(width);
        }
    }

    public void clear() {
        groups.clear();
        panel.clear();
    }

    private SimpleAttributeGroup sureGroup(IAttribute attribute) {

        String groupName = attribute.getGroup();
        if (groupName == null || groupName.length() == 0) {
            groupName = "其他";
        }
        SimpleAttributeGroup attributeGroup = groups.get(groupName);

        if (attributeGroup == null) {
            attributeGroup = new SimpleAttributeGroup();
            attributeGroup.setLabelWidth(labelWidth);
            attributeGroup.setGroupName(attribute.getGroup());
            attributeGroup.addCommonHandler(groupHandler);
            groups.put(attribute.getGroup(), attributeGroup);
            panel.add(attributeGroup);
        }

        return attributeGroup;
    }


    @Override
    public void onAttributeReady(IAttributesProvider attributeProvider) {
        toUI();
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
     * 根据属性 查找对应的编辑器
     *
     * @param attribute
     * @return
     */
    private IAttributeEditor findEditor(IAttribute attribute) {
        for (SimpleAttributeGroup group : groups.values()) {
            IAttributeEditor editor = group.findEditor(attribute);
            if (editor != null) {
                return editor;
            }
        }
        return null;
    }

    /**
     * 根据属性 查找对应ITEM
     *
     * @param attribute
     * @return
     */
    private AttributeItemEditorProxy findEditorProxy(IAttribute attribute) {
        for (SimpleAttributeGroup group : groups.values()) {
            AttributeItemEditorProxy proxy = group.findEditorProxy(attribute);
            if (proxy != null) {
                return proxy;
            }
        }
        return null;
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

    interface AttributeEditorUiBinder extends UiBinder<HTMLPanel, SimpleObjectInspector> {
    }

}
