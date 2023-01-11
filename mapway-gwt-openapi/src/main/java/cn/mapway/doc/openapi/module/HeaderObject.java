package cn.mapway.doc.openapi.module;

import jsinterop.annotations.JsType;

/**
 * The Header Object follows the structure of the Parameter Object with the following changes:
 *
 * <li>name MUST NOT be specified, it is given in the corresponding headers map.</li>
 * <li>in MUST NOT be specified, it is implicitly in header.</li>
 * <li>All traits that are affected by the location MUST be applicable to a location of header (for example, style).</li>
 */
@JsType(isNative = true)
public class HeaderObject extends ParameterObject {

}
