package cn.mapway.test.client;

import cn.mapway.ui.client.widget.color.AiColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class TestMainPage extends Composite {
    interface TestMainPageUiBinder extends UiBinder<DockLayoutPanel, TestMainPage> {
    }

    private static TestMainPageUiBinder ourUiBinder = GWT.create(TestMainPageUiBinder.class);
    @UiField
    AiColor color;

    public TestMainPage() {
        initWidget(ourUiBinder.createAndBindUi(this));
        color.setValue("#FF0055");
    }
}