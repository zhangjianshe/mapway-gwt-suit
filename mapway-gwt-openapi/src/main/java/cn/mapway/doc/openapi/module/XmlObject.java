package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * A metadata object that allows for more fine-tuned XML model definitions.
 *
 * When using arrays, XML element names are not inferred (for singular/plural forms) and the name property SHOULD be used to add that information. See examples for expected behavior.
 *
 *  <b>XML Attribute, Prefix and Namespace</b>
 *  In this example, a full model definition is shown.
 *  <code>
 *      <pre>
 *  {
 *   "Person": {
 *     "type": "object",
 *     "properties": {
 *       "id": {
 *         "type": "integer",
 *         "format": "int32",
 *         "xml": {
 *           "attribute": true
 *         }
 *       },
 *       "name": {
 *         "type": "string",
 *         "xml": {
 *           "namespace": "http://example.com/schema/sample",
 *           "prefix": "sample"
 *         }
 *       }
 *     }
 *   }
 * }
 *      </pre>
 *  </code>
 */
@JsType(isNative = true)
public class XmlObject {
    /**
     * Replaces the name of the element/attribute used for the described schema property. When defined within items, it will affect the name of the individual XML elements within the list. When defined alongside type being array (outside the items), it will affect the wrapping element and only if wrapped is true. If wrapped is false, it will be ignored.
     */
    public String name;

    /**
     * The URI of the namespace definition. Value MUST be in the form of an absolute URI.
     */
    public String namespace;

    /**
     * The prefix to be used for the name.
     */
    public String prefix;

    /**
     * Declares whether the property definition translates to an attribute instead of an element. Default value is false.
     */
    public Boolean attribute;

    /**
     * MAY be used only for an array definition. Signifies whether the array is wrapped (for example, <books><book/><book/></books>) or unwrapped (<book/><book/>). Default value is false. The definition takes effect only when defined alongside type being array (outside the items).
     */
    public Boolean wrapped;
}
