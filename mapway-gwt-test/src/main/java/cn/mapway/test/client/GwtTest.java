package cn.mapway.test.client;

import cn.mapway.test.client.openapi.OpenApiWindow;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

/**
 * main test window for application
 */
public class GwtTest implements EntryPoint {
    public void onModuleLoad() {
        OpenApiWindow openApiWindow = new OpenApiWindow();
        RootLayoutPanel.get().add(openApiWindow);
    }
}
