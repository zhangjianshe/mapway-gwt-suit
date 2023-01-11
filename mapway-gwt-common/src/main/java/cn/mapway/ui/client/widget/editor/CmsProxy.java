package cn.mapway.ui.client.widget.editor;

import com.google.gwt.dom.client.Element;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * CmsEditor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "CKEDITOR")
public class CmsProxy {

    public native static Editor replace(Element id);
    public native static Editor replace(Element element, EditOptions options);
    @JsFunction
    public interface EditorReadyFunction {
        void apply(Editor editor);
    }
}
