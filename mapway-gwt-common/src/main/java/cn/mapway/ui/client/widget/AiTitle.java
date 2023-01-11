package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.tools.IData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * AiTitle
 * 标题
 *
 * @author zhangjianshe@gmail.com
 */
public class AiTitle extends HorizontalPanel implements IData<Object>, HasClickHandlers {

    private final Label label;
    private Object data;
    private Image image;

    public AiTitle() {
        super();
        label = new Label();
        add(label);
        this.setCellVerticalAlignment(label, HasVerticalAlignment.ALIGN_MIDDLE);
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        this.setStyleName("ai-title");
        label.setText("ai-title-text");
    }


    public void click() {
        nativeClick(this.getElement());
    }

    private native void nativeClick(Element e)/*-{
        e.click();
    }-*/;

    public AiTitle setSelected(boolean b) {
        Element element = getElement();
        if (b) {
            element.setAttribute("selected", "true");
        } else {
            element.setAttribute("selected", "false");
        }
        return this;
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        label.setStyleName(style + "-text");
        if (image != null) {
            image.setStyleName(style + "-img");
        }
    }

    public void setText(String text) {
        label.setText(text);
    }

    public AiTitle setIcon(ImageResource resource) {
        if (image == null) {
            image = new Image();
            image.setStyleName(this.getStyleName() + "-img");
            this.insert(image, 0);
            image.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        }
        image.setResource(resource);
        return this;
    }

    public AiTitle setIcon(String url) {
        if (image == null) {
            image = new Image();
            image.setStyleName(this.getStyleName() + "-img");
            this.insert(image, 0);
        }
        image.setUrl(url);
        return this;
    }

    public void setIconSize(int width, int height) {
        if (image != null) {
            image.setPixelSize(width, height);
        }
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public void setData(Object obj) {
        this.data = obj;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
