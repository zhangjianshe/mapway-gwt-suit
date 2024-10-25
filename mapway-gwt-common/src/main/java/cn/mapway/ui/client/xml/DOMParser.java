package cn.mapway.ui.client.xml;

import jsinterop.annotations.JsType;
import jsinterop.annotations.JsPackage;
import elemental2.dom.Document;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class DOMParser {
    /**
     * parse xml document
     * @param xmlText
     * @param mimeType "text/xml"
     * @return
     */
    public native Document parseFromString(String xmlText, String mimeType);
}

