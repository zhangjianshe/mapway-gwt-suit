package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AiButtonGroup extends HTMLPanel implements HasCommonHandlers {

    public AiButtonGroup(String html) {
        super(html);
        this.setStyleName("ai-button-group");
    }

    public AiButtonGroup() {
        super("");
        this.setStyleName("ai-button-group");
    }

    public void addButton(AiButton button) {
        this.add(button);
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }
}
