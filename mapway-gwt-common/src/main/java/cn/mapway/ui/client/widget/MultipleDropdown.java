package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.IOptionProvider;
import cn.mapway.ui.client.mvc.attribute.IOptionProviderCallback;
import cn.mapway.ui.client.mvc.attribute.Option;
import cn.mapway.ui.client.mvc.window.IErrorMessage;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Dropdown
 *
 * @author baoshuai <baoshuaiZealot@163.com>
 */
public class MultipleDropdown extends HorizontalPanel implements IOptionProviderCallback, IErrorMessage, HasValueChangeHandlers, ClickHandler, IData {
    Image icon;
    HTMLPanel content;
    FontIcon downArrow;
    PopupPanel popupPanel;
    VerticalPanel upPanel;
    List<DropdownItem> selecteds = new ArrayList<>();

    List<Object> selectDataList = new ArrayList<>();

    DropdownItem selected;
    String tip = "请选择";
    Object data;
    FontIcon fontIcon;
    private final CommonEventHandler itemClicked = event -> {
        if (event.isSelect()) {
            DropdownItem item = (DropdownItem) event.getSource();
            displayItem(item, true);
        }
    };
    Label label;
    Collection currentData;
    boolean enabled = true;
    int maxHeight = 300;

    public MultipleDropdown() {
        super();
        setWidth("100%");
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        setStyleName("ai-dropdown");
        label = new Label();
        label.setStyleName("label");
        downArrow = new FontIcon();
        downArrow.setIconUnicode(Fonts.DOWN);
        content = new HTMLPanel("");
        content.setStyleName("text-content-multiple");
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

    }

    public void setPopupMaxHeight(int maxHeight) {
        this.maxHeight = 300;
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

    public void displayItem(DropdownItem item, boolean fireEvents) {
        int size = selecteds.size();
        int i = -1;
        for (int index = 0; index < size; index++) {
            DropdownItem selected = selecteds.get(index);
            if (selected == item) {
                i = index;
                break;
            }
        }
        if (i == -1) {
            selecteds.add(item);
            item.setSelect(true);
            selectDataList.add(item.getData());
        } else {
            DropdownItem remove = selecteds.remove(i);
            selectDataList.remove(i);
            if(remove != null){
                remove.setSelect(false);
            }
        }

        if(fireEvents){
            MultipleDropdownEvent multipleDropdownEvent = new MultipleDropdownEvent(i == -1, item.getData(), selectDataList);
            ValueChangeEvent.fire(this, multipleDropdownEvent);
        }
        renderContentUi();
    }

    public void hideItem(DropdownItem item, boolean fireEvents) {
        int size = selecteds.size();
        int i = -1;
        for (int index = 0; index < size; index++) {
            DropdownItem selected = selecteds.get(index);
            if (selected == item) {
                i = index;
                break;
            }
        }
        if (i != -1) {
            selecteds.remove(i);
            selectDataList.remove(i);
            item.setSelect(false);
        }
        if(fireEvents){
            MultipleDropdownEvent multipleDropdownEvent = new MultipleDropdownEvent(false, item.getData(), selectDataList);
            ValueChangeEvent.fire(this, multipleDropdownEvent);
        }
        renderContentUi();
    }


    private void renderContentUi() {
        content.clear();
        if (selecteds.size() == 0) {
            content.add(new Label(tip));
        } else {
            for (DropdownItem item : selecteds) {
                if(item != null){
                    Label label = new Label(item.getText());
                    label.setStyleName("select-item");
                    content.add(label);
                }
            }
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

    public DropdownItem addItem(ImageResource icon, String name, Object value) {
        DropdownItem item = new DropdownItem(icon, name);
        item.setStyleName("dropdown-item");
        item.setData(value);
        item.addCommonHandler(itemClicked);
        item.setSuffixUnicode(Fonts.DONE);
        upPanel.add(item);
        return item;
    }

    public DropdownItem addItem(String iconFontUnicode, String name, Object value) {
        DropdownItem item = new DropdownItem(iconFontUnicode, name);
        item.setStyleName("dropdown-item");
        item.setData(value);
        item.addCommonHandler(itemClicked);
        item.setSuffixUnicode(Fonts.DONE);
        upPanel.add(item);
        return item;
    }

    public void clear() {
        upPanel.clear();
        content.clear();
        selecteds.clear();
        selectDataList.clear();
        if(tip != null){
            content.add(new Label(tip));
        }
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

    @Override
    public void onClick(ClickEvent event) {
        if (enabled) {
            if (upPanel.getOffsetWidth() < this.getOffsetWidth()) {
                upPanel.setWidth(this.getOffsetWidth() + "px");
            }

            int itemCount = upPanel.getWidgetCount();
            int height = itemCount * 32;
            if (height < 64) {
                height = 64;
            }
            if (height > maxHeight) {
                height = maxHeight;
            }
            popupPanel.setHeight(height + "px");
            popupPanel.showRelativeTo(this);
        }
    }

    public void setTip(String text) {
        tip = text;
        if (selected == null) {
            content.add(new Label(tip));
        }
    }

    public void setSelectedIndex(int index) {
        setSelectedIndex(index, true);
    }

    public void setSelectedIndex(int index, boolean fireEvent) {
        if (index >= 0 && index < upPanel.getWidgetCount()) {
            DropdownItem item = (DropdownItem) upPanel.getWidget(index);
            displayItem(item, fireEvent);
        }
    }

    public Object getValue(int index) {
        if (index >= 0 && index < upPanel.getWidgetCount()) {
            DropdownItem item = (DropdownItem) upPanel.getWidget(index);
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

    public String getSelectedText() {
        if (selected != null) {
            return selected.getText().trim();
        }
        return null;
    }

    /**
     * 设置值为选中状态
     *
     * @param value
     */
    public void setValue(Collection value) {
        setValue(value, true);
    }


    public void setValue(Collection value, boolean fireEvents) {
        currentData = value;
        if (value == null) {
            return;
        }

//        selectDataList

        for (int index = 0; index < upPanel.getWidgetCount(); index++) {
            DropdownItem item = (DropdownItem) upPanel.getWidget(index);
            Object o = item.getData();
            if (value.contains(o)) {
                if(!item.isSelect()){
                    displayItem(item, fireEvents);
                }
            } else {
                if(item.isSelect()){
                    hideItem(item, fireEvents);
                }
            }
        }
    }

    public void updateUI(boolean fireEvents) {
        setValue(currentData, fireEvents);
    }

    public Collection getCurrentData() {
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

    class MultipleDropdownEvent<T>{
        public MultipleDropdownEvent() {}

        public MultipleDropdownEvent(boolean flag, T target, List<T> selected) {
            this.flag = flag;
            this.target = target;
            this.selected = selected;
        }

        public boolean flag;

        public T target;

        public List<T> selected;

    }
}
