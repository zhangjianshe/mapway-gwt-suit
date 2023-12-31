package cn.mapway.ui.client.mvc.attribute.editor.inspector;


import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.proxy.AttributeItemEditorProxy;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;


/**
 * SimpleAttributeGroup
 * 属性组
 *
 * @author zhang
 */
public class SimpleAttributeGroup extends CommonEventComposite {
    private static final AttributeGroupUiBinder ourUiBinder = GWT.create(AttributeGroupUiBinder.class);
    private final CommonEventHandler proxyHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isInfo()) {
                //提示事件上报
                fireEvent(event);
            }
        }
    };
    @UiField
    VerticalPanel slot;
    @UiField
    Label lbGroupName;
    int labelWidth = 150;

    public SimpleAttributeGroup() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /**
     * 设置 标签的宽度
     *
     * @param width
     */
    public void setLabelWidth(int width) {
        labelWidth = width;
        for (int i = 0; i < slot.getWidgetCount(); i++) {
            Widget w = slot.getWidget(i);
            if (w instanceof AttributeItemEditorProxy) {
                ((AttributeItemEditorProxy) w).setLabelWidth(width);
            }
        }
    }

    public void setGroupName(String group) {
        lbGroupName.setText(group);
    }

    /**
     * 属性族中添加一个属性
     *
     * @param attribute
     */
    public void appendAttribute(IAttribute attribute) {
        AttributeItemEditorProxy itemEditorProxy = new AttributeItemEditorProxy();
        itemEditorProxy.addCommonHandler(proxyHandler);
        itemEditorProxy.createEditorInstance(attribute);
        slot.add(itemEditorProxy);
    }


    /**
     * 更新属性值
     *
     * @param values
     */
    public void updateValue(List<AttributeValue> values) {
        if (values == null) {
            for (int i = 0; i < slot.getWidgetCount(); i++) {
                Widget w = slot.getWidget(i);
                if (w instanceof AttributeItemEditorProxy) {
                    AttributeItemEditorProxy proxy = (AttributeItemEditorProxy) w;
                    proxy.updateUI();
                }
            }
        } else {
            for (int i = 0; i < slot.getWidgetCount(); i++) {
                Widget w = slot.getWidget(i);
                if (w instanceof AttributeItemEditorProxy) {
                    AttributeItemEditorProxy proxy = (AttributeItemEditorProxy) w;
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
    }


    public IAttributeEditor findEditor(IAttribute attribute) {
        for (int index = 0; index < slot.getWidgetCount(); index++) {
            Widget w = slot.getWidget(index);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy editor = (AttributeItemEditorProxy) w;
                IAttribute attr = editor.getAttribute();
                if (attr.getId().equals(attribute.getId())) {
                    return editor.getEditor();
                }
            }
        }
        return null;

    }

    public AttributeItemEditorProxy findEditorProxy(IAttribute attribute) {
        for (int index = 0; index < slot.getWidgetCount(); index++) {
            Widget w = slot.getWidget(index);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy editor = (AttributeItemEditorProxy) w;
                IAttribute attr = editor.getAttribute();
                if (attr.getId().equals(attribute.getId())) {
                    return editor;
                }
            }
        }
        return null;
    }


    interface AttributeGroupUiBinder extends UiBinder<HTMLPanel, SimpleAttributeGroup> {
    }

}
