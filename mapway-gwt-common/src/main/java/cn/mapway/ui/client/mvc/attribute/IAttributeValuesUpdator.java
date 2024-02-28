package cn.mapway.ui.client.mvc.attribute;

import java.util.List;

/**
 * 更新一组属性值到对象中
 */
public interface IAttributeValuesUpdator {

    /**
     * 更新属性的值
     *
     * @param values
     */
    void updateAttributeValues(List<AttributeValue> values);
}
