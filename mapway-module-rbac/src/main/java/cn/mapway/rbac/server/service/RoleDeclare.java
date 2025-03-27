package cn.mapway.rbac.server.service;


import java.lang.annotation.*;

/**
 * 角色声明
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoleDeclare {

    String name();

    String code();

    String summary() default "";

    String parentCode() default "";

}
