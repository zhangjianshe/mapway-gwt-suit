package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * The object provides metadata about the API.
 * The metadata MAY be used by the clients if needed, and MAY be presented
 * in editing or documentation generation tools for convenience.
 * @link
 *        https://swagger.io/specification/
 */
@JsType(isNative = true)
public class InfoObject {
    /**
     * REQUIRED. The title of the API.
     */
    public String title;

    /**
     * A short description of the API.
     * <a href="https://spec.commonmark.org/">CommonMark syntax</a> MAY be used for rich text representation.
     */
    public String description;

    /**
     * A URL to the Terms of Service for the API.
     * MUST be in the format of a URL.
     */
    public  String termsOfService;

    /**
     * The contact information for the exposed API.
     */
    public ContactObject contact;

    /**
     * The license information for the exposed API.
     */
    public LicenseObject license;

    /**
     * REQUIRED. The version of the OpenAPI document (which is distinct from the
     * <a href='https://swagger.io/specification/#oas-version'> OpenAPI Specification version</a> or the API implementation version).
     */
    public String version;
}
