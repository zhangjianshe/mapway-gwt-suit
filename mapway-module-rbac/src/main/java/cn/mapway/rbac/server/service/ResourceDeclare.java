package cn.mapway.rbac.server.service;


import cn.mapway.rbac.shared.ResourceKind;

import java.lang.annotation.*;

/**
 * 资源点声明
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResourceDeclare {
    String catalog() default "";

    String name();

    String code();

    String summary() default "";

    ResourceKind kind() default ResourceKind.RESOURCE_KIND_SYSTEM;

    String data() default "";
}
