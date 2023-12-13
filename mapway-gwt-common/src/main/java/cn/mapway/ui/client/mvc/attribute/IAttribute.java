package cn.mapway.ui.client.mvc.attribute;


import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorMetaData;

/**
 * IAttribute
 * 用于描述一个属性
 *
 * @author zhang
 */
public interface IAttribute {

    /**
     * 唯一ID
     *
     * @return
     */
    String getId();

    String getName();

    String getAltName();

    Object getValue();

    void setValue(Object value);

    String getDefaultValue();

    String getDescription();

    boolean isReadonly();

    boolean isRequired();

    int getDataType();

    String getValidateRegx();

    String getGroup();

    int getRank();

    String getTip();

    String getErrorTip();

    String getIcon();

    //某种参数自定义的选项 一般为json字符串 由使用者解释
    String getOptions();

    IOptionProvider getOptionProvider();//选项提供者


    /**
     * 属性对应编辑器的元数据
     * 解析为 AttributeEditorMetaData 数据
     *
     * @return AttributeEditorMetaData
     */
    AttributeEditorMetaData getEditorMetaData();

    /**
     * 是否初始化显示
     *
     * @return
     */
    default boolean isInitVisible() {
        return true;
    }

}
