package cn.mapway.ui.client.widget.tree;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.decorator.ISelectable;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.UIConstants;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ImageTextItem
 * ImageTextItem 的样式表如下
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class ImageTextItem extends CommonEventComposite implements IData, HasDragHandlers, HasDragStartHandlers, HasDragEndHandlers, HasDragEnterHandlers, HasDragLeaveHandlers, HasDragOverHandlers, HasDropHandlers {


    private static final ImageTextItemUiBinder ourUiBinder = GWT.create(ImageTextItemUiBinder.class);

    @UiField
    Label lbText;
    @UiField
    Image icon;
    @UiField
    HTMLPanel root;
    @UiField
    VerticalPanel box;
    @UiField
    HTMLPanel childrenPanel;
    @UiField
    Label gap;
    List<ImageTextItem> children;
    @UiField
    FontIcon openClose;

    int level = 0;
    int command = 0;
    ImageTextItem parentItem;
    @UiField
    FontIcon fontIcon;
    List<Widget> rightWidgets = new ArrayList<>();
    @UiField
    FontIcon fontIconSuffix;
    private String storageKey = "";
    private Object data;
    private final DoubleClickHandler itemDoubleClicked = new DoubleClickHandler() {
        @Override
        public void onDoubleClick(DoubleClickEvent event) {
            fireEvent(CommonEvent.doubleClickEvent(getData()));
        }
    };
    private final ClickHandler itemClicked = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            int button=event.getNativeButton();
            if(button== NativeEvent.BUTTON_LEFT) {
                fireEvent(CommonEvent.selectEvent(getData()));
            }
            else if(button==NativeEvent.BUTTON_RIGHT)
            {
                fireEvent(CommonEvent.menuEvent(getData()));
            }
        }
    };

    public ImageTextItem() {
        this("", "");
    }

    public ImageTextItem(ImageResource resource, String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        children = new ArrayList<>();
        setValue(resource, text);
        root.addDomHandler(itemClicked, ClickEvent.getType());
        root.addDomHandler(itemDoubleClicked, DoubleClickEvent.getType());
        root.addDomHandler(event -> {
            event.stopPropagation();
            event.preventDefault();
        }, ContextMenuEvent.getType());
    }

    /**
     * 根据unnicode 创建条目
     *
     * @param fontIconUnicode
     * @param text
     */
    public ImageTextItem(String fontIconUnicode, String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        children = new ArrayList<>();
        setValue(fontIconUnicode, text);
        root.addDomHandler(itemClicked, ClickEvent.getType());
        root.addDomHandler(itemDoubleClicked, DoubleClickEvent.getType());
        root.addDomHandler(event -> {
            event.stopPropagation();
            event.preventDefault();
        }, ContextMenuEvent.getType());
    }

    @Override
    public void setStyleName(String style) {
        root.setStyleName(style);
    }

    public ImageTextItem getParentItem() {
        return parentItem;
    }

    public void setParentItem(ImageTextItem item) {
        parentItem = item;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    /**
     * 在条目的最后添加组件
     *
     * @param widget
     * @return
     */
    public ImageTextItem appendWidget(Widget widget, Integer width) {
        if (width != null) {
            widget.setWidth(width + "px");
        }
        root.add(widget);
        rightWidgets.add(widget);
        return this;
    }

    public ImageTextItem appendWidget(Widget widget) {
        return appendWidget(widget, 26);
    }

    public ImageTextItem addChild(String text, String unicode) {
        ImageTextItem item = new ImageTextItem(unicode, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        children.add(item);
        item.setParentItem(this);

        lbText.getElement().setAttribute("bold", "true");
        return item;
    }

    public ImageTextItem addChild(String text, ImageResource icon) {
        ImageTextItem item = new ImageTextItem(icon, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        children.add(item);
        item.setParentItem(this);
        lbText.getElement().setAttribute("bold", "true");
        return item;
    }

    public ImageTextItem insertChild(String text, ImageResource icon) {
        ImageTextItem item = new ImageTextItem(icon, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        children.add(item);
        item.setParentItem(this);
        lbText.getElement().setAttribute("bold", "true");
        return item;
    }

    public String attr(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        com.google.gwt.dom.client.Element element = getElement();
        if (element.hasAttribute(key)) {
            return element.getAttribute(key);
        }
        return null;
    }

    public void attr(String key, String value) {
        if (key == null || key.length() == 0) {
            return;
        }
        com.google.gwt.dom.client.Element element = getElement();
        if (value == null) {
            element.removeAttribute(key);
        } else {
            element.setAttribute(key, value);
        }
    }

    public String getText() {
        return lbText.getText();
    }

    public void setText(String text) {
        lbText.setText(text);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        gap.getElement().getStyle().setWidth(calPaddingLeft(), Style.Unit.PX);
    }

    private int calPaddingLeft() {
        int spacing = this.level * 20;
        return spacing + 4;
    }

    public ImageTextItem setTextStyle(String textStyle) {
        lbText.setStyleName(textStyle);
        return this;
    }

    public List<ImageTextItem> getChildren() {
        return children;
    }

    public ImageTextItem getLastChild() {
        if (children.size() > 0) {
            return children.get(children.size() - 1);
        }
        return null;
    }

    public ImageTextItem getFirstChild() {
        if (children.size() > 0) {
            return children.get(0);
        }
        return null;
    }

    public ImageResource getResource() {
        return null;
    }

    /**
     * 有可能为空
     *
     * @return
     */
    public Image getIcon() {
        return icon;
    }

    public void setIcon(String unicode) {
        icon.setVisible(false);
        fontIcon.setVisible(false);
        if (!StringUtil.isBlank(unicode)) {
            fontIcon.setVisible(true);
            fontIcon.setIconUnicode(unicode);
        }
    }

    public void setIconSuffix(String unicode) {
        fontIconSuffix.setVisible(false);
        fontIconSuffix.setVisible(false);
        if (!StringUtil.isBlank(unicode)) {
            fontIconSuffix.setVisible(true);
            fontIconSuffix.setIconUnicode(unicode);
        }
    }

    public String getIconSuffix() {
        if (fontIconSuffix.isVisible()) {
            return fontIconSuffix.getIconUnicode();
        } else {
            return "";
        }
    }

    public void setIcon(Image resource) {
        icon.setVisible(false);
        fontIcon.setVisible(false);
        if (resource != null) {
            icon.setVisible(true);
            icon.setUrl(resource.getUrl());
        }
    }

    public void setIcon(ImageResource resource) {
        icon.setVisible(false);
        fontIcon.setVisible(false);
        if (resource != null) {
            icon.setVisible(true);
            icon.setResource(resource);
        }
    }

    public void setColor(String color) {
        icon.getElement().getStyle().setColor(color);
        lbText.getElement().getStyle().setColor(color);
        openClose.getElement().getStyle().setColor(color);
    }

    public FontIcon getFontIcon() {
        return fontIcon;
    }

    public void setValue(Image resource, String text) {
        setIcon(resource);
        setText(text);
    }

    public void setValue(String iconUnicode, String text) {
        setIcon(iconUnicode);
        setText(text);
    }

    public void setValue(ImageResource resource, String text) {
        setIcon(resource);
        setText(text);
    }

    @Override
    public void setSelect(boolean b) {
        if (b) {
            root.getElement().setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
            lbText.getElement().setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
        } else {
            root.getElement().removeAttribute(ISelectable.SELECT_ATTRIBUTE);
            lbText.getElement().removeAttribute(ISelectable.SELECT_ATTRIBUTE);
        }
    }

    boolean enabled = true;

    public void setEnabled(boolean b) {
        enabled = b;
        if (b) {
            root.getElement().removeAttribute(UIConstants.DISABLED);
            lbText.getElement().removeAttribute(UIConstants.DISABLED);
        } else {
            root.getElement().setAttribute(UIConstants.DISABLED, "true");
            lbText.getElement().setAttribute(UIConstants.DISABLED, "true");
        }
    }

    public Label getLabel() {
        return lbText;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    public void setExpand(boolean open, boolean fireEvent) {
        if (open) {
            if (!childrenPanel.isVisible()) {
                if (null != openClose) {
                    openClose.setIconUnicode(Fonts.DOWN);
                    if (fireEvent) {
                        fireEvent(CommonEvent.openEvent(data));
                    }
                }
            }
        } else {
            if (childrenPanel.isVisible()) {
                if (null != openClose) {
                    openClose.setIconUnicode(Fonts.RIGHT);
                    if (fireEvent) {
                        fireEvent(CommonEvent.closeEvent(data));
                    }
                }
            }
        }
    }

    public void toggleChild() {
        boolean show = childrenPanel.isVisible();
        if (show) {
            if (null != openClose) {
                openClose.setIconUnicode(Fonts.RIGHT);
                fireEvent(CommonEvent.closeEvent(data));
            }
        } else {
            if (null != openClose) {
                openClose.setIconUnicode(Fonts.DOWN);
                fireEvent(CommonEvent.openEvent(data));
            }
        }
        childrenPanel.setVisible(!show);
    }

    /**
     * 清空所有的子节点
     */
    public void clear() {
        childrenPanel.clear();
        children.clear();
    }

    @Override
    public HandlerRegistration addDragHandler(DragHandler handler) {
        return addDomHandler(handler, DragEvent.getType());
    }

    @Override
    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return addDomHandler(handler, DragEndEvent.getType());
    }

    @Override
    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return addDomHandler(handler, DragStartEvent.getType());
    }

    @Override
    public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
        return addDomHandler(handler, DragEnterEvent.getType());
    }

    @Override
    public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
        return addDomHandler(handler, DragLeaveEvent.getType());
    }

    @Override
    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return addDomHandler(handler, DragOverEvent.getType());
    }

    @Override
    public HandlerRegistration addDropHandler(DropHandler handler) {
        return addDomHandler(handler, DropEvent.getType());
    }

    public void click() {
        innerClick(root.getElement());
    }

    private native void innerClick(Element element) /*-{
        element.click();
    }-*/;

    public void expand(boolean b) {
        childrenPanel.setVisible(b);
        if (b) {
            if (null != openClose) {
                openClose.setIconUnicode(Fonts.DOWN);
            }
        } else {
            if (null != openClose) {
                openClose.setIconUnicode(Fonts.RIGHT);
            }
        }
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String key) {
        storageKey = key;
    }

    @UiHandler("openClose")
    public void openCloseClick(ClickEvent event) {
        event.stopPropagation();
        event.preventDefault();
        toggleChild();
    }

    public void clearWidget() {

        for (Widget child : rightWidgets) {
            root.remove(child);
        }
        rightWidgets.clear();
    }

    interface ImageTextItemUiBinder extends UiBinder<VerticalPanel, ImageTextItem> {
    }
}
