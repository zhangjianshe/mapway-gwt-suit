package cn.mapway.ui.client.mvc.attribute.design;

import java.util.List;

/**
 * 描述一个属性编辑器 IAttributeEditor 创建时所需要的所有信息
 * 此信息可以被序列化到持久化格式中 （JSON|XML|BYTE|ELSE）
 * 编辑器实例化的时候必须需要这样一个对象
 */
public interface IEditorMetaData {
    /**
     * 获取编辑器的代码
     *
     * @return
     */
    String getEditorCode();

    /**
     * 编辑器名称
     *
     * @return
     */
    String getEditorName();

    /**
     * 获取错误消息
     *
     * @return
     */
    String getErrorMessage();

    /**
     * Version1 only support JSON format
     *
     * @param data
     * @param format
     * @return
     */
    boolean load(Object data, EditorMetaDataFormat format);

    /**
     * 系列化数据到字符串中
     *
     * @param format
     * @return
     */
    Object save(EditorMetaDataFormat format);


    /**
     * 获取创建编辑器所需要的参数列表
     *
     * @return
     */
    List<ParameterValue> getParameterValues();


    /**
     * 查找 [key] 的参数信息
     *
     * @param key
     * @return
     */
    ParameterValue findParameterValue(String key);



}
