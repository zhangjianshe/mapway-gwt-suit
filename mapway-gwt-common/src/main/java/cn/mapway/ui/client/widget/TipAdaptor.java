package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * TipAdaptor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class TipAdaptor implements MouseOverHandler, MouseOutHandler, MouseDownHandler {

    Widget relObject;
    HandlerRegistration overHandler;
    HandlerRegistration outHandler;
    HandlerRegistration mouseDownHandler;
    String tipInfo;
    String tipDirection = "bottom";

    public TipAdaptor(Widget widget) {
        relObject = widget;
    }

    public void setTipDirection(String direction) {
        if (StringUtil.isBlank(direction)) {
            return;
        }
        tipDirection = direction;
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        Tip.hideTip();
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        if (tipInfo != null && !tipInfo.isEmpty()) {
            Tip.showTip(relObject, tipInfo, tipDirection);
        }
    }

    public void setTitle(String title) {
        if (StringUtil.isBlank(title)) {
            tipInfo = "";
            if (overHandler != null) {
                overHandler.removeHandler();
                overHandler = null;
            }
            if (outHandler != null) {
                outHandler.removeHandler();
                outHandler = null;
            }
            if (mouseDownHandler != null) {
                mouseDownHandler.removeHandler();
                mouseDownHandler = null;
            }
        } else {
            tipInfo = title;
            if (outHandler == null) {
                if (relObject instanceof HasMouseOutHandlers) {
                    HasMouseOutHandlers hasMouseOutHandlers = (HasMouseOutHandlers) relObject;
                    outHandler = hasMouseOutHandlers.addMouseOutHandler(this);
                }
            }
            if (overHandler == null) {
                if (relObject instanceof HasMouseOverHandlers) {
                    HasMouseOverHandlers hasMouseOverHandlers = (HasMouseOverHandlers) relObject;
                    overHandler = hasMouseOverHandlers.addMouseOverHandler(this);
                }
            }
            if (mouseDownHandler == null) {
                if (relObject instanceof HasMouseDownHandlers) {
                    HasMouseDownHandlers hasMouseDownHandlers = (HasMouseDownHandlers) relObject;
                    mouseDownHandler = hasMouseDownHandlers.addMouseDownHandler(this);
                }
            }
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        Tip.hideTip();
    }
}
