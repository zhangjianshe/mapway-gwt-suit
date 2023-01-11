package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * Describes a single response from an API Operation, including design-time, static links to operations based on the response.
 */
@JsType(isNative = true)
public class ResponseObject extends ReferenceObject {
    /**
     * REQUIRED. A short description of the response. CommonMark syntax MAY be used for rich text representation.
     */
    public String description;

    /**
     * Maps a header name to its definition. RFC7230 states header names are case insensitive. If a response header is defined with the name "Content-Type", it SHALL be ignored.
     */
    public JsPropertyMap<HeaderObject> headers;

    /**
     * A map containing descriptions of potential response payloads. The key is a media type or media type range and the value describes it. For responses that match multiple keys, only the most specific key is applicable. e.g. text/plain overrides text/*
     */
    public JsPropertyMap<MediaTypeObject> content;

    /**
     * A map of operations links that can be followed from the response. The key of the map is a short name for the link, following the naming constraints of the names for Component Objects.
     */
    public JsPropertyMap<LinkObject> links;
}
