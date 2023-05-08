package cn.mapway.ui.client.widget.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class MessagePanel extends Composite {
    private static final MessagePanelUiBinder ourUiBinder = GWT.create(MessagePanelUiBinder.class);
    @UiField
    Label lbMessage;
    public MessagePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public MessagePanel setText(String text) {
        lbMessage.setText(text);
        return this;
    }

    public MessagePanel setHtml(String html) {
        lbMessage.getElement().setInnerHTML(html);
        return this;
    }

    interface MessagePanelUiBinder extends UiBinder<HTMLPanel, MessagePanel> {
    }
}