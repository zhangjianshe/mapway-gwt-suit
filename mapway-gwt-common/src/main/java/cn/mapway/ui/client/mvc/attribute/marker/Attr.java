package cn.mapway.ui.client.mvc.attribute.marker;

import cn.mapway.ui.client.db.DbFieldType;
import cn.mapway.ui.client.mvc.attribute.editor.textbox.TextBoxEditorMetaData;

import java.lang.annotation.*;

/**
 * 为一个组件的属性进行注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Attr {
    /**
     * 属性的显示名字
     *
     * @return
     */
    String displayName() default "";

    DbFieldType dataType() default DbFieldType.FLD_TYPE_STRING;


    /**
     * 属性的缺省值
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 属性的描述
     *
     * @return
     */
    String description() default "";


    /**
     * 是否只读 default is false
     *
     * @return
     */
    boolean readonly() default false;


    String validateRegx() default "";

    String group() default "";

    int rank() default 0;

    /**
     * 输入的提示信息
     *
     * @return
     */
    String tip() default "";

    /**
     * 输入错误的时候提示
     */
    String errorTip() default "";

    /**
     * 是否显示
     * @return
     */
    boolean visible() default  true;

    /**
     * 为属性提供一个图标
     *
     * @return
     */
    String icon() default "";

    /**
     * input output running ParameterTypeEnum
     *
     * @return
     */
    ParameterTypeEnum type() default ParameterTypeEnum.PT_INPUT;

    /**
     * 通过一个类提供编辑器实例化信息
     *
     * @return
     */
    Class<? extends AbstractEditorMetaData> editor() default TextBoxEditorMetaData.class;
}
