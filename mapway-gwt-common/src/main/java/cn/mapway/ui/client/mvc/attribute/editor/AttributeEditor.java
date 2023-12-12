package cn.mapway.ui.client.mvc.attribute.editor;

/**
 * Mark a class which is a attributeEditor
 */
public @interface AttributeEditor {
    /**
     * 属性编辑器代码
     *
     * @return
     */
    String value() default "";


    /**
     * 属性编辑器名称
     *
     * @return
     */
    String group() default "";

    /**
     * 属性编辑器名称
     *
     * @return
     */
    String name() default "";

    /**
     * 属性编辑器说明
     *
     * @return
     */
    String summary() default "";

    /**
     * 属性编辑作者
     *
     * @return
     */
    String author() default "";
}
