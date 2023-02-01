package cn.mapway.ui.server.code;

import java.lang.annotation.*;

/**
 * 描述一个Json RpcEntry入口
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface RpcProxy {
    /**
     * http API接口的基地址
     * @return
     */
    String url() default "";
    String packageName() default "";
    String className() default "";
    boolean enabled() default true;
}
