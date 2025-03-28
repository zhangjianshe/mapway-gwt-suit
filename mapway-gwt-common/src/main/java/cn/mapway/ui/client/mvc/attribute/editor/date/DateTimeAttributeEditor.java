package cn.mapway.ui.client.mvc.attribute.editor.date;

import cn.mapway.ui.client.db.DbFieldType;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import cn.mapway.ui.client.mvc.attribute.editor.ParameterKeys;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.common.CommonEditorParameterDesigner;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AttributeEditor(value = DateTimeAttributeEditor.EDITOR_CODE,
        name = "日期时间",
        group = IAttributeEditor.CATALOG_SYSTEM,
        summary = "日期编辑",
        icon = Fonts.CALENDAR,
        author = "ZJS")
public class DateTimeAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "DATETIME_EDITOR";
    private static final DateTimeAttributeEditorUiBinder ourUiBinder = GWT.create(DateTimeAttributeEditorUiBinder.class);
    @UiField
    DateBox dateBox;
    CommonEditorParameterDesigner designer;

    public DateTimeAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateBox.addValueChangeHandler(event -> {
            fromUI();
        });
    }

    /**
     * @return
     */
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    /**
     * @return
     */
    @Override
    public Widget getDisplayWidget() {
        return dateBox;
    }

    /**
     *
     */
    @Override
    public void updateUI() {
        IAttribute attribute = getAttribute();
        dateBox.setEnabled(!getAttribute().isReadonly());
        String format = option(ParameterKeys.KEY_DATETIME_FORMAT, "yyyy-MM-dd HH:mm:ss");
        dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(format)));

        Object obj = attribute.getValue();
        if (obj instanceof String) {
            dateBox.setValue(DataCastor.castToDate(obj, format));
        } else if (obj instanceof Date) {
            dateBox.setValue((Date) obj);
        }
    }

    /**
     *
     */
    @Override
    public void fromUI() {
        if (getAttribute() != null) {
            Date date = dateBox.getValue();
            DbFieldType fieldType = DbFieldType.valueOfCode(getAttribute().getDataType());

            switch (fieldType) {
                case FLD_TYPE_DATETIME:
                    getAttribute().setValue(date);
                    break;
                case FLD_TYPE_BIGINTEGER:
                    getAttribute().setValue(date.getTime());
                    break;
                case FLD_TYPE_STRING:
                default:
                    String format = option(ParameterKeys.KEY_DATETIME_FORMAT, "yyyy-MM-dd HH:mm:ss");
                    String value = StringUtil.formatDate(date, format);
                    getAttribute().setValue(value);
                    break;
            }


        }
    }

    @Override
    public IEditorDesigner getDesigner() {
        if (designer == null) {
            designer = new CommonEditorParameterDesigner();

            List<IAttribute> attributes = new ArrayList<>();
            attributes.add(new ParameterAttribute(ParameterKeys.KEY_DATETIME_FORMAT, "日期时间格式", "yyyy-MM-dd HH:mm:ss"));
            // designer need the blow parameters.
            designer.setParameters("日期时间编辑器参数设置", attributes);
        }
        return designer;
    }

    interface DateTimeAttributeEditorUiBinder extends UiBinder<HTMLPanel, DateTimeAttributeEditor> {
    }
}