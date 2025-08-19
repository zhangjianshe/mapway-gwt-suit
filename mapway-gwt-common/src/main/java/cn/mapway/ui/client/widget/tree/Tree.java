package cn.mapway.ui.client.widget.tree;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.resource.MapwayResource;
import cn.mapway.ui.client.util.IEachElement;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Tree extends Composite implements HasCommonHandlers {

    private static final TreeUiBinder ourUiBinder = GWT.create(TreeUiBinder.class);
    private final CloseHandler<Object> closeHandler = event -> fireEvent(CommonEvent.closeEvent(event.getSource()));
    private final OpenHandler<Object> openHandler = event -> fireEvent(CommonEvent.openEvent(event.getSource()));
    private final CommonEventHandler handler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isMenu()) {
                fireEvent(event);
            }
        }
    };
    @Setter
    String openIcon;
    @Setter
    String closeIcon;
    List<TreeItem> selectedItems = new ArrayList<>();
    private final ClickHandler clickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            event.preventDefault();
            event.stopPropagation();
            Object source = event.getSource();
            if (source instanceof TreeItem) {
                TreeItem source1 = (TreeItem) source;
                clearSelected();
                source1.setSelected(true);
                selectedItems.add(source1);
                fireEvent(CommonEvent.selectEvent(source));
            }
        }
    };
    @UiField
    HTMLPanel root;
    boolean enableContextMenu = false;
    private String itemStyle = "";

    public Tree() {
        this(Fonts.toHtmlEntity(Fonts.DOWN), Fonts.toHtmlEntity(Fonts.RIGHT));
    }

    public Tree(String openIcon, String closeIcon) {
        this.openIcon = openIcon;
        this.closeIcon = closeIcon;
        initWidget(ourUiBinder.createAndBindUi(this));
        setItemStyle(MapwayResource.INSTANCE.css().aiTreeItem());

        //禁止上下文菜单
        root.addDomHandler(event -> {
            event.stopPropagation();
            event.preventDefault();
        }, ContextMenuEvent.getType());
    }

    /**
     * 重新加载所有的items 为子节点
     *
     * @param items
     */
    public void reorderChildren(List<TreeItem> items) {
        root.clear();
        for (TreeItem item : items) {
            root.add(item);
        }
    }

    public List<TreeItem> getChildren() {
        List<TreeItem> children = new ArrayList<>(root.getWidgetCount());
        for (int i = 0; i < root.getWidgetCount(); i++) {
            children.add((TreeItem) root.getWidget(i));
        }
        return children;
    }

    /**
     * 允许组件 触发 右键菜单
     */
    public void enableContextMenu(boolean enable) {
        enableContextMenu = enable;
        eachItem(new IEachElement<TreeItem>() {
            @Override
            public boolean each(TreeItem e) {
                e.enableContextMenu(enableContextMenu);
                return true;
            }
        });
    }

    public void clearSelected() {
        for (TreeItem item : selectedItems) {
            item.setSelected(false);
        }
        selectedItems.clear();
    }

    public void clear() {
        root.clear();
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler commonEventHandler) {
        return addHandler(commonEventHandler, CommonEvent.TYPE);
    }

    public TreeItem addItem(TreeItem parent, String text, String unicode) {
        TreeItem item = addItem(parent, text);
        if (StringUtil.isNotBlank(unicode)) {
            item.setUnicode(unicode);
        }
        return item;
    }

    public TreeItem addImageItem(TreeItem parent, String text, String image) {
        TreeItem item = addItem(parent, text);
        if (StringUtil.isNotBlank(image)) {
            item.setImageUrl(image);
        }
        return item;
    }

    public TreeItem addItem(TreeItem parent, String text) {
        TreeItem item;
        if (parent == null) {
            item = new TreeItem();
            root.add(item);
            item.setLevel(1);
        } else {
            item = parent.addItem(text);
        }
        item.setOpenIcon(openIcon);
        item.setCloseIcon(closeIcon);
        item.setText(text);
        item.enableContextMenu(enableContextMenu);
        item.addDomHandler(clickHandler, ClickEvent.getType());
        item.addCloseHandler(closeHandler);
        item.addOpenHandler(openHandler);
        item.addCommonHandler(handler);
        if (StringUtil.isNotBlank(itemStyle)) {
            item.addStyleName(itemStyle);
        }
        return item;
    }

    public void setItemStyle(String style) {
        itemStyle = style;
        eachItem(treeItem -> {
            treeItem.addStyleName(itemStyle);
            return true;
        });

    }

    public void eachItem(IEachElement<TreeItem> handler) {
        if (handler == null) {
            return;
        }
        for (int i = 0; i < root.getWidgetCount(); i++) {
            TreeItem item = (TreeItem) root.getWidget(i);
            boolean doNext = handler.each(item);
            if (!doNext) {
                return;
            }
            item.eachItem(handler);
        }
    }

    public void setValue(TreeItem item, boolean fire) {
        clearSelected();
        selectedItems.add(item);
        item.setSelected(true);
        if (fire) {
            fireEvent(CommonEvent.selectEvent(item));
        }
    }

    interface TreeUiBinder extends UiBinder<HTMLPanel, Tree> {
    }
}