package cn.mapway.ui.client.mvc.attribute.editor.design;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
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
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

import java.util.ArrayList;
import java.util.List;

public class DropdownListDesign extends Composite implements IEditorDesigner {

    private static final DropdownListDesignUiBinder ourUiBinder = GWT.create(DropdownListDesignUiBinder.class);
    @UiField
    HTMLPanel root;
    @UiField
    FontIcon btnPlus;
    @UiField
    HTMLPanel list;
    List<IAttribute> parameters;
    JsArray<DropdownListDesignData> designDataJsArray;

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
     * 获取参数设计器中的数据
     *
     * @return
     */
    @Override
    public List<IAttribute> getParameters() {
        return parameters;
    }

    /**
     * 获取编辑组件的参数数据
     *
     * @return
     */
    @Override
    public List<ParameterValue> getParameterValues() {
        List<ParameterValue> values = new ArrayList<ParameterValue>();
        for (int i = 0; i < list.getWidgetCount(); i++) {
            Widget widget = list.getWidget(i);
            if (widget instanceof ListDataItem) {
                DropdownListDesignData data = ((ListDataItem) widget).getData();
                ParameterValue parameterValue = new ParameterValue();
                parameterValue.name = data.key;
                parameterValue.value = data.value;
                values.add(parameterValue);
            }
        }
        return values;
    }

    /**
     * 初始化参数设计器
     *
     * @param title
     * @param parameters
     */
    @Override
    public void setParameters(String title, List<IAttribute> parameters) {
        this.parameters = parameters;
        // 不需要属性定义
    }

    /**
     * 上面两个方法 用于构造UI 这个方法用于更新数据 这个数据是保存在Editor中的
     *
     * @param parameterValues
     */
    @Override
    public void updateValue(List<ParameterValue> parameterValues) {
        if (parameterValues == null || parameterValues.size() == 0) {
            designDataJsArray = new JsArray<>();
        } else {
            ParameterValue attribute = findParameterValue(parameterValues, EditorOption.KEY_DROPDOWN_OPTIONS);
            if (attribute == null || attribute.value == null) {
                designDataJsArray = new JsArray<>();
            }
            try {
                designDataJsArray = Js.uncheckedCast(Global.JSON.parse((String) attribute.value));
            } catch (Exception e) {
                DomGlobal.console.info("Error parsing dropdown options " + attribute.value);
                designDataJsArray = new JsArray<>();
            }
        }

        toUI();
    }

    private ParameterValue findParameterValue(List<ParameterValue> parameters, String name) {
        if (parameters == null || name == null) {
            return null;
        }
        for (ParameterValue value : parameters) {
            if (name.equals(value.name)) {
                return value;
            }
        }
        return null;
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