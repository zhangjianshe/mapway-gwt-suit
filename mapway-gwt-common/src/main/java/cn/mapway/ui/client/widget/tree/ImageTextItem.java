package cn.mapway.ui.client.widget.tree;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.window.ISelectable;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.UIConstants;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.MenuEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import elemental2.dom.DomGlobal;
import elemental2.svg.SVGAElement;
import elemental2.svg.SVGElement;
import jsinterop.base.Js;

import java.util.*;

/**
 * ImageTextItem
 * ImageTextItem 的样式表如下
 * <p>
 * <-----------loading bar---------------------------->
 * [gap][open.close.icon][checkbox][icon][label]
 * abc-openclose
 * abc-checkbox
 * abc-icon
 * abc-label
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class ImageTextItem extends CommonEventComposite implements IData, HasDragHandlers, HasDragStartHandlers, HasDragEndHandlers, HasDragEnterHandlers, HasDragLeaveHandlers, HasDragOverHandlers, HasDropHandlers {
    private static final Random random = new Random(System.currentTimeMillis());

    private static final ImageTextItemUiBinder ourUiBinder = GWT.create(ImageTextItemUiBinder.class);
    private final MouseDownHandler mouseDownClick = event -> {
        if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
            MenuEvent menuEvent = new MenuEvent(event.getNativeEvent(), ImageTextItem.this);
            fireEvent(CommonEvent.menuEvent(menuEvent));
        }
    };
    private final ValueChangeHandler<Boolean> checkHandler = event -> {
        if (event.getValue()) {
            fireEvent(CommonEvent.checkedEvent(ImageTextItem.this));
        } else {
            fireEvent(CommonEvent.unCheckedEvent(ImageTextItem.this));
        }
    };
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
    @UiField
    CheckBox check;
    @UiField
    HTMLPanel bar;
    @UiField
    MyStyle style;
    boolean enabled = true;
    private String storageKey = "";
    private Object data;
    boolean selectable;
    private final DoubleClickHandler itemDoubleClicked = event ->
    {
        if(selectable) {
            event.stopPropagation();
            event.preventDefault();
            fireEvent(CommonEvent.doubleClickEvent(getData()));
        }
    };
    private final ClickHandler itemClicked = event -> {
        if(selectable) {
            int button = event.getNativeButton();
            if (button == NativeEvent.BUTTON_LEFT) {
                fireEvent(CommonEvent.selectEvent(getData()));
            }
        }
    };

    /**
     * 设置该条目是否可以背选择
     * @param selectable
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        if(selectable){
            root.getElement().setAttribute("selectable","true");
        }
        else {
            root.getElement().removeAttribute("selectable");
        }
    }
    public ImageTextItem() {
        this("", "");
    }

    public ImageTextItem(ImageResource resource, String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        setStyleName("iti-default");
        children = new ArrayList<>();
        setValue(resource, text);
        installEvent();
        setSelectable(true);
    }
    public ImageTextItem(Image resource, String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        setStyleName("iti-default");
        children = new ArrayList<>();
        setValue(resource, text);
        installEvent();
        setSelectable(true);
    }

    /**
     * 根据unnicode 创建条目
     *
     * @param fontIconUnicode
     * @param text
     */
    public ImageTextItem(String fontIconUnicode, String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        setStyleName("iti-default");
        children = new ArrayList<>();
        setValue(fontIconUnicode, text);
        installEvent();
        setSelectable(true);
    }

    private void installEvent() {
        root.addDomHandler(itemClicked, ClickEvent.getType());
        root.addDomHandler(mouseDownClick, MouseDownEvent.getType());
        root.addDomHandler(itemDoubleClicked, DoubleClickEvent.getType());
        root.addDomHandler(event -> {
            event.stopPropagation();
            event.preventDefault();
        }, ContextMenuEvent.getType());
        check.addValueChangeHandler(checkHandler);

    }

    /**
     * * <-----------loading bar---------------------------->
     * * [gap][open.close.icon][checkbox][icon][label]
     * *  abc-openclose
     * *  abc-checkbox
     * *  abc-icon
     * *  abc-label
     *
     * @param styleName
     */
    @Override
    public void setStyleName(String styleName) {
        root.setStyleName(styleName);
        openClose.setStyleName("mapway-font " + styleName + "-openclose");
        check.addStyleName(styleName + "-checkbox");
        fontIcon.setStyleName("mapway-font " + styleName + "-icon");
        fontIconSuffix.setStyleName("mapway-font " + styleName + "-icon");
        lbText.setStyleName(styleName + "-label");
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
    public String clearContentAndGetText() {
        String text = lbText.getText();
        root.clear();
        return text;
    }
    public ImageTextItem appendWidget(Widget widget) {
        return appendWidget(widget, 26);
    }

    public ImageTextItem addChild(String text, String unicode) {
        ImageTextItem item = new ImageTextItem(unicode, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        item.setParentItem(this);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        item.enableCheck(check.isVisible());
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        children.add(item);
        lbText.getElement().setAttribute("bold", "true");
        return item;
    }
    public ImageTextItem addChild(String text, Image image) {
        ImageTextItem item = new ImageTextItem(image, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        item.setParentItem(this);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        item.enableCheck(check.isVisible());
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        children.add(item);
        lbText.getElement().setAttribute("bold", "true");
        return item;
    }
    public ImageTextItem addChild(String text, ImageResource icon) {
        ImageTextItem item = new ImageTextItem(icon, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        item.setParentItem(this);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        item.enableCheck(check.isVisible());
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        children.add(item);
        lbText.getElement().setAttribute("bold", "true");
        return item;
    }

    public ImageTextItem insertChild(String text, ImageResource icon) {
        ImageTextItem item = new ImageTextItem(icon, text);
        item.setStyleName(root.getStyleName());
        item.setStorageKey(storageKey + "/" + text);
        item.setParentItem(this);
        if (this.children.size() == 0) {
            openClose.setIconUnicode(Fonts.DOWN);
            openClose.setVisible(true);
        }
        childrenPanel.add(item);
        item.setLevel(getLevel() + 1);
        item.enableCheck(check.isVisible());
        children.add(item);
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

    public void enableCheck(boolean enabled) {
        check.setVisible(enabled);
        for (int i = 0; i < getChildren().size(); i++) {
            ImageTextItem item = getChildren().get(i);
            item.enableCheck(enabled);
        }
    }

    private int calPaddingLeft() {
        int spacing = this.level * 44;
        if (level > 0 && check.isVisible()) {
            spacing += 15;
        }
        return spacing;
    }

    /**
     * 第一级 需要根据所有第一级是否有图标来进行操作
     */
    public void adjustFirstLevelPosition(){

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

    public void setIcon(Image resource) {
        icon.setVisible(false);
        fontIcon.setVisible(false);
        if (resource != null) {
            icon.setVisible(true);
            icon.setUrl(resource.getUrl());
        }
    }

    public void setSvgIcon(String svgIcon,String text) {
        icon.setVisible(false);
        fontIcon.setVisible(true);
        if (!StringUtil.isBlank(svgIcon) && svgIcon.startsWith("<svg ")) {
            SVGElement svg = createSVG(svgIcon);
            svg.setAttribute("width","22");
            svg.setAttribute("height","22");
            fontIcon.getElement().appendChild(Js.uncheckedCast(svg));
        }
        else {
            fontIcon.setIconUnicode(Fonts.UNKNOWN);
        }
        setText(text);
    }
    private static SVGElement createSVG(String svgXMl) {
        elemental2.dom.Element element = DomGlobal.document.createElement("div");
        element.innerHTML = svgXMl;
        return Js.uncheckedCast(element.firstElementChild);
    }
    public void setIcon(ImageResource resource) {
        icon.setVisible(false);
        fontIcon.setVisible(false);
        if (resource != null) {
            icon.setVisible(true);
            icon.setResource(resource);
        }
    }

    public String getIconSuffix() {
        if (fontIconSuffix.isVisible()) {
            return fontIconSuffix.getIconUnicode();
        } else {
            return "";
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

    public void setColor(String color) {
        icon.getElement().getStyle().setColor(color);
        lbText.getElement().getStyle().setColor(color);
        openClose.getElement().getStyle().setColor(color);
    }

    public FontIcon getFontIcon() {
        return fontIcon;
    }

    public void setValue(Image image, String text) {
        setIcon(image);
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

    public void setChecked(boolean checked, boolean fireEvent) {
        if (check.isVisible()) {
            check.setValue(checked, fireEvent);
        }
    }

    public boolean isChecked() {
        return check.isVisible() && check.getValue();
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


    public void open(boolean openChild) {
        expand(openChild);
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

    /**
     * 设置一项的操作进度 如果为 null 或者不在 [0,100] 之间 就清楚进度信息
     *
     * @param progress
     */
    public void setProgress(Integer progress) {
        if (progress == null || progress < 0 || progress > 100) {
            bar.setVisible(false);
        } else {
            bar.setVisible(true);
            bar.removeStyleName(style.barAnimation());
            bar.setStyleName(style.bar());
            Style style = bar.getElement().getStyle();
            style.setWidth(progress, Style.Unit.PCT);
        }
    }

    public void loading(boolean loading) {
        if (loading) {
            bar.setVisible(true);
            bar.removeStyleName(style.bar());
            bar.setStyleName(style.barAnimation());
            bar.getElement().getStyle().setProperty("animationDelay", random.nextDouble() + "s");
        } else {
            bar.setVisible(false);
            bar.removeStyleName(style.barAnimation());
            bar.setStyleName(style.bar());
        }
    }

    public Label getTextLabel() {
        return lbText;
    }

    public HTMLPanel getChildrenPanel() {
        return childrenPanel;
    }

    public void sortItem(Comparator<ImageTextItem> sort) {
        for (ImageTextItem child : children) {
            child.sortItem(sort);
        }
        if (sort != null && children.size() > 1) {
            String t = lbText.getText();
            Logs.info(lbText.getText());
            childrenPanel.clear();
            Collections.sort(children, sort);
            for (ImageTextItem item : children) {
                childrenPanel.add(item);
            }
        }
    }

    /**
     * 获取可见的项目数 包含本条以及子条目
     * @return
     */
    public int getVisibleCount() {

        int count = 1;
        if(childrenPanel.isVisible()) {
            for (ImageTextItem item : children) {
                count += item.getVisibleCount();
            }
        }
        return count;
    }

    public interface MyStyle extends CssResource {

        String bar();

        String gap();

        String ic();

        String barAnimation();
    }

    interface ImageTextItemUiBinder extends UiBinder<VerticalPanel, ImageTextItem> {
    }
}
