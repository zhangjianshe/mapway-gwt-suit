package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * A simple object to allow referencing other components in the specification, internally and externally.
 *
 * The Reference Object is defined by JSON Reference and follows the same structure, behavior and rules.
 *
 * For this specification, reference resolution is accomplished as defined by the JSON Reference specification and not by the JSON Schema specification.
 */
@JsType(isNative = true)
public class ReferenceObject {
    /**
     * The reference string.
     */
    @JsProperty(name = "$ref")
    public String ref;


    @JsOverlay
    public final <T> T resolve(ComponentsObject components){
        if(ref == null || ref.length() == 0 || components==null)
        {
            return (T) this;
        }
        return (T) this;
    }
}
