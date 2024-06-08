package cn.mapway.ui.client.mvc.attribute.editor.padding;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.dialog.Popup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Padding box 数值编辑
 * box数值为 [top right bottom left]
 * 或者      [top bottom] [left right]
 * 或者       [value]
 */
@AttributeEditor(value = PaddingBoxAttributeEditor.EDITOR_CODE,
        name = "边框Padding编辑器",
        group = IAttributeEditor.CATALOG_DESIGN,
        summary = "边框Padding编辑器",
        author = "ZJS",
        icon = Fonts.BORDER
)
public class PaddingBoxAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "padding_box_editor";
    private static final PaddingBoxAttributeEditorUiBinder ourUiBinder = GWT.create(PaddingBoxAttributeEditorUiBinder.class);
    String[] paddings = new String[]{"0", "0", "0", "0"};
    @UiField
    FontIcon btnSetup;
    @UiField
    TextBox txtValue;
    @UiField
    HTMLPanel editor;

    public PaddingBoxAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        btnSetup.setIconUnicode(Fonts.MORE);
        txtValue.addValueChangeHandler(event -> {
            String value = event.getValue();
            setPadding(value, false);
            fromUI();
            notifyValueChanged();
        });
    }

    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    @Override
    public Widget getDisplayWidget() {
        return editor;
    }

    /**
     * 将值更新到UI
     */
    @Override
    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }
        String value = "";
        Object obj = attribute.getValue();
        if (obj == null) {
            value = castToString(attribute.getDefaultValue());
        } else {
            value = (castToString(obj));
        }
        setPadding(value, false);
    }

    void setPadding(String value, boolean fireEvents) {
        if (StringUtil.isBlank(value)) {
            reset();
        } else {
            String[] vs = value.split(",");
            if (vs.length == 0) {
                reset();
            } else if (vs.length == 1) {
                paddings[0] = vs[0];
                paddings[1] = vs[0];
                paddings[2] = vs[0];
                paddings[3] = vs[0];
            } else if (vs.length == 2 || vs.length == 3) {
                paddings[0] = paddings[2] = vs[0];
                paddings[1] = paddings[3] = vs[1];
            } else {
                paddings[0] = vs[0];
                paddings[1] = vs[1];
                paddings[2] = vs[2];
                paddings[3] = vs[3];
            }
        }
        txtValue.setValue(formatValue(), fireEvents);
    }

    private String formatValue() {
        String sb = paddings[0] +
                "," +
                paddings[1] +
                "," +
                paddings[2] +
                "," +
                paddings[3];
        return sb;
    }

    private void reset() {
        paddings[0] = "0";
        paddings[1] = "0";
        paddings[2] = "0";
        paddings[3] = "0";
    }

    @Override
    public void fromUI() {
        if (getAttribute() != null) {
            getAttribute().setValue(formatValue());
        }
    }

    @UiHandler("btnSetup")
    public void btnSetupClick(ClickEvent event) {
        Popup<PaddingEditor> dialog = PaddingEditor.getDialog(true);
        dialog.addCommonHandler(event1 -> {
            if (event1.isValueChanged()) {
                String[] vs = event1.getValue();
                paddings[0] = vs[0];
                paddings[1] = vs[1];
                paddings[2] = vs[2];
                paddings[3] = vs[3];
                txtValue.setValue(formatValue(), false);
                fromUI();
                notifyValueChanged();
            }
        });
        dialog.getContent().setData(paddings);
        dialog.showRelativeTo(btnSetup);
    }

    interface PaddingBoxAttributeEditorUiBinder extends UiBinder<HTMLPanel, PaddingBoxAttributeEditor> {
    }
}