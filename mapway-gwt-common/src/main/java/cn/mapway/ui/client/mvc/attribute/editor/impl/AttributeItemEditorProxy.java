package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.InputTypeEnum;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorFactory;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.widget.dialog.Popup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

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


    private void toUI() {
        InputTypeEnum inputTypeEnum = InputTypeEnum.valueOfCode(attribute.getInputType());
        switch (inputTypeEnum) {
            case INPUT_CUSTOM_EDITOR:
                loadEditor(attribute.getEditorModuleCode());
                return;
            case INPUT_COLOR:
                loadEditor(ColorBoxAttributeEditor.EDITOR_CODE);
                return;
            case INPUT_DROPDOWN:
                loadEditor(DropdownAttributeEditor.EDITOR_CODE);
                return;
            case INPUT_CHECKBOX:
                loadEditor(CheckBoxAttributeEditor.EDITOR_CODE);
                return;
            case INPUT_TEXTAREA:
                loadEditor("TEXTAREA_EDITOR");
                return;
            case INPUT_SLIDER:
                loadEditor("SLIDER_EDITOR");
                return;
            case INPUT_FILE:
            case INPUT_PATH:
            case INPUT_PATH_ALL:
            case INPUT_MULTI_FILE:
                loadEditor("FILE_DIR_EDITOR");
                return;
            case INPUT_TEXTBOX:
            case INPUT_OTHERS:
                loadEditor(TextboxAttributeEditor.EDITOR_CODE);
                return;
            default:
                errorInput("不支持类型 " + inputTypeEnum.getName());
        }
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
        final Widget popupWidget = attributeEditor.getPopupWidget();
        int columnCount = 1;
        if (displayWidget != null) {
            box.add(displayWidget);
            columnCount++;
        }

        if (popupWidget != null) {
            Button btn = new Button("选择");
            box.add(btn);
            columnCount++;
            btn.addClickHandler(event -> {
                showPopup(box, attributeEditor);
            });
        }
        if (columnCount == 2) {
            box.setStyleName(style.layout2());
        } else if (columnCount == 3) {
            box.setStyleName(style.layout3());
        }
        //让属性编辑器 自己处理数据逻辑
        attributeEditor.setAttribute(editorOption, attribute);

    }

    /**
     * 弹出对话框
     *
     * @param popupWidget
     * @param attributeEditor
     */
    private void showPopup(Widget popupWidget, IAttributeEditor attributeEditor) {

        Popup<Widget> popup = new Popup<>(attributeEditor.getPopupWidget());
        Size size = attributeEditor.getSize();
        popup.setPixelSize(size.getXAsInt(), size.getYAsInt());
        popup.addCommonHandler(commonEvent -> {
            if (commonEvent.isOk()) {
                attributeEditor.setValue(commonEvent.getValue(), true);
            }
            popup.hide(true);
        });
        attributeEditor.popupInit(getEditorOption());
        popup.showRelativeTo(popupWidget);

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

        String layout3();

        String head();

        String error();
    }

}