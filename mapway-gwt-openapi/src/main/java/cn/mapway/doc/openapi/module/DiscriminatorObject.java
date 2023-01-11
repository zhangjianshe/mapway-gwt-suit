package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * When request bodies or response payloads may be one of a number of different schemas, a discriminator object can be used to aid in serialization, deserialization, and validation. The discriminator is a specific object in a schema which is used to inform the consumer of the specification of an alternative schema based on the value associated with it.
 *
 * When using the discriminator, inline schemas will not be considered.
 *
 * The discriminator object is legal only when using one of the composite keywords oneOf, anyOf, allOf.
 *
 */
@JsType(isNative = true)
public class DiscriminatorObject {
    /**
     * REQUIRED. The name of the property in the payload that will hold the discriminator value.
     */
    public String propertyName;

    /**
     * An object to hold mappings between payload values and schema names or references.
     */
    public JsPropertyMap<String> mapping;
}
