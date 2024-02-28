package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * Configuration details for a supported OAuth Flow
 */
@JsType(isNative = true)
public class OAuthFlowObject {
    /**
     * REQUIRED. The authorization URL to be used for this flow. This MUST be in the form of a URL.
     * applies To oauth2 ("implicit", "authorizationCode")
     */
    public String authorizationUrl;


    /**
     * REQUIRED. The token URL to be used for this flow. This MUST be in the form of a URL.
     * applies to oauth2 ("password", "clientCredentials", "authorizationCode")
     */
    public String tokenUrl;


    /**
     * The URL to be used for obtaining refresh tokens. This MUST be in the form of a URL.
     *
     *  applies to oauth2
     */
    public String refreshUrl;

    /**
     * REQUIRED. The available scopes for the OAuth2 security scheme. A map between the scope name and a short description for it. The map MAY be empty.
     */
    public JsPropertyMap<String> oauth2;
}
