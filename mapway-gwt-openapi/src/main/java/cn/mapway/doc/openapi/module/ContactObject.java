package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * Contact information for the exposed API.
 * This object MAY be extended with <a href='https://swagger.io/specification/#specification-extensions'>Specification Extensions</a>.
 */
@JsType(isNative = true)
public class ContactObject {
    /**
     * The identifying name of the contact person/organization.
     */
    public String name;

    /**
     * The URL pointing to the contact information. MUST be in the format of a URL.
     */
    public String url;

    /**
     * The email address of the contact person/organization. MUST be in the format of an email address.
     */
    public String email;
}
