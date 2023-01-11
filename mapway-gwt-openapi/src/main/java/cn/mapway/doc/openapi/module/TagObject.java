package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * Adds metadata to a single tag that is used by the Operation Object. It is not mandatory to have a Tag Object per tag defined in the Operation Object instances.
 */
@JsType(isNative = true)
public class TagObject extends ReferenceObject{
    /**
     * REQUIRED. The name of the tag.
     */
    public String name;
    /**
     * A short description for the tag. CommonMark syntax MAY be used for rich text representation.
     */
    public String description;

    /**
     * Additional external documentation for this tag.
     */
    public ExternalDocumentationObject externalDocs;
}
