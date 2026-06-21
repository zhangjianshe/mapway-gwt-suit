package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.resource.MapwayResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class Waiting extends HorizontalPanel {
    public Waiting() {
        setWidth("100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        Label label = new Label();
        label.setStyleName(MapwayResource.INSTANCE.css().waiting());
        add(label);
    }
}
