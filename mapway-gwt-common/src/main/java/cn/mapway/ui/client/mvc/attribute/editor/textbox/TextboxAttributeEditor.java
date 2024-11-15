package cn.mapway.ui.client.mvc.attribute.editor.textbox;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValues;
import cn.mapway.ui.client.mvc.attribute.editor.*;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 文本框编辑器
 */
@AttributeEditor(value = TextboxAttributeEditor.EDITOR_CODE,
        name = "单行文本编辑器",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "文本输入",
        author = "ZJS",
        icon = Fonts.RENAME
)
public class TextboxAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = EditorCodes.EDITOR_TEXTBOX;
    //输入类型
    public static final String KEY_INPUT_KIND="input_kind";
    private static final TextboxAttributeEditorUiBinder ourUiBinder = GWT.create(TextboxAttributeEditorUiBinder.class);
    @UiField
    TextBox txtBox;
    TextInputKind textInputKind;
    HandlerRegistration register;
    private MouseWheelHandler wheelHandler=new MouseWheelHandler() {
        @Override
        public void onMouseWheel(MouseWheelEvent event) {
            event.preventDefault();
            event.stopPropagation();
            String value=txtBox.getValue();
            if(StringUtil.isBlank(value))
            {
                value="0";
            }
            double step=1.0;
            double v=Double.parseDouble(value);
            boolean isFloat=false;
            if(value.contains("."))
            {
                isFloat=true;
                step=0.1;
            }

            if (event.isNorth()) {
                v+=step;
            } else {
                v-=step;
            }
            if(isFloat) {
                txtBox.setValue(String.valueOf(v),true);
            }
            else {
                txtBox.setValue(String.valueOf((int)v),true);
            }
        }
    };

    private void clearWheelHandler()
    {
        if(register!=null)
        {
            register.removeHandler();
            register=null;
        }
    }
    public void setTextInputKind(TextInputKind kind) {
        this.textInputKind = kind;
        clearWheelHandler();
        switch (kind) {
            case EMAIL:
                txtBox.getElement().setAttribute("type","email");
                break;
            case NUMBER:
                txtBox.getElement().setAttribute("type","number");
                register = txtBox.addMouseWheelHandler(wheelHandler);
                break;
            case DATE:
                txtBox.getElement().setAttribute("type","date");
                break;
            case URL:
                txtBox.getElement().setAttribute("type","url");
                break;
            case TIME:
                txtBox.getElement().setAttribute("type","time");
                break;
            case DATE_TIME:
                txtBox.getElement().setAttribute("type","datetime-local");
            case TEXT:
            default:
                txtBox.getElement().setAttribute("type","text");
        }
    }

    public TextboxAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        txtBox.addChangeHandler(event -> {
            if (getAttribute() != null) {
                getAttribute().setValue(castToValue(txtBox.getValue()));
            }
        });
    }


    /**
     * 编辑器的唯一识别代码
     *
     * @return
     */
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }


    @Override
    public Widget getDisplayWidget() {
        return txtBox;
    }



    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }
         Object inputKind= option(KEY_INPUT_KIND,null);
        if(inputKind  instanceof TextInputKind)
        {
            setTextInputKind((TextInputKind)inputKind);
        }
        else if(inputKind instanceof String)
        {
            setTextInputKind(TextInputKind.valueOfName((String)inputKind));
        }

        if (getAttribute().isReadonly()) {
            txtBox.setReadOnly(true);
        }
        if (getAttribute().getTip() != null) {
            txtBox.getElement().setAttribute("placeholder", getAttribute().getTip());
        }
        Object obj = attribute.getValue();
        if (obj == null) {
            txtBox.setValue(castToString(attribute.getDefaultValue()));
        } else {
            txtBox.setValue(castToString(obj));
        }
    }

    @Override
    public void fromUI() {
        IAttribute attribute = getAttribute();
        if (attribute != null && !attribute.isReadonly()) {
            getAttribute().setValue(txtBox.getValue());
        }
    }

    interface TextboxAttributeEditorUiBinder extends UiBinder<HTMLPanel, TextboxAttributeEditor> {
    }
}
