package cn.mapway.ui.client.widget.dialog;

/**
 * CloseCaption
 *
 * @author zhangjianshe@gmail.com
 */


import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.*;

/**
 * The Class CloseCaption.
 */
public class CloseCaption extends HorizontalPanel implements DialogBox.Caption, HasCloseHandlers {

    /**
     * The close handler.
     */
    /**
     * The title.
     */
    Label title;
    FontIcon icon;
    /**
     * The btn close.
     */
    FontIcon btnClose;
    HorizontalPanel tools;
    HorizontalPanel titleContainer;


    /**
     * Instantiates a new close caption.
     */
    public CloseCaption() {
        icon = new FontIcon();
        title = new Label();
        btnClose = new FontIcon();
        titleContainer = new HorizontalPanel();
        titleContainer.add(icon);
        titleContainer.add(title);
        tools = new HorizontalPanel();
        tools.setSpacing(4);
        btnClose.setIconUnicode(Fonts.CLOSE1);
        btnClose.addStyleName("ai-dialog-close");
        btnClose.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        this.setWidth("100%");
        this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        this.add(titleContainer);
        this.add(tools);
        this.add(btnClose);
        tools.setStyleName("ai-caption-tools");
        this.setCellWidth(icon, "22px");
        btnClose.addClickHandler(event -> {
            CloseEvent.fire(CloseCaption.this, null);
        });
        setStyleName("ai-caption");
        icon.addStyleName("ai-caption-icon");
        title.setStyleName("ai-caption-title");
        this.setCellHorizontalAlignment(btnClose, HasHorizontalAlignment.ALIGN_RIGHT);
        this.setCellVerticalAlignment(title, HasVerticalAlignment.ALIGN_MIDDLE);
        this.setHeight("100%");
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler(com.google.gwt.event.
     * dom.client.MouseDownHandler)
     */
    @Override
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {

        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(com.google.gwt.event.dom.
     * client.MouseUpHandler)
     */
    @Override
    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom
     * .client.MouseOutHandler)
     */
    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.
     * dom.client.MouseOverHandler)
     */
    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.
     * dom.client.MouseMoveHandler)
     */
    @Override
    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event
     * .dom.client.MouseWheelHandler)
     */
    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasHTML#getHTML()
     */
    @Override
    public String getHTML() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasHTML#setHTML(java.lang.String)
     */
    @Override
    public void setHTML(String html) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.gwt.safehtml.client.HasSafeHtml#setHTML(com.google.gwt.safehtml.shared.SafeHtml)
     */
    @Override
    public void setHTML(SafeHtml html) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasText#getText()
     */
    @Override
    public String getText() {
        return title.getText();
    }

    /*
     * (non-Javadoc)
     * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
     */
    @Override
    public void setText(String text) {
        title.setText(text);

    }

    public void setUnicodeIcon(String unicode) {
        if (unicode != null && unicode.length() > 0) {
            icon.setIconUnicode(unicode);
            icon.setVisible(true);
            setCellWidth(icon, "30px");
        } else {
            icon.setVisible(false);
            setCellWidth(icon, "0px");
        }
    }

    public void clearWidget() {
        tools.clear();
    }

    public void addWidget(Widget widget) {
        tools.add(widget);
    }

    public Widget getToolsContainer() {
        return tools;
    }

    public void addTools(Widget... widgets) {
        if (widgets != null) {
            for (Widget w : widgets) {
                tools.add(w);
            }
        }
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler closeHandler) {
        return addHandler(closeHandler, CloseEvent.getType());
    }
}
