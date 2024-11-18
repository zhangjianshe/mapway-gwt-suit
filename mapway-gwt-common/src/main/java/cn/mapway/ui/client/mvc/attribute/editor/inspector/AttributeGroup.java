package cn.mapway.ui.client.mvc.attribute.editor.inspector;


import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributePropertyChangeCallback;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.proxy.AttributeItemEditorProxy;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;


/**
 * AttributeGroup
 * 属性组
 *
 * @author zhang
 */
public class AttributeGroup extends CommonEventComposite implements IAttributePropertyChangeCallback {
    private static final AttributeGroupUiBinder ourUiBinder = GWT.create(AttributeGroupUiBinder.class);
    private final CommonEventHandler itemEditorProxyHandler = commonEvent -> fireEvent(commonEvent);
    @UiField
    VerticalPanel slot;
    @UiField
    Label lbGroupName;
    @UiField
    FontIcon lbExpand;
    @UiField
    Label lbCount;
    int labelWidth = 150;

    public AttributeGroup() {
        initWidget(ourUiBinder.createAndBindUi(this));
        lbExpand.setIconUnicode(Fonts.DOWN);
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
        itemEditorProxy.addCommonHandler(itemEditorProxyHandler);
        itemEditorProxy.createEditorInstance(attribute);
        itemEditorProxy.setLabelWidth(labelWidth);
        //属性特性变更后毁掉到此类
        attribute.setChangeCallback(this);
        slot.add(itemEditorProxy);
        itemEditorProxy.setVisible(attribute.getAttrVisible());
    }

    public void updateUI() {
        int visibleCount = 0;
        for (int index = 0; index < slot.getWidgetCount(); index++) {
            Widget w = slot.getWidget(index);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy editor = (AttributeItemEditorProxy) w;
                editor.updateUI();
                if (editor.isVisible()) {
                    visibleCount++;
                }
            }
        }
        lbCount.setText(visibleCount + "项");
        setVisible(visibleCount != 0);
    }

    /**
     * 更新属性值
     *
     * @param values
     */
    public void updateValue(List<AttributeValue> values) {
        for (int i = 0; i < slot.getWidgetCount(); i++) {
            Widget w = slot.getWidget(i);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy proxy = (AttributeItemEditorProxy) w;
                String attributeName = proxy.getAttribute().getName();
                for (AttributeValue attributeValue : values) {
                    if (attributeValue.getName().equals(attributeName)) {
                        proxy.getAttribute().setValue(attributeValue.getValue());
                        break;
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

    @UiHandler("lbExpand")
    public void lbExpandClick(ClickEvent event) {
        showOrHideContent();
    }

    @UiHandler("lbGroupName")
    public void lbGroupNameClick(ClickEvent event) {
        showOrHideContent();
    }

    private void showOrHideContent() {
        if (lbExpand.getIconUnicode().equals(Fonts.RIGHT)) {
            lbExpand.setIconUnicode(Fonts.DOWN);
            slot.setVisible(true);
        } else {

            lbExpand.setIconUnicode(Fonts.RIGHT);
            slot.setVisible(false);
        }
    }

    /**
     * 从UI获取数据到 对象实体中
     */
    public void fromUI() {
        for (int index = 0; index < slot.getWidgetCount(); index++) {
            Widget w = slot.getWidget(index);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy editor = (AttributeItemEditorProxy) w;
                editor.fromUI();
            }
        }
    }

    /**
     * 某个属性发生变化 显示或者隐藏
     *
     * @param iAttribute
     */
    @Override
    public void onAttributePropertyChange(IAttribute iAttribute) {
        AttributeItemEditorProxy proxy = findEditorProxy(iAttribute);
        if (proxy == null) {
            return;
        }
        proxy.setVisible(iAttribute.getAttrVisible());
        updateUI();

        //TODO 可以添加其他属性peroperty的变化改变
    }


    interface AttributeGroupUiBinder extends UiBinder<HTMLPanel, AttributeGroup> {
    }

}
