package cn.mapway.ui.client.db;


import cn.mapway.ui.client.fonts.Fonts;

/**
 * Shape File  字段类型 0 String 1 Integer 2 Float 3 DateTime 4 Boolean
 * public static final int FLD_TYPE_STRING = 0;
 * public static final int FLD_TYPE_INTEGER = 1;
 * public static final int FLD_TYPE_FLOAT = 2;
 * public static final int FLD_TYPE_DATETIME = 3;
 * public static final int FLD_TYPE_BOOLEAN = 4;
 */
public enum DbFieldType {
    FLD_TYPE_STRING(0, "字符串", Fonts.ABC),
    FLD_TYPE_INTEGER(1, "整数", Fonts.TEXTFORMAT_123),
    FLD_TYPE_BIGINTEGER(6, "长整数", Fonts.INT8),
    FLD_TYPE_FLOAT(2, "浮点数", Fonts.NUMBER),
    FLD_TYPE_DATETIME(3, "日期时间", Fonts.CALENDAR),
    FLD_TYPE_BOOLEAN(4, "布尔型", Fonts.CONTROLER),
    FLD_TYPE_GEOMETRY(5, "地理字段", Fonts.EARTH),
    FLD_TYPE_SERIAL(7, "序列", Fonts.SERIAL),
    FLD_TYPE_STRING_ARRAY(8, "字符串数组", Fonts.LIST),
    FLD_TYPE_CLOB(8, "文本块", Fonts.FILES);
    //TODO 添加新的图标样式
    int code;
    String name;
    String unicode;

    DbFieldType(int code, String name, String unicode) {
        this.code = code;
        this.name = name;
        this.unicode = unicode;
    }

    public static DbFieldType valueOfCode(Integer code) {
        if (code == null) return FLD_TYPE_STRING;
        for (DbFieldType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return FLD_TYPE_STRING;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getUnicode() {
        return unicode;
    }
}
