package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.tools.IData;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class AiCheckBox extends Label implements IData, HasValue<Boolean>, HasAllFocusHandlers {
    Object data;
    boolean value = false;
    String name;

    boolean enabled = true;

    public AiCheckBox() {
        this("");
    }

    public AiCheckBox(String text) {
        setStyleName("ai-checkbox");
        addDomHandler(e -> {
            if(!enabled){
                return;
            }
            if (e.getRelativeX(this.getElement()) < 50) {
                setValue(!value, true);
            }
        }, ClickEvent.getType());
        setText(text);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        this.data = obj;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, false);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        if(value==null){
            value = false;
        }
        this.value = value;
        if (value) {
            getElement().setAttribute("v-data", "true");
        } else {
            getElement().removeAttribute("v-data");
        }
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }
}
