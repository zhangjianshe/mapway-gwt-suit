package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;


/**
 * MainButton
 *
 * @author zhang
 */
public class MainButton extends Composite implements IData, HasClickHandlers {
    private static final MainButtonUiBinder ourUiBinder = GWT.create(MainButtonUiBinder.class);
    @UiField
    Image icon;
    @UiField
    Label txt;
    @UiField
    FontIcon fontIcon;
    Object data;
    private ModuleInfo moduleInfo;

    public MainButton() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setSelected(boolean b) {
        fontIcon.setSelect(b);
        if (b) {
            this.getElement().setAttribute("selected", "true");
        } else {
            this.getElement().removeAttribute("selected");
        }
    }

    public void setText(String text) {
        txt.setText(text);
    }

    public void setResource(ImageResource iconResource) {
        if (fontIcon.isVisible()) {
            fontIcon.setVisible(false);
        }
        if (iconResource != null) {
            icon.setResource(iconResource);
            icon.setVisible(true);
        } else {
            icon.setVisible(false);
        }
    }

    public void setFontIcon(String unicode) {
        if (icon.isVisible()) {
            icon.setVisible(false);
        }
        if (unicode == null || unicode.length() == 0) {
            fontIcon.setVisible(false);
        } else {
            fontIcon.setIconUnicode(unicode);
            fontIcon.setVisible(true);
        }
    }

    /**
     * 设置按钮的文字和图标unicode
     *
     * @param text
     * @param unicode
     */
    public void setData(String text, String unicode) {
        txt.setText(text);
        setFontIcon(unicode);
    }

    /**
     * 设置按钮的文字和图片资源
     *
     * @param text
     * @param iconResource
     */
    public void setData(String text, ImageResource iconResource) {
        txt.setText(text);
        setResource(iconResource);
    }

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
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
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    /**
     * 出发点击事件
     */
    public void click() {
        innerClick(getElement());
    }

    private final native void innerClick(Element e)/*-{
        e.click();
    }-*/;

    interface MainButtonUiBinder extends UiBinder<HorizontalPanel, MainButton> {
    }
}
