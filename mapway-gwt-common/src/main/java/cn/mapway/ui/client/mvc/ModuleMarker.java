package cn.mapway.ui.client.mvc;

import java.lang.annotation.*;


/**
 * 如果一个类实现了 IFrameModule ,必须通过此注解为该模块命名.
 *
 * @author zhangjianshe
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ModuleMarker {

    /**
     * 模块代码.
     *
     * @return the string
     */
    String value() default "";

    /**
     * 模块名称.
     *
     * @return the string
     */
    String name() default "";

    /**
     * 是否是公共模块，无需认证
     *
     * @return boolean
     */
    boolean isPublic() default false;

    /**
     * 模块图表
     *
     * @return string
     */
    String icon() default "icon.png";

    /**
     * 说明.
     *
     * @return string
     */
    String summary() default "";


    /**
     * 用一个图标标志该模块 此图标是一个 WEB FONT 自定义的unicode
     *
     * @return
     */
    String unicode() default "";

    /**
     * 是否可以显示在界面上.
     *
     * @return boolean
     */
    boolean visible() default true;

    /**
     * 模块分组信息.
     * 用于模块的使用说明
     *
     * @return string
     */
    String group() default "/";

    /**
     * 模块的排序 缺省为 0
     *
     * @return
     */
    int order() default 0;

    /**
     * 父模块ID
     * 用于构建模块之间的树状关系
     * 缺省为根模块
     *
     * @return
     */
    String parent() default "";

    /**
     * 模块的标签 可以根据需要添加
     */
    String[] tags() default {};

    /**
     * 这个模块应用于THEME 如果不设定 应用于所有的THEME
     * 否则只会应用在相应的THEME中
     * @return
     */
    String[] themes() default {};
}
