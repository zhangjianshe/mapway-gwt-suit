package cn.mapway.rbac.shared;

/**
 * 资源地理范围类型
 */
public enum RegionKind {
    REGION_KIND_NONE(0,"没有定义"),
    REGION_KIND_SHENG(1,"省"),
    REGION_KIND_SHI(2,"市"),
    REGION_KIND_XIAN(3,"县"),
    REGION_KIND_XIANG(4,"乡"),
    REGION_KIND_CUN(5,"村");

    Integer code;
    String name;

    RegionKind(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RegionKind fromCode(Integer code) {
        if (code == null) {
            return REGION_KIND_NONE;
        }
        for (RegionKind kind : RegionKind.values()) {
            if (kind.code.equals(code)) {
                return kind;
            }
        }
        return REGION_KIND_NONE;
    }
}
