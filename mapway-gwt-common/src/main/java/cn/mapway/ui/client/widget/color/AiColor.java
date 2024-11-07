package cn.mapway.ui.client.widget.color;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.Colors;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.dialog.Popup;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

public class AiColor extends Label implements IData<Object>, HasValue<String>, HasValueChangeHandlers<String>, HasSelectionHandlers<String> {

    Object data;
    int rgba;
    boolean autoOpenDialog = true;

    public AiColor() {
        setStyleName("ai-input");
        setWidth("100%");
        Style style = getElement().getStyle();
        style.setOverflow(Style.Overflow.HIDDEN);
        style.setTextOverflow(Style.TextOverflow.ELLIPSIS);
        style.setCursor(Style.Cursor.DEFAULT);
        addClickHandler(e -> {
            if (autoOpenDialog) {
                Popup<ColorChooser> dialog = ColorChooser.getPopup(true);
                dialog.showRelativeTo(this);
                dialog.addCommonHandler(commonEvent -> {
                    if (commonEvent.isColor()) {
                        ColorData data = commonEvent.getValue();
                        String rgba = Colors.toRGBA(data.getColor());
                        setValue(rgba, true);
                    } else {
                        dialog.hide();
                    }
                });

                ColorData colorData = new ColorData(rgba);
                dialog.getContent().setData(colorData);
            } else {
                //不弹出对话框
                SelectionEvent.fire(this, Colors.toRGBA(rgba));
            }
        });
    }

    public void setAutoOpenDialog(boolean autoOpenDialog) {
        this.autoOpenDialog = autoOpenDialog;
    }

    @Override
    public String getValue() {
        return Colors.toRGBA(rgba);
    }

    /**
     * color is 0xRRGGBBAA
     *
     * @param color
     */
    @Override
    public void setValue(String color) {
        setValue(color, false);
    }

    private void setBackground(Integer color) {
        Style style = getElement().getStyle();
        String start = Colors.toRGBA(color);
        String back = "linear-gradient(90deg," + start + " 0%," + start + " 100%),url('img/transparent.png') ";
        style.setProperty("background", back);
        String colorString;
        if ((Colors.a(color) & 0xFF) < 0x80) {
            colorString = "black";
        } else {
            colorString = Colors.toRGBA(Colors.invertColor(color, true));
        }
        style.setColor(colorString);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        if (StringUtil.isBlank(value)) {
            value = "rgba(0,0,0,1)";
        }
        if (value.startsWith("#")) {
            rgba = Colors.fromHex(value);
        } else {
            rgba = Colors.fromRGBA(value);
        }
        setBackground(rgba);
        setText(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, Colors.toRGBA(rgba));
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    /**
     * @return
     */
    @Override
    public Object getData() {
        return data;
    }

    /**
     * @param o
     */
    @Override
    public void setData(Object o) {
        data = o;
    }
}
