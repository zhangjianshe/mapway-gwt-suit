package cn.mapway.rbac.shared;

/**
 * 资源类型
 */
public enum ResourceKind {

    RESOURCE_KIND_CUSTOM(0,"自定义资源"),
    RESOURCE_KIND_SYSTEM(1,"系统资源"),
    RESOURCE_KIND_REGION(2,"数据区域"),
    RESOURCE_KIND_FUNCTION(3,"功能资源"),

    RESOURCE_KIND_TOOLBOX(1000,"工具箱" ),
    RESOURCE_KIND_MODEL_TEMPLATE( 1001,"模型模板"),
    RESOURCE_KIND_SAMPLE_SET(1002, "样本集"),
    RESOURCE_KIND_TRAIN_PROJECT(1003,"训练项目");

    public final Integer code;
    public final String name;

    ResourceKind(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
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
}
