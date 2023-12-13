package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorFactory;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
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

public class AttributeItemEditorProxy extends Composite {
    private static final AttributeItemEditorProxyUiBinder ourUiBinder = GWT.create(AttributeItemEditorProxyUiBinder.class);
    @UiField
    Label lbHeader;
    @UiField
    SStyle style;
    @UiField
    HTMLPanel box;
    IAttributeEditor attributeEditor;
    EditorOption editorOption;
    private IAttribute attribute;

    public AttributeItemEditorProxy() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setAttribute(EditorOption editorOption, IAttribute obj) {
        this.attribute = obj;
        this.editorOption = editorOption;
        toUI();
    }

    public IAttribute getAttribute() {
        return attribute;
    }

    public EditorOption getEditorOption() {
        return editorOption;
    }


    public IAttributeEditor getEditor() {
        return attributeEditor;
    }

    private void toUI() {
        if (attribute == null) {
            Logs.info("Attribute is null in attributeItemEditorProxy");
            return;
        }
        AttributeEditorMetaData editorMetaData = attribute.getEditorMetaData();
        loadEditor(editorMetaData.code);
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


    private void loadEditor(String code) {

        box.clear();
        box.add(lbHeader);
        lbHeader.setText(getAttributeName());
        //创建统一的属性列表编辑UI
        attributeEditor = AttributeEditorFactory.get().createEditor(code, false);
        if (attributeEditor == null) {
            errorInput("创建属性组件出错" + code);
            return;
        }

        Widget displayWidget = attributeEditor.getDisplayWidget();
        int columnCount = 1;
        if (displayWidget != null) {
            box.add(displayWidget);
            columnCount++;
        }

        //让属性编辑器 自己处理数据逻辑
        attributeEditor.setAttribute(editorOption, attribute);

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

    private String getAttributeName() {
        if (attribute.getAltName() != null && attribute.getAltName().length() > 0) {
            return attribute.getAltName();
        }
        return attribute.getName();
    }

    interface AttributeItemEditorProxyUiBinder extends UiBinder<HTMLPanel, AttributeItemEditorProxy> {
    }

    interface SStyle extends CssResource {

        String layout2();

        String head();

        String error();
    }

}