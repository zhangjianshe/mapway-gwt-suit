package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.db.DbFieldType;
import cn.mapway.ui.client.mvc.attribute.editor.impl.TextboxAttributeEditor;

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

    String editCode() default TextboxAttributeEditor.EDITOR_CODE;

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
     * 为属性提供一个图标
     *
     * @return
     */
    String icon() default "";

    /**
     * 最小值
     *
     * @return
     */
    double min() default 0;

    /**
     * 最大值
     *
     * @return
     */
    double max() default 100;

    //某种参数自定义的选项 一般为json字符串 由使用者解释
    String options() default "";

    /**
     * 编辑器属性 JSON serial String
     *
     * @return
     */
    String editorOptions() default "{}";
}
