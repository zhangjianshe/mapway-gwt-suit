package cn.mapway.rbac.server.service;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ResourceDeclares {
    ResourceDeclare[] value();
}
