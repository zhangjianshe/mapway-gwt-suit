package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.Any;

/**
 * The Schema Object allows the definition of input and output data types. These types can be objects, but also primitives and arrays. This object is an extended subset of the
 * <a href="https://json-schema.org/">JSON Schema Specification Wright Draft 00</a>.
 *
 * For more information about the properties, see JSON Schema Core and JSON Schema Validation. Unless stated otherwise, the property definitions follow the JSON Schema.
 *
 * <b>Properties</b>
 * The following properties are taken directly from the JSON Schema definition and follow the same specifications:
 *
 * title
 * multipleOf
 * maximum
 * exclusiveMaximum
 * minimum
 * exclusiveMinimum
 * maxLength
 * minLength
 * pattern (This string SHOULD be a valid regular expression, according to the Ecma-262 Edition 5.1 regular expression dialect)
 * maxItems
 * minItems
 * uniqueItems
 * maxProperties
 * minProperties
 * required
 * enum
 *  The following properties are taken from the JSON Schema definition but their definitions were adjusted to the OpenAPI Specification.
 *
 * type - Value MUST be a string. Multiple types via an array are not supported.
 * allOf - Inline or referenced schema MUST be of a Schema Object and not a standard JSON Schema.
 * oneOf - Inline or referenced schema MUST be of a Schema Object and not a standard JSON Schema.
 * anyOf - Inline or referenced schema MUST be of a Schema Object and not a standard JSON Schema.
 * not - Inline or referenced schema MUST be of a Schema Object and not a standard JSON Schema.
 * items - Value MUST be an object and not an array. Inline or referenced schema MUST be of a Schema Object and not a standard JSON Schema. items MUST be present if the type is array.
 * properties - Property definitions MUST be a Schema Object and not a standard JSON Schema (inline or referenced).
 * additionalProperties - Value can be boolean or object. Inline or referenced schema MUST be of a Schema Object and not a standard JSON Schema. Consistent with JSON Schema, additionalProperties defaults to true.
 * description - CommonMark syntax MAY be used for rich text representation.
 * format - See Data Type Formats for further details. While relying on JSON Schema's defined formats, the OAS offers a few additional predefined formats.
 * default - The default value represents what would be assumed by the consumer of the input as the value of the schema if one is not provided. Unlike JSON Schema, the value MUST conform to the defined type for the Schema Object defined at the same level. For example, if type is string, then default can be "foo" but cannot be 1.
 *
 * Alternatively, any time a Schema Object can be used, a Reference Object can be used in its place. This allows referencing definitions instead of defining them inline.
 *
 * Additional properties defined by the JSON Schema specification that are not mentioned here are strictly unsupported.
 *
 * Other than the JSON Schema subset fields, the following fields MAY be used for further schema documentation:
 *
 */
@JsType(isNative = true)
public class SchemaObject extends ReferenceObject{
    /**
     * A true value adds "null" to the allowed type specified by the type keyword, only if type is explicitly defined within the same Schema Object. Other Schema Object constraints retain their defined behavior, and therefore may disallow the use of null as a value. A false value leaves the specified or default type unmodified. The default value is false.
     */
    public Boolean nullable;

    /**
     * Adds support for polymorphism. The discriminator is an object name that is used to differentiate between other schemas which may satisfy the payload description. See Composition and Inheritance for more details.
     */
    public DiscriminatorObject discriminator;

    /**
     * Relevant only for Schema "properties" definitions. Declares the property as "read only". This means that it MAY be sent as part of a response but SHOULD NOT be sent as part of the request. If the property is marked as readOnly being true and is in the required list, the required will take effect on the response only. A property MUST NOT be marked as both readOnly and writeOnly being true. Default value is false.
     */
    public Boolean readOnly;

    /**
     * Relevant only for Schema "properties" definitions. Declares the property as "write only". Therefore, it MAY be sent as part of a request but SHOULD NOT be sent as part of the response. If the property is marked as writeOnly being true and is in the required list, the required will take effect on the request only. A property MUST NOT be marked as both readOnly and writeOnly being true. Default value is false.
     */
    public Boolean writeOnly;

    /**
     * This MAY be used only on properties schemas. It has no effect on root schemas. Adds additional metadata to describe the XML representation of this property.
     */
    public XmlObject xml;

    /**
     * Additional external documentation for this schema.
     */
    public ExternalDocumentationObject externalDocs;

    /**
     * A free-form property to include an example of an instance for this schema. To represent examples that cannot be naturally represented in JSON or YAML, a string value can be used to contain the example with escaping where necessary.
     */
    public Any example;

    /**
     * Specifies that a schema is deprecated and SHOULD be transitioned out of usage. Default value is false.
     */
    public Boolean deprecated;
}
