package cn.mapway.ui.server.apt.jsstore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JsStoreTable
 *
 * @author zhang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JsStoreTable {
    /**
     * @return db Name
     */
    String dbName();

    /**
     * @return table Name
     * default is Class Name
     */
    String tableName();

}
