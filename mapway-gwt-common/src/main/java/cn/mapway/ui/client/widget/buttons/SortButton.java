package cn.mapway.ui.client.widget.buttons;


import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * SortButton
 * 排序按钮 再 ASC DESC NONE 三种状态间切换
 *
 * @author zhangjianshe@gmail.com
 */
public class SortButton extends FontIcon implements HasValueChangeHandlers<Integer>, HasValue<Integer> {
    public static final int ASC = 0;
    public static final int DESC = 1;
    public static final int NONE = 2;
    int state;
    private final ClickHandler changeStateHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            event.stopPropagation();
            event.preventDefault();
            int temp = state;
            if (temp == ASC) {
                temp = DESC;
            } else if (temp == DESC) {
                temp = NONE;
            } else {
                temp = ASC;
            }
            setValue(temp, true);
        }
    };

    public SortButton() {
        setIconUnicode(Fonts.SORT_VARIANT);
        state = NONE;
        addDomHandler(changeStateHandler, ClickEvent.getType());
    }

    private void fireValueChangeEvent() {
        ValueChangeEvent.fire(this, state);
    }

    @Override
    public Integer getValue() {
        return state;
    }

    @Override
    public void setValue(Integer value) {
        setValue(value, false);
    }

    public void setValue(Integer value, boolean fireEvent) {
        if (value < 0 || value > NONE) {
            value = NONE;
        }
        if (state != value) {
            state = value;
            if (fireEvent) {
                fireValueChangeEvent();
            }
            if (state == ASC) {
                setIconUnicode(Fonts.SORT_ASCENDING);
            } else if (state == DESC) {
                setIconUnicode(Fonts.SORT_DESCENDING);
            } else {
                setIconUnicode(Fonts.SORT_VARIANT);
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
