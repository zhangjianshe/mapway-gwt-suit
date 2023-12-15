package cn.mapway.ui.client.mvc.attribute;


import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;
import elemental2.core.JsObject;

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
     * 获取设计时期的组件选项
     * 解析为 JsObject 数据
     *
     * @return AttributeEditorMetaData
     */
    JsObject getDesignOption();


    /**
     * 获取运行时期 的参数
     *
     * @return
     */
    default EditorOption getRuntimeOption() {
        return new EditorOption();
    }


    /**
     * 是否初始化显示
     *
     * @return
     */
    default boolean isInitVisible() {
        return true;
    }

    /**
     * 编辑器组件的 代码
     * 可以通过这个方法获取创建组件需要的必要数据  编辑器代码
     *
     * @return
     */
    String getEditorCode();



}
