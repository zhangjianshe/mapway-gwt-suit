package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * License information for the exposed API.
 * This object MAY be extended with <a href='https://swagger.io/specification/#specification-extensions'>Specification Extensions</a>.
 */
@JsType(isNative = true)
public class LicenseObject {
    /**
     * REQUIRED. The license name used for the API.
     */
    public String name;
    /**
     * A URL to the license used for the API. MUST be in the format of a URL.
     */
    public String url;
}
