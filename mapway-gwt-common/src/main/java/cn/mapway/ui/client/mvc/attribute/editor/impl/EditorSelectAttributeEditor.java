package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
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

import java.util.List;

/**
 * 属性编辑器选择 编辑器
 */
@AttributeEditor(value = EditorSelectAttributeEditor.EDITOR_CODE,
        name = "编辑器选择",
        group = IAttributeEditor.CATALOG_DESIGN,
        summary = "就是您正在使用的编辑器",
        author = "ZJS")
public class EditorSelectAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = "ATTR_SELECT_EDITOR";
    private static final EditorSelectAttributeEditorUiBinder ourUiBinder = GWT.create(EditorSelectAttributeEditorUiBinder.class);
    @UiField
    TextBox txtName;
    @UiField
    AiButton btnSelector;
    @UiField
    HTMLPanel box;

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
                AttributeEditorMetaData editorDesignOption = event1.getValue();
                getAttribute().setValue(editorDesignOption.toJSON());

                txtName.setValue(editorDesignOption.name);
            }
            popup.hide(true);
        });
        popup.showRelativeTo(box);
    }

    /**
     * 编辑器的数据应该是一个JSON字符串
     * {
     * "code":"ZJJSJSJ",
     * "options":{
     * ...
     * }
     * }
     *
     * @param editorOption 编辑器选项 是一个 KV Ma,继承的组件自己定义所需的参数
     * @param attribute    属性编辑器对应的属性内容
     */
    @Override
    public void setAttribute(EditorOption editorOption, IAttribute attribute) {
        super.setAttribute(editorOption, attribute);
        String value = DataCastor.castToString(attribute.getValue());

        AttributeEditorMetaData designOption = AttributeEditorMetaData.parse(value);

        AttributeEditorInfo editorInfo = findInfoByCode(designOption.code);
        if (editorInfo == null) {
            txtName.setValue(designOption.code + " Editor invalid.");
        } else {
            txtName.setValue(editorInfo.name);
        }
        //合并编辑器选项
        getEditorOption().merge(designOption.toEditorOption());

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