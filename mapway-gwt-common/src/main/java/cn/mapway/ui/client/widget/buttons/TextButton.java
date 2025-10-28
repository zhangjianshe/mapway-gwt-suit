package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.AiLabel;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.Tip;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * TextButton
 *
 * @author zhang
 */
public class TextButton extends CommonEventComposite implements ContextMenuHandler, HasClickHandlers, ClickHandler, MouseOverHandler, MouseOutHandler, IData {
    private static final BigButtonUiBinder ourUiBinder = GWT.create(BigButtonUiBinder.class);
    @UiField
    FontIcon icon;
    @UiField
    AiLabel label;
    @UiField
    HorizontalPanel root;
    SimpleEventBus customEventBus = new SimpleEventBus();
    HandlerRegistration outHandler;
    HandlerRegistration overHandler;
    Object data;

    /**
     * TextButton
     *
     * @param unicode 参见 FontIcons常量
     * @param text
     */
    @UiConstructor
    public TextButton(String unicode, String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        if (unicode == null || unicode.length() == 0) {
            icon.setVisible(false);
        } else {
            icon.setIconUnicode(unicode);
            icon.setVisible(true);
        }
        label.setText(text);
        addDomHandler(this, ClickEvent.getType());
        addDomHandler(this, ContextMenuEvent.getType());
    }

    public void setFontSize(int size) {
        icon.setSize(size, Style.Unit.PX);
    }

    @Override
    public void setTitle(String title) {

        if (title != null && title.length() > 0) {
            if (outHandler == null) {
                //安装提示
                outHandler = addDomHandler(this, MouseOutEvent.getType());
                overHandler = addDomHandler(this, MouseOverEvent.getType());
            }
        } else {
            if (outHandler != null) {
                outHandler.removeHandler();
                overHandler.removeHandler();
                outHandler = null;
                overHandler = null;
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        icon.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    public void setIconUnicode(String unicode) {
        icon.setIconUnicode(unicode);
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setEnableText(boolean enabled) {
        if (enabled) {
            root.remove(label);
        } else {
            root.add(label);
        }
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return customEventBus.addHandler(ClickEvent.getType(), handler);
    }

    @Override
    public void onClick(ClickEvent event) {
        event.stopPropagation();
        event.preventDefault();
        if (getEnabled()) {
            customEventBus.fireEvent(event);
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.stopPropagation();
        event.preventDefault();
        Tip.hideTip();
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        event.stopPropagation();
        event.preventDefault();
        String title = getTitle();
        if (title != null && title.length() > 0) {
            Tip.showTip(this, title, Tip.LOCATION_BOTTOM);
        }
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    @Override
    public void onContextMenu(ContextMenuEvent event) {
        event.stopPropagation();
        event.preventDefault();
        ContextData contextEvent = new ContextData();
        contextEvent.setData(data);
        NativeEvent nativeEvent = event.getNativeEvent();
        contextEvent.setX(nativeEvent.getClientX());
        contextEvent.setY(nativeEvent.getClientY());
        fireEvent(CommonEvent.contextEvent(contextEvent));
    }

    interface BigButtonUiBinder extends UiBinder<HorizontalPanel, TextButton> {
    }
}
