package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;


/**
 * Describes a single request body.
 */
@JsType(isNative = true)
public class RequestBodyObject extends ReferenceObject {

    /**
     * A brief description of the request body. This could contain examples of use. CommonMark syntax MAY be used for rich text representation.
     */
   public String description;

    /**
     * REQUIRED. The content of the request body. The key is a media type or media type range and the value describes it. For requests that match multiple keys, only the most specific key is applicable. e.g. text/plain overrides text/*
     */
   public JsPropertyMap<MediaTypeObject> content;

    /**
     * Determines if the request body is required in the request. Defaults to false.
     */
   public Boolean required;
}
