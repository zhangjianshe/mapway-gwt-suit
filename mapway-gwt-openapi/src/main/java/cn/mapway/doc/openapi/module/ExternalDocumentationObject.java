package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * Allows referencing an external resource for extended documentation.
 */
@JsType(isNative = true)
public class ExternalDocumentationObject {
    /**
     * A short description of the target documentation. CommonMark syntax MAY be used for rich text representation.
     */
    public String description;

    /**
     * REQUIRED. The URL for the target documentation. Value MUST be in the format of a URL.
     */
    public String url;
}
