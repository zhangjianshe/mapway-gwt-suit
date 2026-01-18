package cn.mapway.rbac.shared;

import java.util.HashSet;
import java.util.Set;

/**
 * 资源类型
 */
public enum ResourceKind {

    RESOURCE_KIND_CUSTOM(0, "自定义资源"),
    RESOURCE_KIND_SYSTEM(1, "系统资源"),
    RESOURCE_KIND_REGION(2, "数据区域"),
    RESOURCE_KIND_FUNCTION(3, "功能资源"),
    RESOURCE_KIND_SYS_MENU(4, "菜单资源"),
    RESOURCE_KIND_APPLICATION(5, "应用模块"),

    RESOURCE_KIND_TOOLBOX(1000, "工具箱"),
    RESOURCE_KIND_MODEL_TEMPLATE(1001, "模型模板"),
    RESOURCE_KIND_SAMPLE_SET(1002, "样本集"),
    RESOURCE_KIND_TRAIN_PROJECT(1003, "训练项目"),
    RESOURCE_KIND_COMPUTE_RESOURCE(1004, "硬件资源"),
    RESOURCE_KIND_FRONT_END_COMPONENT(1005, "前端组件资源"),
    RESOURCE_KIND_TRAIN_MODEL(1006, "可训练模型");

    public final Integer code;
    public final String name;

    ResourceKind(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ResourceKind fromCode(Integer code) {
        if (code == null) {
            return RESOURCE_KIND_CUSTOM;
        }
        for (ResourceKind kind : ResourceKind.values()) {
            if (kind.code.equals(code)) {
                return kind;
            }
        }
        return RESOURCE_KIND_CUSTOM;
    }

    public static Set<ResourceKind> getResourceSet() {
        Set<ResourceKind> result = new HashSet<>();

        for (ResourceKind kind : ResourceKind.values()) {
            if (kind.code >= RESOURCE_KIND_TOOLBOX.code) {
                result.add(kind);
            }
        }
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "{ 名称: " + name + ", 代码: " + code + "}";
    }
}
