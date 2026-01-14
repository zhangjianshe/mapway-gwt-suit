package cn.mapway.ui.client.mvc.attribute.editor.button;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorCodes;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.widget.buttons.AiButton;
import cn.mapway.ui.shared.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

@AttributeEditor(value = ButtonAttributeEditor.EDITOR_CODE,
        name = "按钮编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "按钮编辑器",
        author = "ZJS",
        icon = Fonts.IMAGE_BUTTON
)
public class ButtonAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = EditorCodes.EDITOR_BUTTON;
    private static final ButtonAttributeEditorUiBinder ourUiBinder = GWT.create(ButtonAttributeEditorUiBinder.class);
    @UiField
    AiButton btn;
    @UiField
    HTMLPanel panel;

    public ButtonAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        btn.addClickHandler(event -> {
            getAttribute().setValue("");
        });
    }

    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    @Override
    public Widget getDisplayWidget() {
        return panel;
    }

    @Override
    public void updateUI() {
        btn.setText(DataCastor.castToString(getAttribute().getValue()));
        String buttonType = option(ParameterKeys.KEY_BUTTON_TYPE, ButtonType.PRIMARY.getValue());
        ButtonType button = ButtonType.fromValue(buttonType);
        btn.setButtonStyle(button.getValue());
    }

    @Override
    public void fromUI() {

    }

    interface ButtonAttributeEditorUiBinder extends UiBinder<HTMLPanel, ButtonAttributeEditor> {
    }
}