package cn.mapway.ui.client.mvc.attribute;


import cn.mapway.ui.client.mvc.attribute.design.IEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.EditorOption;

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


    IOptionProvider getOptionProvider();//选项提供者


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
     * 编辑这个属性需要的属性编辑器信息 将会替代 上面的getEditorCode designOption getOptions等字段
     * 完成后 上面的字段将会被删除
     *
     * @return
     */
    IEditorMetaData getEditorMetaData();

}
