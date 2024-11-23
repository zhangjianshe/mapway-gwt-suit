package cn.mapway.ui.client.widget;

import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * AiTab
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class AiTab extends HorizontalPanel implements HasCommonHandlers, ClickHandler {
    AiAnchor current = null;

    public AiTab() {

    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

    public void addItem(String name, ImageResource icon, Object data) {
        AiAnchor anchor = new AiAnchor(name);
        anchor.setStyleName("ai-operator");
        anchor.setData(data);
        anchor.addClickHandler(this);
        add(anchor);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (current != null) {
            current.setSelected(false);
            current = null;
        }

        AiAnchor anchor = (AiAnchor) event.getSource();
        Object data = anchor.getData();
        current = anchor;
        current.setSelected(true);
        fireEvent(CommonEvent.selectEvent(data));
    }

    public void setSelectIndex(int index, boolean fireEvent) {
        if (index >= 0 && index < this.getWidgetCount()) {
            if (current != null) {
                current.setSelected(false);
                current = null;
            }
            AiAnchor anchor = (AiAnchor) this.getWidget(index);
            current = anchor;
            current.setSelected(true);
            if (fireEvent) {
                fireEvent(CommonEvent.selectEvent(anchor.getData()));
            }
        }
    }
}
