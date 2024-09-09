package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.mvc.attribute.design.ParameterValue;

import java.util.ArrayList;
import java.util.List;

public class AttrEditorMetaData {
    List<ParameterValue> parameters;
    String editorCode;
    public AttrEditorMetaData()
    {
        parameters=new ArrayList<>();
        editorCode="";
    }

    public void setEditorCode(String code)
    {
        editorCode=code;
    }
    public List<ParameterValue> getParameters()
    {
        return parameters;
    }
}
