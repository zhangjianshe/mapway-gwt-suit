package cn.mapway.ui.client.mvc.attribute.editor.sys;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.SlideProperty;
import cn.mapway.ui.client.mvc.attribute.atts.SimpleCheckBoxAttribute;
import cn.mapway.ui.client.mvc.attribute.atts.SimpleTextBoxAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.common.CommonEditorParameterDesigner;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.widget.SliderEx;
import cn.mapway.ui.client.widget.Tip;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动条属性编辑器
 * 滑动条本身可以定义一些参数 SlideProperty
 * 滑动条输出的值 value为 sliderProperty中设定的值
 * 当代码初始化改组件的时候 可以传入滑动条的属性
 */
@AttributeEditor(value = SliderAttributeEditor.EDITOR_CODE,
        name = "滑动条选择",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "在一个范围内选择数据",
        icon = Fonts.SLIDER,
        author = "ZJS")
public class SliderAttributeEditor extends AbstractAttributeEditor<String> {

    public static final String EDITOR_CODE = "SLIDER_EDITOR";
    private static final SliderAttributeEditorUiBinder ourUiBinder = GWT.create(SliderAttributeEditorUiBinder.class);
    @UiField
    SliderEx sliderBox;
    CommonEditorParameterDesigner designer;

    public SliderAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        sliderBox.setTipLocation(Tip.LOCATION_BOTTOM);
        sliderBox.addValueChangeHandler(event -> {
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
        return sliderBox;
    }


    /**
     * {@link cn.mapway.ui.client.mvc.attribute.editor.inspector.ObjectInspector } 等使用组件的容器 会创建组件然后根据用户输入的
     * 组件属性property进行ui改变和设定 每个组件都有不同的逻辑 参数也不一样
     * 参数可能通过 zuian的MetaData定义(SlidereditorMetaData)
     */
    @Override
    public void updateUI() {
        IAttribute attribute = getAttribute();
        if (getAttribute().isReadonly()) {
            sliderBox.setEnabled(false);
        }
        if (getAttribute().getTip() != null) {
            sliderBox.setTitle(getAttribute().getTip());
        }
        try {
            SlideProperty slideProperty = new SlideProperty();
            slideProperty.min = DataCastor.castToDouble(option("min", "0.0"));
            slideProperty.max = DataCastor.castToDouble(option("max", "100.0"));
            slideProperty.step = DataCastor.castToDouble(option("step", "1.0"));
            slideProperty.unit = DataCastor.castToString(option("unit", ""));
            slideProperty.exponent = DataCastor.castToDouble(option("exponent", "0.0"));
            slideProperty.continueReport = DataCastor.castToBoolean(option("continueReport", "false"));

            sliderBox.setProperty(slideProperty);

        } catch (Exception e) {
            Logs.info(e.getMessage());
        }
        Object obj = attribute.getValue();
        // this is important
        if (obj == null) {
            obj = attribute.getDefaultValue();
        }
        sliderBox.setValue(DataCastor.castToDouble(obj));
    }

    /**
     * 滑动条属性编辑器的参数设计器
     * //     public double min;
     * //    public double max;
     * //    public double step;
     * //    public String unit;
     * //    public double exponent;
     * //    public Boolean continueReport;
     *
     * @return
     */
    @Override
    public IEditorDesigner getDesigner() {
        if (designer == null) {
            designer = new CommonEditorParameterDesigner();
            List<IAttribute> attributes = new ArrayList<>();
            attributes.add(new SimpleTextBoxAttribute("min", "最小值", "0"));
            attributes.add(new SimpleTextBoxAttribute("max", "最大值", "100"));
            attributes.add(new SimpleTextBoxAttribute("step", "步长", "1"));
            attributes.add(new SimpleTextBoxAttribute("unit", "单位", ""));
            attributes.add(new SimpleTextBoxAttribute("exponent", "滑动指数", "1"));
            attributes.add(new SimpleCheckBoxAttribute("continueReport", "持续事件通知", false));
            designer.setParameters("滑动条参数", attributes);
        }
        return designer;
    }

    @Override
    public void fromUI() {
        if (getAttribute() != null) {
            getAttribute().setValue(sliderBox.getValue());
        }
    }


    interface SliderAttributeEditorUiBinder extends UiBinder<HTMLPanel, SliderAttributeEditor> {
    }
}