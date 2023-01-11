package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * An object representing a Server.
 * This object MAY be extended with <a href='https://swagger.io/specification/#specification-extensions'>Specification Extensions</a>.
 */
@JsType(isNative = true)
public class ServerObject {
    /**
     * REQUIRED. A URL to the target host. This URL supports Server Variables
     * and MAY be relative, to indicate that the host location is relative to
     * the location where the OpenAPI document is being served.
     * Variable substitutions will be made when a variable is named in {brackets}.
     */
    public String url;

    /**
     * An optional string describing the host designated by the URL.
     * CommonMark syntax MAY be used for rich text representation.
     */
    public String description;

    /**
     * A map between a variable name and its value. The value is used for substitution in the server's URL template.
     */
    public JsPropertyMap<ServerVariableObject> variables;

    @JsOverlay
    public final String getName(){
        if(description==null)
        {
            return url;
        }
        return description;
    }
}
