package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.mvc.window.ISelectable;
import cn.mapway.ui.client.tools.IData;
import com.google.gwt.user.client.ui.Label;

/**
 * Header
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class Header extends Label implements IData, ISelectable {
    Object data;

    public Header() {
        this("");
    }

    public Header(String header) {
        super(header);
        setStyleName("ai-header");
    }

    public void setHTML(String html) {
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

    @Override
    public void setSelect(boolean select) {
        CommonEventComposite.setElementSelect(getElement(), select);
    }
}
