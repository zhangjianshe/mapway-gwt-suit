package cn.mapway.ui.client.mvc.attribute.editor.sys;

import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;
import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

public class SliderEditorMetaData extends AbstractEditorMetaData {
    /**
     *
     */
    @Override
    protected void initMetaData() {
        setEditorCode(SliderAttributeEditor.EDITOR_CODE);
    }

    public SliderEditorMetaData setData(double min, double max, double step, String unit, boolean continueReport) {
        getParameterValues().add(ParameterValue.create("min", min));
        getParameterValues().add(ParameterValue.create("max", max));
        getParameterValues().add(ParameterValue.create("step", step));
        getParameterValues().add(ParameterValue.create("unit", unit));
        getParameterValues().add(ParameterValue.create("continueReport", continueReport));
        return this;
    }

}
