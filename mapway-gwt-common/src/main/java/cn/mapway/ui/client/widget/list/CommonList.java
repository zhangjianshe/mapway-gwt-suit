package cn.mapway.ui.client.widget.list;

import cn.mapway.ui.client.util.IEachElement;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CommonList extends CommonEventComposite {
    private static final CommonListUiBinder ourUiBinder = GWT.create(CommonListUiBinder.class);
    CommonListItem selected = null;
    private final CommonEventHandler commonHandler = commonEvent -> {
        if (commonEvent.isRename()) {
            fireEvent(commonEvent);
        } else if (commonEvent.isSelect()) {
            CommonListItem item = (CommonListItem) commonEvent.getSource();
            selectItem(item, true);

        }
    };
    @UiField
    VerticalPanel list;
    boolean editable = false;

    public CommonList() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /**
     * 获取当前选择的条目的数据
     *
     * @return nullable
     */
    public Object getValue() {
        if (selected == null) {
            return null;
        } else {
            return selected.getData();
        }
    }

    void selectItem(CommonListItem item, boolean fireEvent) {
        if (selected != null) {
            selected.setSelect(false);
        }
        selected = item;
        selected.setSelect(true);
        if (fireEvent) {
            fireEvent(CommonEvent.selectEvent(item.getData()));
        }

    }

    public boolean eachItem(IEachElement<CommonListItem> handler){
        if(handler==null)
        {
            return false;
        }
        for(int i=0;i<list.getWidgetCount();i++){
            if(list.getWidget(i) instanceof CommonListItem){
                boolean checkNext = handler.each((CommonListItem) list.getWidget(i));
                if(!checkNext){
                    return false;
                }
            }
        }
        return true;
    }

    public void clear() {
        list.clear();
    }

    public CommonListItem addItem(String unicode, String text, Object data) {
        CommonListItem item = new CommonListItem();
        item.addCommonHandler(commonHandler);
        item.setUnicode(unicode);
        item.setText(text);
        item.setData(data);
        item.setEditable(editable);
        list.add(item);
        return item;
    }

    public void enableEditor(boolean editable) {
        this.editable = editable;
        for (int i = 0; i < list.getWidgetCount(); i++) {
            Widget widget = list.getWidget(i);
            if (widget instanceof CommonListItem) {
                CommonListItem item = (CommonListItem) widget;
                item.setEditable(editable);
            }
        }
    }

    public void reset() {
        for (int i = 0; i < list.getWidgetCount(); i++) {
            Widget widget = list.getWidget(i);
            if (widget instanceof CommonListItem) {
                CommonListItem item = (CommonListItem) widget;
                item.reset();
            }
        }
    }

    public void selectFirst() {
        if (list.getWidgetCount() > 0) {
            CommonListItem item = (CommonListItem) list.getWidget(0);
            selectItem(item, true);
        }
    }


    interface CommonListUiBinder extends UiBinder<VerticalPanel, CommonList> {
    }
}