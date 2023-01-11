package cn.mapway.ui.client.widget.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class MessagePanel extends Composite {
    interface MessagePanelUiBinder extends UiBinder<HTMLPanel, MessagePanel> {
    }

    private static MessagePanelUiBinder ourUiBinder = GWT.create(MessagePanelUiBinder.class);
    @UiField
    Label lbMessage;
    public MessagePanel setText(String text)
    {
        lbMessage.setText(text);
        return this;
    }
    public MessagePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}