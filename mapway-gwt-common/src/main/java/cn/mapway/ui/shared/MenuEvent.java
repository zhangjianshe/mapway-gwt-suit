package cn.mapway.ui.shared;

import com.google.gwt.dom.client.NativeEvent;
import lombok.Data;

@Data
public class MenuEvent {
    NativeEvent nativeEvent;
    Object  data;
    public MenuEvent(NativeEvent nativeEvent,Object data) {
        this.nativeEvent = nativeEvent;
        this.data = data;
    }
}
