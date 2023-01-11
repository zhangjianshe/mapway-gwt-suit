package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 *  Allows configuration of the supported OAuth Flows.
 */
@JsType(isNative = true)
public class OAuthFlowsObject {
    /**
     * Configuration for the OAuth Implicit flow
     */
    public OAuthFlowObject implicit;
    /**
     * Configuration for the OAuth Resource Owner Password flow
     */
    public OAuthFlowObject password;
    /**
     * Configuration for the OAuth Client Credentials flow. Previously called application in OpenAPI 2.0.
     */
    public OAuthFlowObject clientCredentials;
    /**
     * Configuration for the OAuth Authorization Code flow. Previously called accessCode in OpenAPI 2.0.
     */
    public OAuthFlowObject authorizationCode;

}
