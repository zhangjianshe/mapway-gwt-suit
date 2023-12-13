package cn.mapway.ui.client.mvc.attribute.editor.design;

import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.core.Global;
import elemental2.core.JsArray;
import jsinterop.base.Js;

public class DropdownListDesign extends Composite implements IEditorDesigner {


    private static final DropdownListDesignUiBinder ourUiBinder = GWT.create(DropdownListDesignUiBinder.class);
    JsArray<DropdownListDesignData> designDataJsArray;
    @UiField
    HTMLPanel root;

    public DropdownListDesign() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /**
     * 参数的编辑UI
     *
     * @return
     */
    @Override
    public Widget getDesignRoot() {
        return getWidget();
    }

    /**
     * 解析参数
     *
     * @param options
     */
    @Override
    public void parseDesignOptions(String options) {
        try {
            designDataJsArray = Js.uncheckedCast(Global.JSON.parse(options));
        } catch (Exception e) {
            designDataJsArray = new JsArray<>();
        }
        toUI();
    }

    private void toUI() {
        root.clear();
        for (int i = 0; i < designDataJsArray.length; i++) {
            ListDataItem item = new ListDataItem();
            item.setData(designDataJsArray.getAt(i));
            root.add(item);
        }
    }

    /**
     * 将设计的参数数据转化为字符串 JSON
     *
     * @return
     */
    @Override
    public String toDesignOptions() {
        if (designDataJsArray == null) {
            return "[]";
        } else {
            return Global.JSON.stringify(designDataJsArray);
        }
    }

    interface DropdownListDesignUiBinder extends UiBinder<HTMLPanel, DropdownListDesign> {
    }
}