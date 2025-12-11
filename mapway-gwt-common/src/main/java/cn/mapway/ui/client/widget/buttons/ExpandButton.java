package cn.mapway.ui.client.widget.buttons;


import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class ExpandButton extends FontIcon implements ClickHandler, HasValueChangeHandlers<Boolean> {
    String current;
    boolean upAndDown = false;
    public ExpandButton() {
        current = Fonts.DOUBLE_RIGHT;
        setIconUnicode(current);
        addClickHandler(this);
    }

    public void setUpAndDown(boolean upAndDown)
    {
        this.upAndDown = upAndDown;
        updateUI();
    }

    private void updateUI() {
        if (upAndDown) {
            if(current.equals(Fonts.DOUBLE_RIGHT)) {
                current = Fonts.UP;
            }
            else {
                current = Fonts.DOWN;
            }
        }
        else {
            if(current.equals(Fonts.UP)) {
                current = Fonts.DOUBLE_LEFT;
            }
            else {
                current = Fonts.DOUBLE_RIGHT;
            }
        }
        setIconUnicode(current);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void onClick(ClickEvent event) {
        if(upAndDown) {
            if (current.equals(Fonts.UP)) {
                current = Fonts.DOWN;
            } else {
                current = Fonts.UP;
            }
        }
        else {
            if (current.equals(Fonts.DOUBLE_LEFT)) {
                current = Fonts.DOUBLE_RIGHT;
            } else {
                current = Fonts.DOUBLE_LEFT;
            }
        }
        setIconUnicode(current);
        ValueChangeEvent.fire(this, current.equals(Fonts.DOUBLE_RIGHT) || current.equals(Fonts.DOWN));
    }
}
