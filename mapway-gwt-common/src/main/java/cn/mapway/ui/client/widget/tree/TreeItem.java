package cn.mapway.ui.client.widget.tree;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.window.ISelectable;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.IEachElement;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import cn.mapway.ui.shared.MenuEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class TreeItem extends Composite implements IData<Object>, HasOpenHandlers<Object>, HasCloseHandlers<Object>, HasCommonHandlers {
    private static final TreeItemUiBinder ourUiBinder = GWT.create(TreeItemUiBinder.class);
    private final MouseDownHandler downHandler = event -> {
        if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            //近处理右键
            event.stopPropagation();
            event.preventDefault();
            MenuEvent menuEvent = new MenuEvent(event.getNativeEvent(), TreeItem.this);
            fireEvent(CommonEvent.menuEvent(menuEvent));
        }
    };
    @UiField
    HTML lbText;
    @UiField
    HTMLPanel childrenPanel;
    @UiField
    HTML navi;
    @UiField
    HTMLPanel root;
    @UiField
    HTML icon;
    @UiField
    HTMLPanel rightWidget;
    @Getter
    int level = 1;
    @Getter
    boolean open = false;
    @Getter
    @Setter
    TreeItem parentItem = null;
    @Setter
    String openIcon;
    @Setter
    String closeIcon;
    @Setter
    @Getter
    String id;
    HandlerRegistration downHandlerRegistration = null;
    private Object data;
    private boolean isDir = false;

    public TreeItem() {
        this(Fonts.toHtmlEntity(Fonts.DOWN), Fonts.toHtmlEntity(Fonts.RIGHT));
    }

    public TreeItem(String openIcon, String closeIcon) {
        randomId();
        this.openIcon = openIcon;
        this.closeIcon = closeIcon;
        initWidget(ourUiBinder.createAndBindUi(this));
        navi.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.preventDefault();
                event.stopPropagation();
                if (isDir) {
                    setOpen(!open, true);
                }
            }
        });
    }

    /**
     * 随机设定一个值
     */
    public void randomId() {
        setId(StringUtil.randomString(6));
    }

    public boolean equals(Object obj) {
        if (obj instanceof TreeItem) {
            return this.getId().equals(((TreeItem) obj).getId());
        }
        return false;
    }

    public List<TreeItem> getChildren() {
        List<TreeItem> children = new ArrayList<>(childrenPanel.getWidgetCount());
        for (int i = 0; i < childrenPanel.getWidgetCount(); i++) {
            children.add((TreeItem) childrenPanel.getWidget(i));
        }
        return children;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            root.getElement().setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
        } else {
            root.getElement().removeAttribute(ISelectable.SELECT_ATTRIBUTE);
        }
    }

    public void setUnicode(String unicode) {
        if (StringUtil.isNotBlank(unicode)) {
            icon.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            icon.setHTML(Fonts.toHtmlEntity(unicode));
        } else {
            icon.getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public void clear() {
        data = null;
        childrenPanel.clear();
        childrenPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    public void setLevel(int level) {
        this.level = level;
        int padding = isDir ? this.level : (this.level - 1);
        navi.getElement().getStyle().setWidth(padding * 22, Style.Unit.PX);
    }

    protected TreeItem addItem(String text) {
        TreeItem item = new TreeItem();
        childrenPanel.add(item);
        if (open) {
            navi.getElement().setInnerHTML(openIcon);
        } else {
            navi.getElement().setInnerHTML(closeIcon);
        }
        item.setLevel(level + 1);
        setIsDir(true);
        item.setText(text);
        item.setParentItem(this);
        return item;
    }

    /**
     * 重新加载所有的items 为子节点
     *
     * @param items
     */
    public void reorderChildren(List<TreeItem> items) {
        childrenPanel.clear();
        for (TreeItem item : items) {
            childrenPanel.add(item);
        }
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
        if (isDir) {
            root.getElement().setAttribute("isdir", "true");
        } else {
            root.getElement().removeAttribute("isdir");
        }
        setLevel(level);
    }

    public void setOpen(boolean open, boolean fireEvent) {
        this.open = open;
        if (open) {
            if (childrenPanel.getWidgetCount() > 0 || isDir) {
                childrenPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
                navi.getElement().setInnerHTML(openIcon);
            } else {
                childrenPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        } else {
            if (childrenPanel.getWidgetCount() > 0 || isDir) {
                childrenPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
                navi.getElement().setInnerHTML(closeIcon);
            } else {
                childrenPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
            }
        }
        if (fireEvent) {
            if (open) {
                OpenEvent.fire(this, data);
            } else {
                CloseEvent.fire(this, data);
            }
        }
    }

    @Override
    public HandlerRegistration addOpenHandler(OpenHandler<Object> handler) {
        return addHandler(handler, OpenEvent.getType());
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<Object> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    public void setUnicodeColor(String color) {
        icon.getElement().getStyle().setColor(color);
    }

    public void setImageUrl(String iconUrl) {
        if (StringUtil.isNotBlank(iconUrl)) {
            icon.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            icon.setHTML("<img src=\"" + iconUrl + "\" width=\"100%\" height=\"100%\" />");
        } else {
            icon.getElement().getStyle().setDisplay(Style.Display.NONE);
        }

    }

    public void addStyleName(String styleName) {
        root.addStyleName(styleName);
    }

    public void eachItem(IEachElement<TreeItem> handler) {
        if (handler == null) {
            return;
        }
        for (int i = 0; i < childrenPanel.getWidgetCount(); i++) {
            TreeItem item = (TreeItem) childrenPanel.getWidget(i);
            boolean doNext = handler.each(item);
            if (!doNext) {
                return;
            }
        }
    }

    public void appendRightWidget(Widget widget) {
        appendRightWidget(widget, null);
    }

    public void appendRightWidget(Widget widget, Integer width) {
        assert widget != null;
        rightWidget.add(widget);
        if (width != null) {
            widget.setWidth(width + "px");
        }
    }

    public String getText() {
        return lbText.getText();
    }

    public void setText(String text) {
        lbText.setText(text);
    }

    public void clearRightWidget() {
        rightWidget.clear();
    }

    public void enableContextMenu(boolean enable) {
        if (enable) {
            if (downHandlerRegistration == null) {
                downHandlerRegistration = addDomHandler(downHandler, MouseDownEvent.getType());
            }
        } else {
            if (downHandlerRegistration != null) {
                downHandlerRegistration.removeHandler();
                downHandlerRegistration = null;
            }
        }
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

    interface TreeItemUiBinder extends UiBinder<HTMLPanel, TreeItem> {
    }
}