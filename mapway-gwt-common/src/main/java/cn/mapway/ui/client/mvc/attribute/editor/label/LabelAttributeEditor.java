package cn.mapway.ui.client.mvc.attribute.editor.label;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

@AttributeEditor(value = LabelAttributeEditor.EDITOR_CODE,
        name = "只读标签",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "只读标签",
        author = "ZJS",
        icon = Fonts.LABEL
)
public class LabelAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "EDITOR_LABEL";
    Label label;

    public LabelAttributeEditor() {
        label = new Label();
        Style style = label.getElement().getStyle();
        style.setOverflow(Style.Overflow.HIDDEN);
        style.setWidth(100, Style.Unit.PCT);
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
        String v = DataCastor.castToString(getAttribute().getValue());
        Object object = option(ParameterKeys.KEY_AS_HTML,null);
        if (DataCastor.castToBoolean(object)) {
            label.getElement().setInnerHTML(v);
        } else {
            label.setText(v);
            label.setTitle(v);
        }
        Object height = option(ParameterKeys.KEY_HEIGHT,null);
        if (height != null) {
            String h = String.valueOf(height);
            label.setHeight(h);
            Style style = label.getElement().getStyle();
            style.setProperty("textWrap","auto");
            style.setOverflow(Style.Overflow.AUTO);
        }
    }

    @Override
    public void fromUI() {

    }
}
