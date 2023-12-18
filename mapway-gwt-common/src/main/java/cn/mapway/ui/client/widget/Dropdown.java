package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IOptionProvider;
import cn.mapway.ui.client.mvc.attribute.IOptionProviderCallback;
import cn.mapway.ui.client.mvc.attribute.Option;
import cn.mapway.ui.client.mvc.decorator.IErrorMessage;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

import java.util.List;
import java.util.Objects;

/**
 * Dropdown
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class Dropdown extends HorizontalPanel implements IOptionProviderCallback, IErrorMessage, HasValueChangeHandlers, ClickHandler, IData, HasValue {
    Image icon;
    Label content;
    FontIcon downArrow;
    PopupPanel popupPanel;
    VerticalPanel upPanel;
    ImageTextItem selected = null;
    String tip = "请选择";
    Object data;
    FontIcon fontIcon;
    private final CommonEventHandler itemClicked = event -> {
        if (event.isSelect()) {
            ImageTextItem item = (ImageTextItem) event.getSource();
            displayItem(item, true);
        }
    };
    Label label;
    Object currentData;
    boolean enabled = true;

    public Dropdown() {
        super();
        setWidth("100%");
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        setStyleName("ai-dropdown");
        label = new Label();
        label.setStyleName("label");
        downArrow = new FontIcon();
        downArrow.setIconUnicode(Fonts.DOWN);
        content = new Label();
        content.setStyleName("text-content");
        content.setWidth("100%");
        this.add(label);
        this.add(content);
        this.add(downArrow);
        this.setCellHorizontalAlignment(downArrow, HasHorizontalAlignment.ALIGN_RIGHT);
        popupPanel = new PopupPanel();
        ScrollPanel scrollPanel = new ScrollPanel();
        popupPanel.setWidget(scrollPanel);
        upPanel = new VerticalPanel();
        scrollPanel.add(upPanel);
        this.addDomHandler(this, ClickEvent.getType());
        downArrow.addClickHandler(this);
        popupPanel.setAutoHideEnabled(true);
        //缺省最大300像素高
        setPopupMaxHeight(300);
    }

    public void setPopupMaxHeight(int maxHeight) {
        popupPanel.getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
    }

    public void setLabel(String txt) {
        if (txt == null || txt.length() == 0) {
            this.setCellWidth(label, "0px");
        } else {
            label.setText(txt);
        }
    }

    public void setLabelWidth(int width) {
        this.setCellWidth(label, width + "px");
    }

    public void displayItem(ImageTextItem item, boolean fireEvents) {
        if (selected != null) {
            selected.setSelect(false);
        }
        selected = item;
        if (selected != null) {
            content.setText(selected.getText());
            if (fireEvents) {
                ValueChangeEvent.fire(this, selected.getData());
            }
            popupPanel.hide();
            if (selected.getFontIcon() != null) {
                setIcon(selected.getFontIcon().getIconUnicode());
            } else {
                setIcon(selected.getResource());
            }
            selected.setSelect(true);
        } else {
            content.setText(tip);
            setIcon("");
        }
    }

    public void setIcon(ImageResource resource) {
        if (fontIcon != null) {
            this.setCellWidth(fontIcon, "0px");
        }
        if (resource == null) {
            if (icon != null) {
                this.setCellWidth(icon, "0px");
            }
        } else {
            if (icon == null) {
                icon = new Image();
                icon.setPixelSize(18, 18);
                icon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.BOTTOM);
                this.insert(icon, 0);
                this.setCellWidth(icon, "22px");
            }
            icon.setResource(resource);
        }
    }

    public void setIcon(String unicode) {
        if (icon != null) {
            icon.removeFromParent();
            this.setCellWidth(icon, "0px");
        }
        if (unicode == null || unicode.isEmpty()) {
            if (fontIcon != null) {
                this.setCellWidth(fontIcon, "0px");
            }
        } else {
            if (fontIcon == null) {
                fontIcon = new FontIcon();
                fontIcon.addStyleName("ai-gutter-right ai-gutter-left");
                fontIcon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.BOTTOM);
                this.insert(fontIcon, 0);
                this.setCellWidth(fontIcon, "22px");
            }
            fontIcon.setIconUnicode(unicode);
        }
    }

    public ImageTextItem addItem(ImageResource icon, String name, Object value) {
        ImageTextItem item = new ImageTextItem(icon, name);
        item.setStyleName("dropdown-item");
        item.setData(value);
        item.addCommonHandler(itemClicked);
        upPanel.add(item);
        return item;
    }

    public ImageTextItem addItem(String iconFontUnicode, String name, Object value) {
        ImageTextItem item = new ImageTextItem(iconFontUnicode, name);
        item.setStyleName("dropdown-item");
        item.setData(value);
        item.addCommonHandler(itemClicked);
        upPanel.add(item);
        return item;
    }

    public void clear() {
        upPanel.clear();
        content.setText("");
    }

    public int getItemCount() {
        return upPanel.getWidgetCount();
    }

    public Widget getItemWidget(int index) {
        if (index >= 0 && index < upPanel.getWidgetCount()) {
            return upPanel.getWidget(index);
        }
        return null;
    }

    @Override
    public void setErrorMessage(String message) {
        if (message == null || message.length() == 0) {
            getElement().removeAttribute(UIConstants.ERROR_MSG_KEY);
        } else {
            getElement().setAttribute(UIConstants.ERROR_MSG_KEY, message);
        }
    }

    public void setText(String text) {
        content.setText(text);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (enabled) {
            if (upPanel.getOffsetWidth() < this.getOffsetWidth()) {
                upPanel.setWidth(this.getOffsetWidth() + "px");
            }
            popupPanel.showRelativeTo(this);
        }
    }

    public void setTip(String text) {
        tip = text;
        if (selected == null) {
            content.setText(tip);
        }
    }

    public void setSelectedIndex(int index) {
        setSelectedIndex(index, true);
    }

    public void setSelectedIndex(int index, boolean fireEvent) {
        if (index >= 0 && index < upPanel.getWidgetCount()) {
            ImageTextItem item = (ImageTextItem) upPanel.getWidget(index);
            displayItem(item, fireEvent);
        }
    }

    public Object getValue(int index) {
        if (index >= 0 && index < upPanel.getWidgetCount()) {
            ImageTextItem item = (ImageTextItem) upPanel.getWidget(index);
            return item.getData();
        }
        return null;
    }

    public Object getValue() {
        if (selected != null) {
            return selected.getData();
        }
        return null;
    }

    /**
     * 设置值为选中状态
     *
     * @param value
     */
    @Override
    public void setValue(Object value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Object value, boolean fireEvents) {
        currentData = value;
        if (value == null) {
            displayItem(null, fireEvents);
            return;
        }
        for (int index = 0; index < upPanel.getWidgetCount(); index++) {
            ImageTextItem item = (ImageTextItem) upPanel.getWidget(index);
            Object o = item.getData();
            if (Objects.equals(value, o)) {
                displayItem(item, fireEvents);
                break;
            }
        }
    }

    public void updateUI(boolean fireEvents) {
        setValue(currentData, fireEvents);
    }

    public Object getCurrentData() {
        return currentData;
    }

    public void setEnabled(boolean b) {
        enabled = b;
        if (b) {
            this.getElement().removeAttribute(UIConstants.DISABLED);
        } else {
            this.getElement().setAttribute(UIConstants.DISABLED, "true");
        }
    }


    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setOptionProvider(IOptionProvider optionProvider) {
        if (optionProvider == null) {
            return;
        }
        List<Option> options = optionProvider.getOptions();
        optionProvider.setCallback(this);
        setOptions(options);
    }

    @Override
    public void setOptions(List<Option> options) {
        this.clear();
        if (options == null) {
            return;
        }
        int selectedIndex = 0;
        int index = 0;
        for (Option option : options) {
            this.addItem(option.getIcon(), option.getText(), option.getValue());
            if (option.isInitSelected()) {
                selectedIndex = index;
            }
            index++;
        }
        if (selectedIndex >= 0 && selectedIndex < options.size()) {
            setSelectedIndex(selectedIndex);
        }
    }
}
