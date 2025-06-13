package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.mvc.window.IEnabled;
import cn.mapway.ui.client.mvc.window.ISelectable;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;


/**
 * FontIcon
 * 字体图标
 *
 * @author zhang
 */
public class FontIcon<T> extends Label implements IData<T>, IEnabled, ISelectable {

    T data;
    TipAdaptor tipAdaptor;
    int messageCount = 0;
    private String iconUnicode;
    private boolean isPushButton = false;
    RbacComposite rbacComposite = null;

    private boolean visibleFlag = true;

    public FontIcon() {
        this("");
    }

    public FontIcon(String text) {
        super();
        setStyleName("mapway-font");
        tipAdaptor = new TipAdaptor(this);
        setIconUnicode(text);
        rbacComposite = new RbacComposite();
        rbacComposite.addCommonHandler((e)->{
            if (e.getType() == CommonEvent.RBAC_PERMISSION_CHANGE) {
                // 权限发生变化 自定义处理
                showOrHide();
            }
        });
    }

    public void asToolButton() {
        setStyleName("ai-tool-btn");
    }

    public void setProperty(String name, String value) {
        if (name != null) {
            if (value == null) {
                getElement().removeAttribute(name);
            } else {
                getElement().setAttribute(name, value);
            }
        }
    }

    public void setPushButton(boolean isPushButton) {
        this.isPushButton = isPushButton;
    }

    public void addMessageCount(int count) {
        messageCount += count;
        setProperty("count", String.valueOf(messageCount));
    }

    public void setMessageCount(int count) {
        messageCount = count;
        if (messageCount <= 0) {
            setProperty("count", null);
        } else {
            setProperty("count", String.valueOf(messageCount));
        }
    }

    /**
     * 设置字体大小
     *
     * @param size
     * @param unit
     */
    public void setSize(double size, Style.Unit unit) {
        this.getElement().getStyle().setFontSize(size, unit);
    }

    public void setFontSize(int size) {
        this.getElement().getStyle().setFontSize(size, Style.Unit.PX);
    }

    public void setLineHeight(int lineHeight) {
        this.getElement().getStyle().setLineHeight(lineHeight, Style.Unit.PX);
    }

    public void setColor(String color) {
        this.getElement().getStyle().setColor(color);
    }

    @Override
    public void setTitle(String title) {
        tipAdaptor.setTitle(title);
    }

    public void setTipDirection(String tipDirection) {
        tipAdaptor.setTipDirection(tipDirection);
    }


    @Override
    public boolean getEnabled() {
        String attribute = getElement().getAttribute(IEnabled.ENABLED_ATTRIBUTE);
        return !"false".equals(attribute);
    }

    @Override
    public void setEnabled(boolean enabled) {
        Element element = getElement();
        if (enabled) {
            element.setAttribute(IEnabled.ENABLED_ATTRIBUTE, "true");
            element.getStyle().clearProperty("pointerEvents");
        } else {
            element.setAttribute(IEnabled.ENABLED_ATTRIBUTE, "false");
            element.getStyle().setProperty("pointerEvents", "auto");
        }
    }

    public boolean isSelect() {
        return getElement().hasAttribute(ISelectable.SELECT_ATTRIBUTE);
    }

    public void setSelect(boolean selected) {
        if (isPushButton) {
            if (selected) {
                this.getElement().setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
            } else {
                this.getElement().removeAttribute(ISelectable.SELECT_ATTRIBUTE);
            }
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONDBLCLICK:
            case Event.ONFOCUS:
            case Event.ONCLICK:
                if (!getEnabled()) {
                    event.stopPropagation();
                    event.preventDefault();
                    return;
                }
                break;
        }
        super.onBrowserEvent(event);
    }

    public String getIconUnicode() {
        return iconUnicode;
    }

    /**
     * unicode value example 60ef
     *
     * @param text
     */
    public void setIconUnicode(String text) {
        iconUnicode = text;
        if (iconUnicode == null || iconUnicode.length() == 0) {
            getElement().setInnerHTML("");
        } else {
            getElement().setInnerHTML("&#x" + text);
        }
    }

    @Override
    public void setText(String text) {
        setIconUnicode(text);
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }


    public void click() {
        innerClick(getElement());
    }

    private native void innerClick(Element element)/*-{
        element.click();
    }-*/;


    @Override
    protected void onLoad() {
        super.onLoad();
        showOrHide();
    }

    /**
     * 将RbacComposite 变为组合式的组件
     * start
     */
    public void setRole(String role) {
        rbacComposite.setRole(role);
    }

    public void setResource(String resource) {
        rbacComposite.setResource(resource);
    }

    public void setAllRole(String role) {
        rbacComposite.setAllRole(role);
    }

    public void setAllResource(String resource) {
        rbacComposite.setAllResource(resource);
    }

    public static void setUserRoleProvider(IUserRoleProvider provider) {
        RbacComposite.setUserRoleProvider(provider);
    }

    public boolean isAssign(int type) {
        return rbacComposite.isAssign(type);
    }

    public void showOrHide() {
        if(visibleFlag){
            boolean assign = isAssign(ICheckRole.ALL_TYPE);
            if(!assign){
                setVisible(this.getElement(), assign);
            }
        }
    }

    public void setVisible(boolean visible) {
        setVisible(this.getElement(), visible);
        this.visibleFlag = visible;
        showOrHide();
    }

    /**
     * 将RbacComposite 变为组合式的组件
     * end
     */
}
