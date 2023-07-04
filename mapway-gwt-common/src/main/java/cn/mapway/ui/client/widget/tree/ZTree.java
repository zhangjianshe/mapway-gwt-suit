package cn.mapway.ui.client.widget.tree;

import cn.mapway.ui.client.widget.panel.MessagePanel;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ZTree
 * Tree
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class ZTree extends VerticalPanel implements HasCommonHandlers {

    String storagePrefix = "";
    Storage storage = Storage.getLocalStorageIfSupported();
    ImageTextItem current = null;
    String itemStyleName;
    MessagePanel messagePanel;
    boolean enabledChecked = false;
    boolean checkWithChildren = false;
    boolean autoSelected = true;

    //树形条目被点击了
    private final CommonEventHandler itemClicked = event -> {
        ImageTextItem item = (ImageTextItem) event.getSource();
        if (event.isToogle()) {
        } else if (event.isSelect()) {
            if (autoSelected) {
                setCurrentItem(item);
            }
            fireEvent(CommonEvent.selectEvent(item));
        } else if (event.isDoubleClick()) {
            setCurrentItem(item);
            fireEvent(CommonEvent.doubleClickEvent(item));
        } else if (event.isOpen()) {
            if (storage != null) {
                storage.setItem(item.getStorageKey(), "1");
            }
        } else if (event.isClose()) {
            if (storage != null) {
                storage.setItem(item.getStorageKey(), "0");
            }
        } else if (event.isMenu()) {
            fireEvent(CommonEvent.menuEvent(event.getValue()));
        } else if (event.isChecked() || event.isUnChecked()) {

            if (this.checkWithChildren) {
                checkChildren(item, event.isChecked());
            }
            fireEvent(event);

        }
    };


    public ZTree() {
        messagePanel = new MessagePanel();
        add(messagePanel);
    }

    private void checkChildren(ImageTextItem item, boolean checked) {
        for (ImageTextItem child : item.getChildren()) {
            child.setChecked(checked, false);
            checkChildren(child, checked);
        }
    }

    /**
     * 是否自动选择
     *
     * @param autoSelected
     */
    public void setAutoSelected(boolean autoSelected) {
        this.autoSelected = autoSelected;
    }

    public void setValue(ImageTextItem item, boolean fire) {
        setCurrentItem(item);
        if (fire) {
            fireEvent(CommonEvent.selectEvent(item));
        }
    }

    public void enableChecked(Boolean checked) {
        if (enabledChecked != checked) {
            updateItems(checked);
            enabledChecked = checked;
        }
    }

    /**
     * 设置选择的时候 是否同时选择或者取消所有的子节点
     *
     * @param checkWithChildren
     */
    public void enableCheckWithChildren(boolean checkWithChildren) {
        this.checkWithChildren = checkWithChildren;
    }

    private void updateItems(Boolean checked) {
        int index = 0;
        for (index = 0; index < getWidgetCount(); index++) {
            Widget widget = getWidget(index);
            if (widget instanceof ImageTextItem) {
                ImageTextItem item = (ImageTextItem) widget;
                item.enableCheck(checked);
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        messagePanel.setHeight("0px");
        add(messagePanel);
    }

    public void setCurrentItem(ImageTextItem item) {
        if (current != null) {
            current.setSelect(false);
        }
        current = item;
        current.setSelect(true);
    }

    public void setStoragePrefix(String prefix) {
        storagePrefix = prefix;
    }

    public ImageTextItem addItem(ImageTextItem parent, String text, ImageResource icon) {
        ImageTextItem item;
        if (parent == null) {
            item = new ImageTextItem();
            if (itemStyleName != null && itemStyleName.length() > 0) {
                item.setStyleName(itemStyleName);
            }
            item.setValue(icon, text);
            item.setLevel(0);
            item.setStorageKey(storagePrefix + "/" + text);
            item.setParentItem(null);
            item.enableCheck(enabledChecked);
            add(item);
        } else {
            item = parent.addChild(text, icon);
        }
        item.addCommonHandler(itemClicked);
        return item;
    }

    public ImageTextItem addFontIconItem(ImageTextItem parent, String text, String unicode) {
        ImageTextItem item;
        if (parent == null) {
            item = new ImageTextItem();
            if (itemStyleName != null && itemStyleName.length() > 0) {
                item.setStyleName(itemStyleName);
            }
            item.setValue(unicode, text);
            item.setLevel(0);
            item.setStorageKey(storagePrefix + "/" + text);
            item.enableCheck(enabledChecked);
            add(item);
            item.setParentItem(null);
        } else {
            item = parent.addChild(text, unicode);
        }
        item.addCommonHandler(itemClicked);
        return item;
    }

    public ImageTextItem insertItem(ImageTextItem parent, String text, ImageResource icon) {
        ImageTextItem item;
        if (parent == null) {
            item = new ImageTextItem();
            if (itemStyleName != null && itemStyleName.length() > 0) {
                item.setStyleName(itemStyleName);
            }
            item.setValue(icon, text);
            item.setLevel(0);
            item.setStorageKey(storagePrefix + "/" + text);
            item.enableCheck(enabledChecked);
            insert(item, 0);
            item.setParentItem(null);

        } else {
            item = parent.addChild(text, icon);
        }
        item.addCommonHandler(itemClicked);

        return item;
    }

    public void setMessage(String message, int height) {
        messagePanel.setHtml(message);
        messagePanel.setHeight(height + "px");
    }

    public void setMessage(String message) {
        setMessage(message, 60);
    }


    public void clearMessage() {
        messagePanel.setText("");
        messagePanel.setHeight("0px");
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

    /**
     * 根据本地存储 展开响应的节点
     */
    public void resetLayout() {
        if (storage != null) {
            for (int i = 0; i < getWidgetCount(); i++) {
                Widget widget = getWidget(i);
                if (widget instanceof ImageTextItem) {
                    ImageTextItem item = (ImageTextItem) widget;
                    layoutItem(item);
                }
            }
        }
    }

    private void layoutItem(ImageTextItem item) {
        if (item.getChildren().size() > 0) {
            String key = item.getStorageKey();
            String expanded = storage.getItem(key);
            item.expand(expanded != null && !expanded.equals("0"));

            for (ImageTextItem item1 : item.getChildren()) {
                layoutItem(item1);
            }
        }
    }

    public void sortItem(Comparator<ImageTextItem> sort) {
        List<ImageTextItem> all = new ArrayList<>();
        for (int index = 0; index < getWidgetCount(); index++) {
            Widget widget = getWidget(index);
            if (widget instanceof ImageTextItem) {
                ImageTextItem item = (ImageTextItem) widget;
                item.sortItem(sort);
                all.add(item);
            }
        }
        clear();
        if (sort != null) {
            Collections.sort(all, sort);
        }

        for (ImageTextItem item : all) {
            add(item);
        }
    }

    public void sortByName(boolean asc) {
        if (asc) {
            sortItem((o1, o2) -> o1.getText().compareTo(o2.getText()));
        } else {
            sortItem((o1, o2) -> o2.getText().compareTo(o1.getText()));
        }
    }

    public ImageTextItem getCurrent() {
        return current;
    }

    public void setItemStyleName(String treeItem) {
        this.itemStyleName = treeItem;
    }
}
