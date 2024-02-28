package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

import java.security.Security;

/**
 * Holds a set of reusable objects for different aspects of the OAS. All objects defined within the components object will have no effect on the API unless they are explicitly referenced from properties outside the components object.
 *
 * All the fixed fields declared above are objects that MUST use keys that match the regular expression: ^[a-zA-Z0-9\.\-_]+$.
 *
 */
@JsType(isNative = true)
public class ComponentsObject {
    /**
     * An object to hold reusable Schema Objects.
     */
    public JsPropertyMap<SchemaObject> schemas;
    public JsPropertyMap<ResponseObject> responses;
    public JsPropertyMap<ParameterObject> parameters;
    public JsPropertyMap<ExampleObject> examples;
    public JsPropertyMap<RequestBodyObject> requestBodies;
    public JsPropertyMap<HeaderObject> headers;
    public JsPropertyMap<SecuritySchemeObject> securitySchemes;
    public JsPropertyMap<LinkObject> links;
    public JsPropertyMap<CallbackObject> callbacks;


}
