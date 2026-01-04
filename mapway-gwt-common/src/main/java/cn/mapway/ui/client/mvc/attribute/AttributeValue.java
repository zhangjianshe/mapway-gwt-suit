package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.mvc.attribute.marker.ParameterTypeEnum;
import cn.mapway.ui.shared.CommonConstant;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Getter;
import lombok.Setter;

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

    /**
     * 当作为　参数在流程图中进行属性传递过程中　该值表示　从上一个节点传递过来的参数映射
     * 对应　上一个节点的参数Name
     * 这个字段的格式为nodeId.paramName 如果没有nodeId.就表示从上一个节点
     * 如何使用这个字段　当启动算法模型进行计算的时候　就会根据此值进行查询并赋值
     */
    String mapFrom;
    public AttributeValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param name
     * @param altName
     * @param value
     * @param inputType
     * @param editCode
     * @param parameterType is type
     */
    public AttributeValue(String name, String altName, String value, Integer inputType, String editCode, Integer parameterType) {
        this.name = name;
        this.altName = altName;
        this.value = value;
        this.inputType = inputType;
        this.editCode = editCode;
        this.parameterType = parameterType;
    }

    public AttributeValue() {
    }

    public AttributeValue asFileFolder() {
        setEditCode(CommonConstant.EDITOR_FILE_DIR);
        return this;
    }

    public AttributeValue asRunningParameter() {
        parameterType = ParameterTypeEnum.PT_RUNNING.getCode();
        return this;
    }

    public AttributeValue asInputParameter() {
        parameterType = ParameterTypeEnum.PT_INPUT.getCode();
        return this;
    }

    public AttributeValue asOutputParameter() {
        parameterType = ParameterTypeEnum.PT_OUTPUT.getCode();
        return this;
    }
}
