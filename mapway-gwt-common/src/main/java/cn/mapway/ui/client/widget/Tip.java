package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * Tip
 * 显示提示弹出框，全局只有一个
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class Tip extends PopupPanel implements PopupPanel.PositionCallback {

    public final static String LOCATION_BOTTOM = "bottom";
    public final static String LOCATION_TOP = "top";
    public final static String LOCATION_RIGHT = "right";
    public final static String LOCATION_LEFT = "left";
    static Tip globalTip;
    HTML lb;
    Label lb1;
    String tipDirection = LOCATION_BOTTOM;
    UIObject relative;
    private Element element;

    public Tip() {
        lb = new HTML();
        HTMLPanel hp = new HTMLPanel("");
        this.setStyleName("ai-tip");
        lb.setStyleName("ai-tip-message");
        lb1 = new Label(" ");
        lb1.setStyleName("indicator");
        hp.add(lb1);
        hp.add(lb);
        this.setWidget(hp);

    }

    /**
     * 全局过去TIP窗口
     *
     * @return
     */
    public static Tip getTip() {
        if (globalTip == null) {
            globalTip = new Tip();
        }
        return globalTip;
    }

    public static void showTip(UIObject widget, String message, String tipDirection) {
        getTip().innerShowTip(widget, message, tipDirection);
    }

    public static void showTip(Element element, String message, String tipDirection) {
        getTip().innerShowTip(element, message, tipDirection);
    }

    public static void hideTip() {
        if (globalTip != null) {
            globalTip.hide(true);
        }
    }

    /**
     * 支持 top right bottom left
     *
     * @param direction
     */
    public void setTipDirection(String direction) {
        if (StringUtil.isBlank(direction)) {
            return;
        }
        this.tipDirection = direction;
    }

    private void innerShowTip(UIObject relativeObject, String message, String tipDirection) {
        lb.setHTML(message);
        this.setTipDirection(tipDirection);
        this.relative = relativeObject;
        setPopupPositionAndShow(this::setPosition);
    }

    private void innerShowTip(Element relativeObject, String message, String tipDirection) {
        lb.setHTML(message);
        this.setTipDirection(tipDirection);
        // this.relative = relativeObject;
        this.element = relativeObject;
        setPopupPositionAndShow(this::setPosition);
    }

    @Override
    public void setPosition(int offsetWidth, int offsetHeight) {
        int relWidth;
        int relHeight;
        int relLeft;
        int relTop;
        if (relative != null) {
            relWidth = relative.getOffsetWidth();
            relHeight = relative.getOffsetHeight();
            relLeft = relative.getAbsoluteLeft();
            relTop = relative.getAbsoluteTop();
        } else if (element != null) {
            relWidth = element.getOffsetWidth();
            relHeight = element.getOffsetHeight();
            relLeft = DOM.getAbsoluteLeft(element);
            relTop = DOM.getAbsoluteTop(element);
        } else {
            super.setPopupPosition(0, 0);
            return;
        }


        Style style = lb1.getElement().getStyle();
        int left = 0;
        int top = 0;
        if (tipDirection.equalsIgnoreCase(LOCATION_TOP)) {
            //显示在上面
            //    <--------->
            //      [     ]
            top = relTop - offsetHeight - 10;
            style.setTop(offsetHeight-7, Style.Unit.PX);
            if (offsetWidth > relWidth) {
                //tip宽度大于 目标的宽度
                if (relLeft < (offsetWidth - relWidth) / 2) {
                    //左对齐
                    left = relLeft;
                    style.setLeft(relWidth / 2 - 5, Style.Unit.PX);

                } else if ((relLeft + relWidth) > (Window.getClientWidth() - (offsetWidth - relWidth) / 2)) {
                    //右对齐
                    left = relLeft + relWidth - offsetWidth;
                    style.setLeft(this.getOffsetWidth() - relWidth / 2 - 5, Style.Unit.PX);
                } else {
                    //居中对齐
                    left = relLeft - (offsetWidth - relWidth) / 2;
                    style.setLeft(this.getOffsetWidth() / 2 - 5, Style.Unit.PX);
                }
            } else {
                left = relLeft - (offsetWidth - relWidth) / 2;
                style.setLeft(this.getOffsetWidth() / 2 - 5, Style.Unit.PX);
            }
        } else if (tipDirection.equalsIgnoreCase(LOCATION_BOTTOM)) {
            //显示在下面
            //      [     ]
            //    <--------->
            top = relTop + relHeight + 10;
            style.setTop(-4, Style.Unit.PX);
            if (offsetWidth > relWidth) {
                //tip宽度大于 目标的宽度
                if (relLeft < (offsetWidth - relWidth) / 2) {
                    //左对齐
                    left = relLeft;
                    style.setLeft(relWidth / 2 - 5, Style.Unit.PX);

                } else if ((relLeft + relWidth) > (Window.getClientWidth() - (offsetWidth - relWidth) / 2)) {
                    //右对齐
                    left = relLeft + relWidth - offsetWidth;
                    style.setLeft(this.getOffsetWidth() - relWidth / 2 - 5, Style.Unit.PX);
                } else {
                    //居中对齐
                    left = relLeft - (offsetWidth - relWidth) / 2;
                    style.setLeft(this.getOffsetWidth() / 2 - 5, Style.Unit.PX);
                }
            } else {
                left = relLeft - (offsetWidth - relWidth) / 2;
                style.setLeft(this.getOffsetWidth() / 2 - 5, Style.Unit.PX);
            }
        } else if (tipDirection.equalsIgnoreCase(LOCATION_RIGHT)) {//显示在目标的右边
            left = relLeft + relWidth + 10;
            style.setLeft(-4, Style.Unit.PX);
            if (offsetHeight > relHeight) {
                //tip宽度大于 目标的宽度
                if (relTop < (offsetHeight - relHeight) / 2) {
                    //上对齐
                    top = relTop;
                    style.setTop(relHeight / 2 - 5, Style.Unit.PX);

                } else if ((relLeft + relWidth) > (Window.getClientHeight() - (offsetHeight - relHeight) / 2)) {
                    //低对齐
                    top = relTop + relHeight - offsetHeight;
                    style.setTop(this.getOffsetHeight() - relHeight / 2 - 5, Style.Unit.PX);
                } else {
                    //垂直居中对齐
                    top = relTop - (offsetHeight - relHeight) / 2;
                    style.setTop(this.getOffsetHeight() / 2 - 5, Style.Unit.PX);
                }
            } else {
                top = relTop - (offsetHeight - relHeight) / 2;
                style.setTop(this.getOffsetHeight() / 2 - 5, Style.Unit.PX);
            }
        }
        setPopupPosition(left, top);
    }
}
