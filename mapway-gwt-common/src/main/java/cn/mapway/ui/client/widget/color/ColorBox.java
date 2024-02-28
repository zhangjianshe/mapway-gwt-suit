package cn.mapway.ui.client.widget.color;

import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBox;

import java.util.List;

import static java.lang.Integer.parseInt;


/**
 * The Class ColorLabel.
 */
public class ColorBox extends TextBox implements HasCommonHandlers {

    private final CommonEventHandler m_color_event = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isColors()) {
                List<String> colors = (List<String>) event.getValue();
                setValue(colors.get(0));
                fireEvent(CommonEvent.colorEvent(colors.get(0)));
            }
        }
    };
    /**
     * The popup.
     */
    private final ColorPopup popup;


    /**
     * Instantiates a new color label.
     */
    public ColorBox() {
        setStyleName("ai-input");
        this.popup = new ColorPopup();

        popup.addCommonHandler(m_color_event);
        addFocusHandler(event -> {
            popup.setHex(getText());
            popup.setAutoHideEnabled(true);
            popup.setPopupPosition(getAbsoluteLeft() + 10, getAbsoluteTop() + 10);
            popup.showRelativeTo(ColorBox.this);
        });
        addChangeHandler(event -> {
            String text = getText();
            if (!StringUtil.isBlank(text)) {
                try {
                    if (text.startsWith("#")) {
                        setValue(text);
                    } else if (text.startsWith("rgb(")) {
                        setValue(rgbToNumber(text));
                    }
                } catch (Exception e){}
            }
        });
        addKeyDownHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                String v = super.getValue();
                if (!StringUtil.isBlank(v)) {
                    if (v.startsWith("#")) {
                        setValue(v);
                    } else if (v.startsWith("rgb(")) {
                        setValue(rgbToNumber(v));
                    }
                }
            }
        });

    }

    private static String rgbToNumber(String color) {
        if (StringUtil.isBlank(color)) {
            return "#000000";
        } else if (color.startsWith("rgb(")) {
            String temp = color.substring(4, color.length() - 1);
            String[] vs = temp.split(",");

            if (vs.length == 3) {
                int r = parseInt(vs[0].trim(), 10);
                int g = parseInt(vs[1].trim(), 10);
                int b = parseInt(vs[2].trim(), 10);
                return "#" + toHex(r) + toHex(g) + toHex(b);

            } else {
                return color;
            }
        } else {
            return color;
        }
    }

    private static String toHex(Integer c) {
        String hex = Integer.toHexString(c);
        if (hex.length() == 1) {
            return "0" + hex;
        } else if (hex.length() == 2) {
            return hex;
        } else {
            return hex.substring(hex.length() - 2);
        }
    }

    @Override
    public String getValue() {
        Style style = getElement().getStyle();
        return style.getBackgroundColor();
    }

    @Override
    public void setValue(String value) {
        if(checkColor(value)){
            Style style = getElement().getStyle();
            style.setBackgroundColor(value);
            Color color = new Color(value);
            style.setColor(color.getLightness() < 0.5 ? "#ffffff" : "#000000");
            super.setValue(value);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();

    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }


    private boolean checkColor(String value){
        try{
            if(value.startsWith("rgb(")){
               value = rgbToNumber(value);
            }
            if(value.length() == 7 && value.startsWith("#")){
                String r = value.substring(1, 3);
                String g = value.substring(3, 5);
                String b = value.substring(5, 7);

                Integer.parseInt(r,16);
                Integer.parseInt(g,16);
                Integer.parseInt(b,16);
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}
