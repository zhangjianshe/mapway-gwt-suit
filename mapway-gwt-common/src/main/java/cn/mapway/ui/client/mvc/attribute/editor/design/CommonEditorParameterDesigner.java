package cn.mapway.ui.client.mvc.attribute.editor.design;

import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import cn.mapway.ui.client.mvc.attribute.simple.SimpleAttributeEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.core.Global;
import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import java.util.List;

/**
 * 通用编辑器参数设计器
 */
public class CommonEditorParameterDesigner extends Composite implements IEditorDesigner {
    private static final CommonEditorParameterDesignerUiBinder ourUiBinder = GWT.create(CommonEditorParameterDesignerUiBinder.class);
    @UiField
    SimpleAttributeEditor designer;

    public CommonEditorParameterDesigner(IAttributesProvider provider) {
        initWidget(ourUiBinder.createAndBindUi(this));
        designer.setData(provider);
    }


    /**
     * 参数的编辑UI
     *
     * @return
     */
    @Override
    public Widget getDesignRoot() {
        return designer;
    }

    @Override
    public String getDesignOptions() {
        List<AttributeValue> flatten = designer.getData().flatten();
        JsPropertyMap propertyMap = JsPropertyMap.of();
        for (AttributeValue attributeValue : flatten) {
            String key = attributeValue.getName();
            propertyMap.set(key, attributeValue.getValue());
        }
        return Global.JSON.stringify(propertyMap);
    }

    @Override
    public void setDesignOptions(JsObject designOptions) {
        if (designOptions == null) {
            return;
        }

        JsPropertyMap propertyMap = Js.asPropertyMap(designOptions);

        List<AttributeValue> flatten = designer.getData().flatten();
        for (AttributeValue attributeValue : flatten) {
            String key = attributeValue.getName();
            Object obj = propertyMap.get(key);
            attributeValue.setValue(DataCastor.castToString(obj));
        }
        //设置属性值
        designer.updateValue(flatten);
    }


    interface CommonEditorParameterDesignerUiBinder extends UiBinder<HTMLPanel, CommonEditorParameterDesigner> {
    }
}