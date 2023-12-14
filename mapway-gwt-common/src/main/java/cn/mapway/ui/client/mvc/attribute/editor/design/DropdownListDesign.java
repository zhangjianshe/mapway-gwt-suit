package cn.mapway.ui.client.mvc.attribute.editor.design;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import jsinterop.base.Js;

public class DropdownListDesign extends Composite implements IEditorDesigner {

    private static final DropdownListDesignUiBinder ourUiBinder = GWT.create(DropdownListDesignUiBinder.class);
    JsArray<DropdownListDesignData> designDataJsArray;
    @UiField
    HTMLPanel root;
    @UiField
    FontIcon btnPlus;
    @UiField
    HTMLPanel list;

    public DropdownListDesign() {
        initWidget(ourUiBinder.createAndBindUi(this));
        btnPlus.setIconUnicode(Fonts.PLUS);

    }

    /**
     * 参数的编辑UI
     *
     * @return
     */
    @Override
    public Widget getDesignRoot() {
        return root;
    }

    /**
     * 将设计的参数数据转化为字符串 JSON
     *
     * @return
     */
    @Override
    public String getDesignOptions() {
        if (designDataJsArray == null) {
            return "[]";
        } else {
            return Global.JSON.stringify(designDataJsArray);
        }
    }

    /**
     * 解析参数
     *
     * @param designOptions
     */
    @Override
    public void setDesignOptions(JsObject designOptions) {
        try {
            designDataJsArray = Js.uncheckedCast(designOptions);
        } catch (Exception e) {
            designDataJsArray = new JsArray<>();
        }
        toUI();
    }

    private void toUI() {
        list.clear();
        for (int i = 0; i < designDataJsArray.length; i++) {
            ListDataItem item = new ListDataItem();
            item.setData(designDataJsArray.getAt(i));
            list.add(item);
        }
    }


    @UiHandler("btnPlus")
    public void btnPlusClick(ClickEvent event) {
        if (designDataJsArray == null) {
            designDataJsArray = new JsArray<>();
        }
        DropdownListDesignData item = new DropdownListDesignData();
        item.key = "key";
        item.value = "value";
        item.init = false;
        designDataJsArray.push(item);
        toUI();
    }

    interface DropdownListDesignUiBinder extends UiBinder<HTMLPanel, DropdownListDesign> {
    }
}