package cn.mapway.ui.client.mvc.attribute.editor.label;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.EditorCodes;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextboxAttributeEditor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
@AttributeEditor(value = TextboxAttributeEditor.EDITOR_CODE,
        name = "只读标签",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "只读标签",
        author = "ZJS",
        icon = Fonts.LABEL
)
public class LabelAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "EDITOR_LABEL";
    Label label;
    public LabelAttributeEditor()
    {
        label=new Label();
        Style style = label.getElement().getStyle();
        style.setOverflow(Style.Overflow.HIDDEN);
        style.setWidth(100, Style.Unit.PCT);
        style.setTextOverflow(Style.TextOverflow.CLIP);
    }
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    @Override
    public Widget getDisplayWidget() {
        return label;
    }

    @Override
    public void updateUI() {
        String v= DataCastor.castToString(getAttribute().getValue());
        label.setText(v);
    }

    @Override
    public void fromUI() {

    }
}
