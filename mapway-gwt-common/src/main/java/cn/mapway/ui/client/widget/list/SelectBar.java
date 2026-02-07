package cn.mapway.ui.client.widget.list;

import cn.mapway.ui.client.widget.AiLabel;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.ArrayList;
import java.util.List;

public class SelectBar extends CommonEventComposite {
    private static final SelectBarUiBinder ourUiBinder = GWT.create(SelectBarUiBinder.class);
    List<AiLabel> labels = new ArrayList<>();
    @UiField
    HTMLPanel root;
    @UiField
    SStyle style;
    AiLabel selectedItem = null;
    private final ClickHandler itemClicked = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            AiLabel source = (AiLabel) event.getSource();
            selectItem(source, true);
        }
    };

    public SelectBar() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void selectItem(AiLabel source, boolean b) {
        if (selectedItem != null) {
            selectedItem.setSelect(false);
        }
        selectedItem = source;
        selectedItem.setSelect(true);
        if (b) {
            fireEvent(CommonEvent.selectEvent(selectedItem.getData()));
        }
    }

    public void clear() {
        labels.clear();
        root.clear();
    }

    public void addItem(String title, Object data) {
        AiLabel label = new AiLabel(title);
        label.setStyleName(style.item());
        root.add(label);
        labels.add(label);
        label.addClickHandler(itemClicked);
        label.setData(data);
    }

    public void updateStyle() {
        if (labels.size() == 0) {
            return;
        }
        if (labels.size() == 1) {
            labels.get(0).addStyleName(style.one());
        } else if (labels.size() == 2) {
            labels.get(0).addStyleName(style.start());
            labels.get(1).addStyleName(style.end());
        } else {
            labels.get(0).addStyleName(style.start());
            labels.get(labels.size() - 1).addStyleName(style.end());
        }
    }

    public void selectIndex(int index, boolean fireEvent) {
        if (index >= 0 && index < labels.size()) {
            AiLabel label = labels.get(index);
            selectItem(label, fireEvent);
            return;
        }
        if (!labels.isEmpty()) {
            AiLabel label = labels.get(0);
            selectItem(label, fireEvent);
        }
    }

    interface SelectBarUiBinder extends UiBinder<HTMLPanel, SelectBar> {
    }

    interface SStyle extends CssResource {

        String item();

        String one();

        String start();

        String box();

        String end();
    }
}