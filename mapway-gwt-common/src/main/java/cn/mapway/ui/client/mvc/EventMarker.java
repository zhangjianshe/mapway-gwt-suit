package cn.mapway.ui.client.mvc;

import java.lang.annotation.Repeatable;

/**
 * 用于描述一个事件的信息
 */
@Repeatable(Events.class)
public @interface EventMarker {
    /**
     * 事件的代码 example onLoad
     *
     * @return
     */
    String code();

    /**
     * 事件名称 example 组件加载
     *
     * @return
     */
    String name();

    String summary() default "";

    String signature() default "";

    String group() default "";
}
