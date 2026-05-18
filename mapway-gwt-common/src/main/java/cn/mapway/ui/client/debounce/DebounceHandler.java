package cn.mapway.ui.client.debounce;

import com.google.gwt.event.shared.EventHandler;
import jsinterop.annotations.JsType;

@JsType
public interface DebounceHandler extends EventHandler {

    Object onInvoke();

}
