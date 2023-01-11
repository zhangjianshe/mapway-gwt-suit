package cn.mapway.ui.client.mvc.attribute;

import java.util.List;

/**
 * IAttributeProvider
 * 属性提供接口 用来描述一个对象的属性
 * 一个对象可以有多个属性 IAttribute
 *
 * @author zhang
 */
public interface IAttributeProvider {
    String getAttributeTitle();

    /**
     * 获取属性
     *
     * @return 属性列表
     */
    List<IAttribute> getAttributes();

    /**
     * 获取属性的描述
     *
     * @return 属性组列表
     */
    String getAttributeSummary();

    /**
     * 持久化属性 兼容 ISaveable
     */
    void commit();

    /**
     * 将属性变为哈希表
     *
     * @return
     */
    List<AttributeValue> flatten();


    /**
     * 设置属性 准备好后的回调
     *
     * @param callback
     */
    void addAttributeReadyCallback(IAttributeReadyCallback callback);

    /**
     * 移除属性准备好的回调
     *
     * @param callback
     */
    void removeAttributeReadyCallback(IAttributeReadyCallback callback);

    /**
     * 通知属性变更完成
     */
    void notifyAttributeReady();

    /**
     * 验证用户的输入数据
     * 返回错误的描述信息
     *
     * @return
     */
    List<String> isValidate();

}
