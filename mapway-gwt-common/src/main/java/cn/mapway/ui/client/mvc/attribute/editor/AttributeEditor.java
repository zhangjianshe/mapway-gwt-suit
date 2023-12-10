package cn.mapway.ui.client.mvc.attribute.editor;

/**
 * Mark a class which is a attributeEditor
 */
public @interface AttributeEditor {
    String value() default "";
}
