package cn.mapway.ui.client.mvc.attribute;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AttributeValue
 * 描述一个属性的值
 *
 * @author zhang
 */
public class AttributeValue implements Serializable, IsSerializable {
    String name;
    String altName;
    String value;
    @Deprecated
    Integer inputType;
    /**
     * 新增 编辑器代码 代替 inputType,inputType will be removed
     */
    String editCode;
    /**
     * @See ParameterTypeEnum
     */
    Integer parameterType;

    public AttributeValue(String name, String value) {
        this.name = name;
        this.value = value;
    }
    public AttributeValue()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        this.inputType = inputType;
    }

    public String getEditCode() {
        return editCode;
    }

    public void setEditCode(String editCode) {
        this.editCode = editCode;
    }

    public Integer getParameterType() {
        return parameterType;
    }

    public void setParameterType(Integer parameterType) {
        this.parameterType = parameterType;
    }
}
