package cn.mapway.ui.client.widget.editor;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;

/**
 * CmsTools
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class CmsTools {
    public static void loadScript(Callback callback) {
        if (!isLoaded()) {
            ScriptInjector.fromUrl("/js/editor/ckeditor.js").setCallback(callback).inject();
        } else {
            if (callback != null) {
                callback.onSuccess(null);
            }
        }
    }

    private native static boolean isLoaded()/*-{
        return $wnd.ClassicEditor != null;
    }-*/;

}
