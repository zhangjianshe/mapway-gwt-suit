package cn.mapway.ui.client.widget.buttons;


import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * DeleteButton
 * 删除按钮
 *
 * @author zhangjianshe@gmail.com
 */
public class ShowButton extends FontIcon implements HasValueChangeHandlers<Boolean> {
    public ShowButton() {
        setIconUnicode(Fonts.VISIBILITY);
        addDomHandler(event -> {
            event.stopPropagation();
            event.preventDefault();
            setValue(!getValue(), true);
        }, ClickEvent.getType());
    }

    public boolean getValue() {
        return getIconUnicode().equals(Fonts.VISIBILITY);
    }

    public void setValue(Boolean show) {
        setValue(show, false);
    }

    public void setValue(Boolean show, boolean fireEvent) {
        if (show) {
            setIconUnicode(Fonts.VISIBILITY);
        } else {
            setIconUnicode(Fonts.HIDDEN);
        }
        if (fireEvent) {
            ValueChangeEvent.fire(this, show);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
