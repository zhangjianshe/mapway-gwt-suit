package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.decorator.ISelectable;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;

/**
 * AiButton
 *
 * @author zhangjianshe@gmail.com
 */
public class AiButton extends Button implements IData, ISelectable {
    private static final String BUTTON_STYLE_KEY = "btn-style";
    boolean down = false;
    private Object data;

    boolean loading = false;

    private String spanContext;

    private String spanStyle;

    private String icon;

    private String iconStyle;

    public AiButton(String te) {
        String html = getHtml(te);
        setHTML(html);
        installEvent();
    }

    public AiButton() {
        String html = getHtml(null);
        setHTML(html);
        installEvent();
    }

    private void installEvent() {
        addDomHandler((event) -> {
            down = true;
            DOM.setCapture(getElement());
            getElement().getStyle().setProperty("scale", "0.95");
        }, MouseDownEvent.getType());
        addDomHandler((event) -> {
            if (down) {
                getElement().getStyle().setProperty("scale", "1");
                DOM.releaseCapture(getElement());
            }
        }, MouseUpEvent.getType());
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
    public void setSelect(boolean select) {
        setElementSelect(getElement(), select);
    }

    protected void setElementSelect(Element element, boolean select) {
        setAttribute(ISelectable.SELECT_ATTRIBUTE, select ? "true" : null);
    }

    protected void setAttribute(String attribute, String value) {
        if (attribute == null || attribute.length() == 0) {
            return;
        }
        if (value == null) {
            getElement().removeAttribute(attribute);
        } else {
            getElement().setAttribute(attribute, value);
        }
    }

    public void setButtonStyle(String style) {
        if (style == null || style.length() == 0) {
            setAttribute(BUTTON_STYLE_KEY, null);
        } else {
            setAttribute(BUTTON_STYLE_KEY, style);
        }
    }

    public String getText(){
        if(StringUtil.isBlank(spanContext)){
            return super.getText();
        } else {
            return spanContext;
        }
    }

    // 拼接html字符串
    private String getHtml() {
        if(StringUtil.isBlank(spanContext)){
            spanContext = super.getText();
        }
        if(iconStyle == null){
            iconStyle = "";
        }
        if(spanStyle == null){
            spanStyle = "";
        }
        StringBuffer sb = new StringBuffer();
        if(loading){
            sb.append("<span class=\"ai-rotate ai-icon" + iconStyle + "\"> &#x" + Fonts.REFRESH + " </span>");
        } else if (!StringUtil.isBlank(icon)) {
            sb.append("<span class=\" ai-icon " + iconStyle + "\"> &#x" + icon + "</span>");
        }
        sb.append("<span class=\" " + spanStyle + "\">" + spanContext + "</span>");
        return sb.toString();
    }

    private String getHtml(String span) {
        spanContext = span;
        return getHtml();
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        setEnabled(!loading);
        setHTML(getHtml());
        if(loading){
            this.addStyleName("is-loading");
        } else {
            this.removeStyleName("is-loading");
        }
    }

    public void setIconStyle(String iconStyle) {
        this.iconStyle = iconStyle;
        setHTML(getHtml());
    }

    public void setIcon(String icon) {
        this.icon = icon;
        setHTML(getHtml());
    }

    public void setSpanStyle(String spanStyle) {
        this.spanStyle = spanStyle;
        setHTML(getHtml());
    }

    public void setSpanContext(String spanContext) {
        this.spanContext = spanContext;
        setHTML(getHtml());
    }
}
