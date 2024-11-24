package cn.mapway.ui.client.mvc.attribute.editor.sys;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

// TEXTAREA_EDITOR 这个值不能变 系统会使用这个值
@AttributeEditor(value = TextAreaAttributeEditor.EDITOR_CODE,
        name = "多行文本编辑",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "编辑多行文本",
        icon = Fonts.LOG,
        author = "ZJS")
public class TextAreaAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "TEXTAREA_EDITOR";
    private static final TextAreaAttributeEditorUiBinder ourUiBinder = GWT.create(TextAreaAttributeEditorUiBinder.class);
    @UiField
    TextArea txtArea;
    ////////////////////////////////  designer //////////////////////////////////////////////
    CommonEditorParameterDesigner designer;

    public TextAreaAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        txtArea.addValueChangeHandler(event -> {
            getAttribute().setValue(event.getValue());
        });
    }

    /**
     * @return
     */
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    @Override
    public Widget getDisplayWidget() {
        return txtArea;
    }



    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (getAttribute().isReadonly()) {
            txtArea.setReadOnly(true);
        }
        if (getAttribute().getTip() != null) {
            txtArea.getElement().setAttribute("placeholder", attribute.getTip());
        }
        Object obj = attribute.getValue();
        txtArea.setValue(DataCastor.castToString(obj));
        txtArea.setHeight(option(ParameterKeys.KEY_HEIGHT, "70px"));
    }


    @Override
    public void fromUI() {
        if (getAttribute() != null) {
            getAttribute().setValue(txtArea.getValue());
        }
    }

    /**
     * 设计的时候决定了需要什么参数
     * @return
     */
    @Override
    public IEditorDesigner getDesigner() {
        if (designer == null) {
            designer = new CommonEditorParameterDesigner();

            List<IAttribute> attributes=new ArrayList<>();
            attributes.add(new ParameterAttribute(ParameterKeys.KEY_HEIGHT,"高度","70px"));
            // designer need the blow parameters.
            designer.setParameters("多行文本编辑器参数设置", attributes);
        }
        return designer;
    }

    interface TextAreaAttributeEditorUiBinder extends UiBinder<HTMLPanel, TextAreaAttributeEditor> {
    }
}