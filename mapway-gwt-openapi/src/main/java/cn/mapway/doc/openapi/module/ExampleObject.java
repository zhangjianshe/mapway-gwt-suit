package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.Any;

/**
 *
 */
@JsType(isNative = true)
public class ExampleObject extends ReferenceObject {
    /**
     * Short description for the example.
     */
    public String summary;

    /**
     * Long description for the example. CommonMark syntax MAY be used for rich text representation.
     */
    public String description;

    /**
     * 	Embedded literal example. The value field and externalValue field are mutually exclusive. To represent examples of media types that cannot naturally represented in JSON or YAML, use a string value to contain the example, escaping where necessary.
     */
    public Any value;

    /**
     * A URL that points to the literal example. This provides the capability to reference examples that cannot easily be included in JSON or YAML documents. The value field and externalValue field are mutually exclusive.
     */
    public String externalValue;
}
