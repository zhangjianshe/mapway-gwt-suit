package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.user.client.ui.TextBox;

/**
 * AiTextBox
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class AiTextBox extends TextBox implements IData {
    Object data;

    String defaultValue = null;

    public AiTextBox() {
        super();
        setStyleName("ai-input");
    }


    public void asNumber() {
        setAttribute("type", "number");
    }

    public void asEmail() {
        setAttribute("type", "email");
    }

    public void asDate() {
        setAttribute("type", "date");
    }

    public void asColor() {
        setAttribute("type", "color");
    }

    public void asDateTime() {
        setAttribute("type", "datetime");
    }

    public void asRange() {
        setAttribute("type", "range");
    }
    public void asPassword() {
        setAttribute("type", "password");
    }

    public void setPlaceholder(String placeholder) {
        setAttribute("placeholder", placeholder);
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    public void setMin(int min) {
        setAttribute("min", min + "");
    }

    public void setMax(int max) {
        setAttribute("max", max + "");
    }

    public void setStep(int step) {
        setAttribute("step", step + "");
    }

    public void setDoubleStep(double step) {
        setAttribute("step", step + "");
    }

    public void disableAutocomplete() {
        setAttribute("autocomplete", "new-thing");
    }
    public void setDefaultValue(String value) {
        defaultValue = value;
    }

    public String getValue() {
        String value = super.getValue();
        if(StringUtil.isBlank(value) && defaultValue!=null){
            if(defaultValue.contains("$random$")){
                return defaultValue.replace("$random$", "" + System.currentTimeMillis());
            }
            return defaultValue;
        } else {
            return value;
        }
    }

    private void setAttribute(String key, String value) {
        getElement().setAttribute(key, value);
    }

}
