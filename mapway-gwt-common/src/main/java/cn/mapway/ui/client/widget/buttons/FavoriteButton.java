package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;


/**
 * DeleteButton
 * 删除按钮
 *
 * @author zhangjianshe@gmail.com
 */
public class FavoriteButton extends FontIcon implements HasValue<Boolean> {
    boolean value;


    public FavoriteButton() {
        setValue(true);
        addClickHandler(event -> {
            event.stopPropagation();
            event.preventDefault();
            setValue(!value, true);
        });
    }

    /**
     * Adds a {@link ValueChangeEvent} handler.
     *
     * @param handler the handler
     * @return the registration for the event
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Gets this object's value.
     *
     * @return the object's value
     */
    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        this.value = value;
        if (value) {
            setIconUnicode(Fonts.FAVORITE_ON);
        } else {
            setIconUnicode(Fonts.FAVORITE_OFF);
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }
}
