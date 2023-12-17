package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.EditorMetaMetaData;
import cn.mapway.ui.client.mvc.attribute.design.EditorDataFormat;
import cn.mapway.ui.client.mvc.attribute.editor.*;
import cn.mapway.ui.client.widget.buttons.AiButton;
import cn.mapway.ui.client.widget.dialog.Popup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;

import java.util.List;

/**
 * 属性编辑器选择 编辑器
 */
@AttributeEditor(value = EditorSelectAttributeEditor.EDITOR_CODE,
        name = "编辑器选择",
        group = IAttributeEditor.CATALOG_DESIGN,
        summary = "就是您正在使用的编辑器",
        author = "ZJS",
        icon = Fonts.XUANZE
)
public class EditorSelectAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "ATTR_SELECT_EDITOR";
    private static final EditorSelectAttributeEditorUiBinder ourUiBinder = GWT.create(EditorSelectAttributeEditorUiBinder.class);
    @UiField
    TextBox txtName;
    @UiField
    AiButton btnSelector;
    @UiField
    HTMLPanel box;
    EditorMetaMetaData editorMetaData;

    public EditorSelectAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
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

    /**
     * 显示显示面板
     *
     * @return
     */
    @Override
    public Widget getDisplayWidget() {
        return box;
    }

    @UiHandler("btnSelector")
    public void btnSelectorClick(ClickEvent event) {
        Popup<EditorSelector> popup = EditorSelector.getPopup(true);
        popup.addCommonHandler(event1 -> {
            if (event1.isOk()) {
                if (getAttribute() != null) {
                    //这个是保存在 属性定义的 编辑器字段中的数据
                    editorMetaData = event1.getValue();
                    txtName.setValue(editorMetaData.getEditorName());
                    getAttribute().setValue(editorMetaData.save(EditorDataFormat.EDF_JSON));
                }
            }
            popup.hide(true);
        });
        popup.getContent().editorValue(editorMetaData);
        popup.showRelativeTo(box);
    }

    /**
     * 编辑器的数据应该是一个JSON字符串
     *
     * @param runtimeOption 编辑器选项 是一个 KV Ma,继承的组件自己定义所需的参数
     * @param attribute     属性编辑器对应的属性内容
     */
    @Override
    public void setAttribute(EditorOption runtimeOption, IAttribute attribute) {
        super.setAttribute(runtimeOption, attribute);

        updateUI();
    }

    @Override
    public void updateUI() {
        if (getAttribute() == null) {
            return;
        }
        editorMetaData = new EditorMetaMetaData();
        editorMetaData.load(getAttribute().getValue(), EditorDataFormat.EDF_JSON);

        AttributeEditorInfo info;

        String editorCode = editorMetaData.getEditorCode();

        if (editorCode == null || editorCode.length() == 0) {
            editorCode = TextboxAttributeEditor.EDITOR_CODE;
        }
        info = findInfoByCode(editorCode);
        if (info == null) {
            DomGlobal.console.error("load editor data error in Editor SelectAttributeEditor.");
        } else {
            txtName.setValue(info.name);
            editorMetaData.setEditorCode(info.code);
            editorMetaData.setEditorName(info.name);
        }
    }

    AttributeEditorInfo findInfoByCode(String code) {
        List<AttributeEditorInfo> editors = AttributeEditorFactory.get().getEditors();
        for (AttributeEditorInfo info : editors) {
            if (info.code.equals(code)) {
                return info;
            }
        }
        return null;
    }

    interface EditorSelectAttributeEditorUiBinder extends UiBinder<HTMLPanel, EditorSelectAttributeEditor> {
    }
}