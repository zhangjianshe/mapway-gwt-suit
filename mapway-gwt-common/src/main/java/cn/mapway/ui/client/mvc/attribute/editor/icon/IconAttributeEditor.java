package cn.mapway.ui.client.mvc.attribute.editor.icon;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.widget.buttons.IconSelectorButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

@AttributeEditor(value = IconAttributeEditor.EDITOR_CODE,
        name = "ICON编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "选择一个图标",
        icon = Fonts.FIRE,
        author = "ZJS")
public class IconAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "ICON_EDITOR";
    private static final IconAttributeEditorUiBinder ourUiBinder = GWT.create(IconAttributeEditorUiBinder.class);
    @UiField
    IconSelectorButton iconSelector;

    String selectedValue = null;

    public IconAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));

        iconSelector.addValueChangeHandler(event -> {
            if (getAttribute() != null) {
                selectedValue = event.getValue();
                getAttribute().setValue(event.getValue());
            }
        });

    }

    @Override
    public Widget getDisplayWidget() {
        return iconSelector;
    }


    /**
     *
     */
    @Override
    public void updateUI() {
        if (getAttribute() == null) {
            return;
        }
        iconSelector.setValue(DataCastor.castToString(getAttribute().getValue()));
    }


    @Override
    public void fromUI() {
        if (getAttribute() != null && selectedValue != null && selectedValue.length() > 0) {
            getAttribute().setValue(selectedValue);
        }
    }

    /**
     * @return
     */
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }


    interface IconAttributeEditorUiBinder extends UiBinder<HTMLPanel, IconAttributeEditor> {
    }


}