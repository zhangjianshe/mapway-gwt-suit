package cn.mapway.test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

public class Test implements EntryPoint {
    public void onModuleLoad() {
        RootLayoutPanel.get().add(new TestMainPage());
    }
}
