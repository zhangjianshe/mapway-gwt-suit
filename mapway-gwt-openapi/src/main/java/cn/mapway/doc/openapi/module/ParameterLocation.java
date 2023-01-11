package cn.mapway.doc.openapi.module;

public enum ParameterLocation {
    QUERY("query"),
    PATH("path"),
    HEADER("header"),
    COOKIE("cookie");

    private final String value;

    ParameterLocation(final String value) {
        this.value = value;
    }

}
