package cn.mapway.ui.client.widget.dialog;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.window.IProvideSize;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * AiDialog
 * 开始自定义对话框
 *
 * @author zhangjianshe@gmail.com
 */
public class AiDialog extends PopupPanel implements IData, HasCommonHandlers {
    private final int clientLeft;
    private final int clientTop;
    private final CommonEventHandler bodyHandler = commonEvent -> fireEvent(commonEvent);
    Object userData;
    HandlerRegistration oldHandler = null;
    DockLayoutPanel layout;
    CloseCaption caption;
    HandlerRegistration resizeHandlerRegistration;
    Widget body = null;
    boolean initialized = false;
    private boolean dragging;
    private int dragStartX;
    private int dragStartY;
    private int windowWidth;
    private HandlerRegistration bodyHandlerRegistry = null;

    public AiDialog() {
        this("", null);
    }

    public AiDialog(String title, Widget content) {
        super();
        layout = new DockLayoutPanel(Style.Unit.PX);
        caption = new CloseCaption();
        caption.addCloseHandler(e -> {
            hide();
        });
        layout.addNorth(caption, 54);
        setWidget(layout);
        addStyleName("ai-dialog");
        this.setText(title);
        if (content != null) {
            this.setWidget(content);
            body = content;
        }
        this.setGlassEnabled(true);
        this.setModal(true);
        setAnimationEnabled(false);
        caption.setUnicodeIcon(null);

        this.windowWidth = Window.getClientWidth();
        this.clientLeft = Document.get().getBodyOffsetLeft();
        this.clientTop = Document.get().getBodyOffsetTop();
        MouseHandler mouseHandler = new MouseHandler();
        this.addDomHandler(mouseHandler, MouseDownEvent.getType());
        this.addDomHandler(mouseHandler, MouseUpEvent.getType());
        this.addDomHandler(mouseHandler, MouseMoveEvent.getType());
        this.addDomHandler(mouseHandler, MouseOverEvent.getType());
        this.addDomHandler(mouseHandler, MouseOutEvent.getType());
        getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);

    }


    @Override
    public void setWidget(Widget w) {
        if (!initialized) {
            initialized = true;
            super.setWidget(w);
        } else {
            if (body != null) {
                body.removeFromParent();
            }
            if (bodyHandlerRegistry != null) {
                bodyHandlerRegistry.removeHandler();
                bodyHandlerRegistry = null;
            }
            body = w;
            layout.add(w);
            if (w instanceof IProvideSize) {
                IProvideSize w2 = (IProvideSize) w;
                Size size = w2.requireDefaultSize();
                if (size != null) {
                    setPixelSize(size.getXAsInt(), size.getYAsInt());
                } else {
                    setPixelSize(900, 500);
                }
            } else {
                setPixelSize(900, 500);
            }

            if (w instanceof HasCommonHandlers) {
                bodyHandlerRegistry = ((HasCommonHandlers) w).addCommonHandler(bodyHandler);
            }
        }
    }

    public Widget getContent() {
        return body;
    }

    @Override
    public void setPixelSize(int width, int height) {
        layout.setPixelSize(width, height);
        layout.forceLayout();
    }

    public void setText(String title) {
        caption.setText(title);
    }

    public CloseCaption getCloseCaption() {
        return caption;
    }

    @Override
    public Object getData() {
        return userData;
    }

    @Override
    public void setData(Object obj) {
        userData = obj;
    }

    @Override
    public void add(Widget w) {
        setWidget(w);
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        if (oldHandler != null) {
            oldHandler.removeHandler();
            oldHandler = null;
        }
        if (handler != null) {
            oldHandler = addHandler(handler, CommonEvent.TYPE);
        }
        return oldHandler;
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        NativeEvent nativeEvent = event.getNativeEvent();

        if (!event.isCanceled() && (event.getTypeInt() == Event.ONMOUSEDOWN)
                && isToolEvents(nativeEvent)) {
            //如果是工具栏事件 缺省处理
            return;
        }
        if (!event.isCanceled() && event.getTypeInt() == 4 && this.isCaptionEvent(nativeEvent)) {
            nativeEvent.preventDefault();
        }
        super.onPreviewNativeEvent(event);
    }

    public void hide(boolean autoClosed) {
        if (this.resizeHandlerRegistration != null) {
            this.resizeHandlerRegistration.removeHandler();
            this.resizeHandlerRegistration = null;
        }

        super.hide(autoClosed);
    }

    public void onBrowserEvent(Event event) {
        switch (event.getTypeInt()) {
            case 4:
            case 8:
            case 16:
            case 32:
            case 64:
                if (!this.dragging && !this.isCaptionEvent(event)) {
                    return;
                }
            default:
                super.onBrowserEvent(event);
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onMouseDown(Widget sender, int x, int y) {
        if (DOM.getCaptureElement() == null) {
            this.dragging = true;
            DOM.setCapture(this.getElement());
            this.dragStartX = x;
            this.dragStartY = y;
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onMouseEnter(Widget sender) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onMouseLeave(Widget sender) {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onMouseMove(Widget sender, int x, int y) {
        if (this.dragging) {
            int absX = x + this.getAbsoluteLeft();
            int absY = y + this.getAbsoluteTop();
            if (absX < this.clientLeft || absX >= this.windowWidth || absY < this.clientTop) {
                return;
            }

            this.setPopupPosition(absX - this.dragStartX, absY - this.dragStartY);
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public void onMouseUp(Widget sender, int x, int y) {
        this.dragging = false;
        DOM.releaseCapture(this.getElement());
    }

    public void show() {
        if (this.resizeHandlerRegistration == null) {
            this.resizeHandlerRegistration = Window.addResizeHandler(new ResizeHandler() {
                public void onResize(ResizeEvent event) {
                    AiDialog.this.windowWidth = event.getWidth();
                }
            });
        }

        super.show();
    }

    protected void beginDragging(MouseDownEvent event) {
        this.onMouseDown(this.caption.asWidget(), event.getX(), event.getY());
    }

    protected void continueDragging(MouseMoveEvent event) {
        this.onMouseMove(this.caption.asWidget(), event.getX(), event.getY());
    }

    protected void endDragging(MouseUpEvent event) {
        this.onMouseUp(this.caption.asWidget(), event.getX(), event.getY());
    }

    private boolean isToolEvents(NativeEvent event) {
        EventTarget target = event.getEventTarget();
        if (Element.is(target)) {
            return getCloseCaption().getToolsContainer().getElement().isOrHasChild(Element.as(target));
        }
        return false;
    }

    private boolean isCaptionEvent(NativeEvent event) {
        EventTarget target = event.getEventTarget();
        return Element.is(target) && this.getCloseCaption().getElement().isOrHasChild(Element.as(target));
    }

    public void setSize(Size size) {
        setPixelSize(size.getXAsInt(), size.getYAsInt());
    }

    public void resizeToClient() {
        int height = Window.getClientHeight();
        int width = Window.getClientWidth() - 400;
        height = height - 120;
        if (height < 520) {
            height = 520;
        }
        if (width > 1500) {
            width = 1500;
        } else if (width < 400) {
            width = 400;
        }

        setPixelSize(width, height);
    }

    private class MouseHandler implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler, MouseMoveHandler {
        private MouseHandler() {
        }

        public void onMouseDown(MouseDownEvent event) {
            AiDialog.this.beginDragging(event);
        }

        public void onMouseMove(MouseMoveEvent event) {
            AiDialog.this.continueDragging(event);
        }

        public void onMouseOut(MouseOutEvent event) {
            AiDialog.this.onMouseLeave(AiDialog.this.caption.asWidget());
        }

        public void onMouseOver(MouseOverEvent event) {
            AiDialog.this.onMouseEnter(AiDialog.this.caption.asWidget());
        }

        public void onMouseUp(MouseUpEvent event) {
            AiDialog.this.endDragging(event);
        }
    }

}
