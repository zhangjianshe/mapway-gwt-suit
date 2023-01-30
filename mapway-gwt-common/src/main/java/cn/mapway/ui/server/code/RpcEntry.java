package cn.mapway.ui.server.code;

import java.lang.annotation.*;

import static cn.mapway.ui.client.rpc.JsonRpcBase.JSON_CONTENT_TYPE;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface RpcEntry {
    String path() default "";
    String method() default "POST";
    String contentType() default JSON_CONTENT_TYPE;
    String acceptType() default "";
}
