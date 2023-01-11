package cn.mapway.doc.openapi.module;

import elemental2.core.JsArray;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;

/**
 * Describes a single API operation on a path.
 */
@JsType(isNative = true)
public class OperationObject {
    /**
     * A list of tags for API documentation control.
     * Tags can be used for logical grouping of operations
     * by resources or any other qualifier.
     */
    public JsArray<String> tags;

    /**
     * A short summary of what the operation does.
     */
    public String summary;

    /**
     * A verbose explanation of the operation behavior.
     * CommonMark syntax MAY be used for rich text representation.
     */
    public String description;

    /**
     * Unique string used to identify the operation. The id MUST be unique among all operations described in the API.
     * The operationId value is case-sensitive.
     * Tools and libraries MAY use the operationId to uniquely identify an operation,
     * therefore, it is RECOMMENDED to follow common programming naming conventions.
     */
    public String operationId;

    /**
     * Declares this operation to be deprecated. Consumers SHOULD refrain from usage of the declared operation.
     * Default value is false.
     */
    public Boolean deprecated;

    /**
     * Additional external documentation for this operation.
     */
    public ExternalDocumentationObject externalDocs;

    /**
     * A list of parameters that are applicable for this operation.
     * If a parameter is already defined at the Path Item,
     * the new definition will override it but can never remove it.
     * The list MUST NOT include duplicated parameters.
     * A unique parameter is defined by a combination of a name and location.
     * The list can use the Reference Object to link to parameters that are
     * defined at the OpenAPI Object's components/parameters.
     */
    public JsArrayLike<ParameterObject> parameters;
}
