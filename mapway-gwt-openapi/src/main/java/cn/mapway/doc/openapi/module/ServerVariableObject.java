package cn.mapway.doc.openapi.module;

import elemental2.core.JsArray;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * An object representing a Server Variable for server URL template substitution.
 */
@JsType(isNative = true)
public class ServerVariableObject {

    /**
     * An enumeration of string values to be used if the substitution options are from a limited set.
     * The array SHOULD NOT be empty.
     */
    @JsProperty(name = "enum")
    public JsArray<String> enums;

    /**
     * REQUIRED. The default value to use for substitution,
     * which SHALL be sent if an alternate value is not supplied.
     * Note this behavior is different than the Schema Object's
     * treatment of default values,
     * because in those cases parameter values are optional.
     * If the enum is defined, the value SHOULD exist in the enum's values.
     */
    @JsProperty(name = "default")
    public JsArray<String> defaultValue;

    /**
     * An optional description for the server variable.
     * CommonMark syntax MAY be used for rich text representation.
     */
    public String description;
}
