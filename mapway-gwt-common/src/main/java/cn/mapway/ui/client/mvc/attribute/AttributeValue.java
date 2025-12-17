package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.shared.CommonConstant;
import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;
import lombok.*;

import java.io.Serializable;

/**
 * AttributeValue
 * 描述一个属性的值
 *
 * @author zhang
 */
@Getter
@Setter
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

    public AttributeValue asFileFolder()
    {
        setEditCode(CommonConstant.EDITOR_FILE_DIR);
        return this;
    }


    public AttributeValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     *
     * @param name
     * @param altName
     * @param value
     * @param inputType
     * @param editCode
     * @param parameterType is type
     */
    public AttributeValue(String name, String altName,String value,Integer inputType,String editCode,Integer parameterType) {
        this.name = name;
        this.altName = altName;
        this.value = value;
        this.inputType = inputType;
        this.editCode = editCode;
        this.parameterType = parameterType;
    }
    public AttributeValue()
    {
    }
}
