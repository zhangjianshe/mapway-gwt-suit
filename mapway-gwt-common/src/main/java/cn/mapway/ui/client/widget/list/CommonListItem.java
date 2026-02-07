package cn.mapway.ui.client.widget.list;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;

public class CommonListItem extends CommonEventComposite implements IData<Object> {
    private static final CommonListItemUiBinder ourUiBinder = GWT.create(CommonListItemUiBinder.class);
    @UiField
    FontIcon icon;
    @UiField
    Label label;
    @UiField
    HorizontalPanel rightPanel;
    Object obj;
    String oldValue;
    boolean editable = false;

    public CommonListItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addDomHandler(event -> {
            fireEvent(CommonEvent.selectEvent(obj));
        }, ClickEvent.getType());

        addDomHandler(event -> {
            if (editable) {
                event.stopPropagation();
                event.preventDefault();
                HTMLElement element = Js.uncheckedCast(label.getElement());
                element.setAttribute("contenteditable", true);
                element.focus();
            }
        }, DoubleClickEvent.getType());

        label.addDomHandler(event -> {
            checkEditor();
        }, BlurEvent.getType());

        label.addDomHandler(event -> {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER
                    || event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                event.preventDefault();
                event.stopPropagation();
                label.getElement().blur();
            }
        }, KeyDownEvent.getType());
    }

    private void checkEditor() {
        HTMLElement element = Js.uncheckedCast(label.getElement());
        element.removeAttribute("contenteditable");
        String text = element.textContent;
        if (text != null && text.length() > 0 && !text.equals(oldValue)) {
            String[] data = new String[2];
            data[0] = oldValue;
            data[1] = text;
            fireEvent(CommonEvent.renameEvent(data));
        } else {
            setText(oldValue);
        }
    }

    public void setUnicode(String unicode) {
        icon.setIconUnicode(unicode);
    }

    public void setText(String text) {
        oldValue = text;
        label.setText(text);
    }

    /**
     * @return
     */
    @Override
    public Object getData() {
        return obj;
    }

    /**
     * @param o
     */
    @Override
    public void setData(Object o) {
        this.obj = o;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void reset() {
        setText(oldValue);
    }


    public void appendRightWidget(Widget w) {
        if (w != null) {
            rightPanel.insert(w, 0);
        }
    }

    public Label getLabel() {
        return label;
    }

    public Widget getRightWidget(int index) {
        if (index >= 0 && index < rightPanel.getWidgetCount()) {
            return rightPanel.getWidget(index);
        }
        return null;
    }

    interface CommonListItemUiBinder extends UiBinder<HTMLPanel, CommonListItem> {
    }
}