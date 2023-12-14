package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorFactory;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.util.Logs;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * 所有编辑器组件的一个代理
 * [name   :    EDITOR]
 */
public class AttributeItemEditorProxy extends Composite {
    private static final AttributeItemEditorProxyUiBinder ourUiBinder = GWT.create(AttributeItemEditorProxyUiBinder.class);
    @UiField
    Label lbHeader;
    @UiField
    SStyle style;
    @UiField
    HTMLPanel box;
    IAttributeEditor attributeEditor;

    public AttributeItemEditorProxy() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /**
     * 创建编辑器实例
     *
     * @param attribute     编辑器组件的定义(属性的定义)
     */
    public void createEditorInstance(IAttribute attribute) {
        if (attribute == null || attribute.getEditorCode() == null) {
            Logs.info("Attribute is null or editorCode is null in AttributeItemEditorProxy");
            return;
        }

        box.clear();
        box.add(lbHeader);

        //创建统一的属性列表编辑UI
        attributeEditor = AttributeEditorFactory.get().createEditor(attribute.getEditorCode(), false);
        if (attributeEditor == null) {
            errorInput("创建属性组件出错" + attribute.getEditorCode());
            return;
        }

        Widget displayWidget = attributeEditor.getDisplayWidget();
        box.add(displayWidget);

        //让属性编辑器 自己处理数据逻辑
        attributeEditor.setAttribute(attribute.getRuntimeOption(), attribute);

        //
        lbHeader.setText(getAttributeName());
    }

    public IAttribute getAttribute() {
        if (attributeEditor != null) {
            return attributeEditor.getAttribute();
        } else {
            return null;
        }
    }


    public IAttributeEditor getEditor() {
        return attributeEditor;
    }

    private void errorInput(String msg) {
        Label error = new Label(msg);
        error.setTitle(msg);
        error.setStyleName(style.error());
        box.setStyleName(style.layout2());
        box.add(error);
    }

    public void setLabelWidth(int width) {
        if (box.getWidgetCount() == 2) {
            Style style1 = box.getElement().getStyle();
            style1.setProperty("gridTemplateColumns", width + "px 1fr");
        } else if (box.getWidgetCount() == 3) {
            Style style1 = box.getElement().getStyle();
            style1.setProperty("gridTemplateColumns", width + "px 1fr 80px");
        }
    }

    public void updateEditorOption(String key, Object value) {
        attributeEditor.updateEditorOption(key, value);
    }

    public void updateAllEditorOption() {
        attributeEditor.updateAllEditorOption();
    }


    /**
     * 运行编辑器是否可以编辑
     *
     * @param readOnly
     */
    public void setEditorReadOnly(boolean readOnly) {
        if (attributeEditor != null) {
            attributeEditor.setReadonly(readOnly);
        }
    }

    /**
     * 获取属性的名称
     *
     * @return
     */
    private String getAttributeName() {
        if (attributeEditor == null) {
            return "没有编辑器定义";
        } else {
            IAttribute attribute = attributeEditor.getAttribute();
            if (attribute.getAltName() != null && attribute.getAltName().length() > 0) {
                return attribute.getAltName();
            } else {
                return attribute.getName();
            }
        }
    }

    interface AttributeItemEditorProxyUiBinder extends UiBinder<HTMLPanel, AttributeItemEditorProxy> {
    }

    interface SStyle extends CssResource {

        String layout2();

        String head();

        String error();
    }

}