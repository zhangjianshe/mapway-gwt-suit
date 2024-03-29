package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.mvc.attribute.IAttributesProvider;

/**
 * 属性收集器
 *
 * @param <O>
 */
public interface AttributeCollector<O> {
    //创建一个属性手机器并代理 target目标 返回一个
    IAttributesProvider create(O target);
}
