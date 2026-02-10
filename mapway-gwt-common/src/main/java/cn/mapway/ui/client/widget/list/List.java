package cn.mapway.ui.client.widget.list;

import cn.mapway.ui.client.util.IEachElement;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.VerticalPanel;

public class List extends CommonEventComposite {
    private static final ListUiBinder ourUiBinder = GWT.create(ListUiBinder.class);
    ListItem selectedItem = null;
    ClickHandler itemClicked = event -> {
        ListItem listItem = (ListItem) event.getSource();
        selectItem(listItem, true);
    };
    @UiField
    VerticalPanel root;

    public List() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void selectItem(ListItem listItem) {
        selectItem(listItem, false);
    }

    public void selectItem(ListItem listItem, boolean fireEvent) {
        if (selectedItem != null) {
            selectedItem.setSelect(false);
        }
        selectedItem = listItem;
        if (selectedItem != null) {
            selectedItem.setSelect(true);
        }
        if (fireEvent) {
            fireEvent(CommonEvent.selectEvent(selectedItem));
        }
    }

    public void selectIndex(int index, boolean fireEvent) {
        if (index < 0 || index >= root.getWidgetCount()) {
            return;
        }
        ListItem listItem = (ListItem) root.getWidget(index);
        selectItem(listItem, fireEvent);
    }

    public void selectFirst(boolean fireEvent) {
        selectIndex(0, fireEvent);
    }

    public void addItem(ListItem item) {
        root.add(item);

        item.addDomHandler(itemClicked, ClickEvent.getType());
    }

    public void eachItem(IEachElement<ListItem> consumer) {
        if (consumer == null) {
            return;
        }
        for (int i = 0; i < root.getWidgetCount(); i++) {
            ListItem listItem = (ListItem) root.getWidget(i);
            boolean doNext = consumer.each(listItem);
            if (!doNext) {
                return;
            }
        }
    }

    public void clear() {
        root.clear();
    }

    interface ListUiBinder extends UiBinder<VerticalPanel, List> {
    }
}