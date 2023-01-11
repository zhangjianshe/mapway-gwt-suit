package cn.mapway.ui.client.mvc.decorator;

/**
 * WindowDecorator
 *
 * @author zhang
 */
public @interface WindowDecorator {
    String value();
    String iconUnicode() default "";
    String summary() default "";
}
