package cn.mapway.ui.client.widget;


import cn.mapway.ui.client.tools.IData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Anchor;

/**
 * AiAnchor
 *
 * @author zhangjianshe@gmail.com
 */
public class AiAnchor extends Anchor implements IData {
    Object data;

    public AiAnchor() {
        super();
        this.setStyleName("ai-anchor");
    }

    public AiAnchor(String text) {
        super(text);
        this.setStyleName("ai-anchor");
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    public void click() {
        innerClick(this.getElement());
    }

    private native void innerClick(Element e)/*-{
        e.click();
    }-*/;
    TipAdaptor adaptor;
    @Override
    public void setTitle(String tip) {
        if (tip == null || tip.length() == 0) {
            if(adaptor!=null) {
                adaptor.setTitle(null);
            }
        } else {
            if(adaptor==null)
            {
                adaptor=new TipAdaptor(this);
                adaptor.setTitle(tip);
            }
        }
    }


    /**
     * @param b
     */
    public void setSelected(boolean b) {
        if (b) {
            this.getElement().setAttribute("selected", "true");
        } else {
            this.getElement().removeAttribute("selected");
        }
    }
}
