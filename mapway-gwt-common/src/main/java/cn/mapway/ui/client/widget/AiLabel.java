package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.mvc.window.IEnabled;
import cn.mapway.ui.client.mvc.window.ISelectable;
import cn.mapway.ui.client.tools.IData;
import com.google.gwt.user.client.ui.Label;

/**
 * AiLabel
 *
 * @author zhang
 */
public class AiLabel extends Label implements IEnabled, ISelectable, IData<Object> {
    Object data;

    public AiLabel() {
        this("");
    }

    public AiLabel(String text) {
        super(text);
        setStyleName("ai-label");
    }

    @Override
    public boolean getEnabled() {
        String attribute = getElement().getAttribute(IEnabled.ENABLED_ATTRIBUTE);
        return !"false".equals(attribute);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            getElement().setAttribute(IEnabled.ENABLED_ATTRIBUTE, "true");
        } else {
            getElement().setAttribute(IEnabled.ENABLED_ATTRIBUTE, "false");
        }
    }

    public void setHtml(String html) {
        getElement().setInnerHTML(html);
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    /**
     * @param b
     */
    @Override
    public void setSelect(boolean b) {
        if (b) {
            getElement().setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
        } else {
            getElement().removeAttribute(ISelectable.SELECT_ATTRIBUTE);
        }
    }
}
