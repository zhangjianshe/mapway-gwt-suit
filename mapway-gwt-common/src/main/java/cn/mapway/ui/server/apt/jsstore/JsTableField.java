package cn.mapway.ui.server.apt.jsstore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JsTableField
 *
 * @author zhang
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface JsTableField {
    boolean primaryKey() default false;

    boolean autoIncrement() default false;

    boolean notNull() default true;

    JsFieldType dataType() default JsFieldType.String;
}
