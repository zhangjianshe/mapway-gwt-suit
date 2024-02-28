package cn.mapway.spring.processor;

import java.lang.annotation.*;

/**
 *  注解在一个Method 上 或者一个类上 处理器将忽略改类或者 method
 *
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RpcIgnore {

}