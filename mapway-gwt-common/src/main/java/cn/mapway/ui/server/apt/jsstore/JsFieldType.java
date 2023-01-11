package cn.mapway.ui.server.apt.jsstore;

/**
 * JsFieldType
 *
 * @author zhang
 */
public enum JsFieldType {
    String("string"),
    Number("number"),
    Boolean("boolean"),
    Object("object"),
    Array("array"),
    Date("date");
    private final String value;

    JsFieldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
