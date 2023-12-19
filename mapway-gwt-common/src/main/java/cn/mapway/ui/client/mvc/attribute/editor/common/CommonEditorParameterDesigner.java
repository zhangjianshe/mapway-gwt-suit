package cn.mapway.ui.client.mvc.attribute.editor.common;

import cn.mapway.ui.client.mvc.attribute.AbstractAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import cn.mapway.ui.client.mvc.attribute.editor.inspector.SimpleObjectInspector;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用编辑器参数设计器
 */
public class CommonEditorParameterDesigner extends Composite implements IEditorDesigner {
    private static final CommonEditorParameterDesignerUiBinder ourUiBinder = GWT.create(CommonEditorParameterDesignerUiBinder.class);
    @UiField
    SimpleObjectInspector designer;

    public CommonEditorParameterDesigner() {
        initWidget(ourUiBinder.createAndBindUi(this));
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

    /**
     * 获取参数设计器中的数据
     *
     * @return
     */
    @Override
    public List<IAttribute> getParameters() {
        return designer.getData().getAttributes();
    }

    /**
     * 获取编辑组件的参数数据
     *
     * @return
     */
    @Override
    public List<ParameterValue> getParameterValues() {
        if (getParameters() == null || getParameters().size() == 0) {
            return new ArrayList();
        }
        List<ParameterValue> values = new ArrayList<>();
        for (IAttribute attribute : getParameters()) {
            values.add(ParameterValue.create(attribute.getName(), attribute.getValue()));
        }
        return values;
    }

    /**
     * 初始化参数设计器
     *
     * @param parameters
     */
    @Override
    public void setParameters(String title, List<IAttribute> parameters) {
        AbstractAttributesProvider attributesProvider = new AbstractAttributesProvider(title) {
        };
        if (parameters != null) {
            attributesProvider.getAttributes().addAll(parameters);
        }
        designer.setData(attributesProvider);
    }

    /**
     * 上面两个方法 用于构造UI 这个方法用于更新数据 这个数据是保存在Editor中的
     *
     * @param parameterValues
     */
    @Override
    public void updateValue(List<ParameterValue> parameterValues) {
        if (parameterValues == null || parameterValues.size() == 0) {
            return;
        }
        //参谋数值转换为 AttributeValues
        List<AttributeValue> values = new ArrayList<>(parameterValues.size());
        for (ParameterValue value : parameterValues) {
            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setName(value.name);
            attributeValue.setValue((String) value.value);
            values.add(attributeValue);
        }
        //所有参数数据更新完毕
        designer.updateValue(values);

    }


    interface CommonEditorParameterDesignerUiBinder extends UiBinder<HTMLPanel, CommonEditorParameterDesigner> {
    }
}