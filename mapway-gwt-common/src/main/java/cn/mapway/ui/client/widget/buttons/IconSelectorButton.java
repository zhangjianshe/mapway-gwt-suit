package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.client.widget.icon.IconSelector;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class IconSelectorButton extends FontIcon implements HasValue<String> {

    public IconSelectorButton() {
        addClickHandler(event -> {
            showSelector();
        });
    }

    private void showSelector() {
        Popup<IconSelector> popup = IconSelector.getPopup(true);
        popup.addCommonHandler(commonEvent -> {
            if (commonEvent.isData()) {
                setValue(commonEvent.getValue(), true);
            } else if (commonEvent.isClear()) {
                setValue("", true);
            }
            popup.hide();
        });
        popup.showRelativeTo(this);
    }

    @Override
    public String getValue() {
        return getIconUnicode();
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        if (value == null || value.length() == 0) {
            value = Fonts.XUANZE;
        }
        setIconUnicode(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }


    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
