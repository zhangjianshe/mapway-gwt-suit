package cn.mapway.test.client;

import cn.mapway.ui.client.widget.color.AiColor;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.client.widget.icon.IconSelector;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class TestMainPage extends Composite {
    private static final TestMainPageUiBinder ourUiBinder = GWT.create(TestMainPageUiBinder.class);
    @UiField
    AiColor color;
    @UiField
    Button btnTest;
    public TestMainPage() {
        initWidget(ourUiBinder.createAndBindUi(this));
        color.setValue("#FF0055");
    }

    @UiHandler("btnTest")
    public void btnTestClick(ClickEvent event) {
        Popup<IconSelector> popup = IconSelector.getPopup(true);

        popup.showRelativeTo(btnTest);
    }

    interface TestMainPageUiBinder extends UiBinder<DockLayoutPanel, TestMainPage> {
    }
}