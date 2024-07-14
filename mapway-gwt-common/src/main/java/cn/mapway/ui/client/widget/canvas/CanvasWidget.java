package cn.mapway.ui.client.widget.canvas;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.util.Logs;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.RootPanel;
import elemental2.dom.DomGlobal;
import elemental2.dom.Window;

/**
 * CanvasWidget
 * 颜色面板
 *
 * @author zhang
 */
public class CanvasWidget extends FocusWidget implements AnimationScheduler.AnimationCallback {
    boolean continueDraw = false;
    double dpr;
    Size size = new Size(0, 0);
    private AnimationScheduler.AnimationHandle animationHandle;

    public CanvasWidget() {
        this(Document.get().createCanvasElement());
    }


    private CanvasWidget(CanvasElement element) {
        setElement(element);
        addStyleName("ai-canvas");
    }

    public static CanvasWidget wrap(CanvasElement element) {
        assert Document.get().getBody().isOrHasChild(element);
        CanvasWidget canvas = new CanvasWidget(element);

        canvas.onAttach();
        RootPanel.detachOnWindowClose(canvas);

        return canvas;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setContinueDraw(true);
    }

    public double getDpr() {
        return dpr;
    }

    public Size resizeWindow() {
        Window window = DomGlobal.window;
        dpr = 1.0;
        if (window.devicePixelRatio > 0) {
            dpr = window.devicePixelRatio;
        }

        Element parentElement = getElement().getParentElement();
        int offsetHeight = parentElement.getOffsetHeight();
        int offsetWidth = parentElement.getOffsetWidth();
        size.set(offsetWidth, offsetHeight);

        Logs.info("dpr=" + dpr + " size=" + size);
        setCoordinateSpaceWidth(offsetWidth);
        setCoordinateSpaceHeight(offsetHeight);

        return size;
    }

    public Size getSpaceSize() {
        return size;
    }

    /**
     * Returns the attached Canvas Element.
     *
     * @return the Canvas Element
     */
    public CanvasElement getCanvasElement() {
        return this.getElement().cast();
    }

    /**
     * Gets the rendering context that may be used to draw on this canvas.
     *
     * @param contextId the context id as a String
     * @return the canvas rendering context
     */
    public Context getContext(String contextId) {
        return getCanvasElement().getContext(contextId);
    }

    /**
     * Returns a 2D rendering context.
     * <p>
     * This is a convenience method, see {@link #getContext(String)}.
     *
     * @return a 2D canvas rendering context
     */
    public Context2d getContext2d() {
        return getCanvasElement().getContext2d();
    }

    /**
     * Gets the height of the internal canvas coordinate space.
     *
     * @return the height, in pixels
     * @see #setCoordinateSpaceHeight(int)
     */
    public int getCoordinateSpaceHeight() {
        return getCanvasElement().getHeight();
    }

    /**
     * Sets the height of the internal canvas coordinate space.
     *
     * @param height the height, in pixels
     * @see #getCoordinateSpaceHeight()
     */
    public void setCoordinateSpaceHeight(int height) {
        getCanvasElement().setHeight(height);
    }

    /**
     * Gets the width of the internal canvas coordinate space.
     *
     * @return the width, in pixels
     * @see #setCoordinateSpaceWidth(int)
     */
    public int getCoordinateSpaceWidth() {
        return getCanvasElement().getWidth();
    }

    /**
     * Sets the width of the internal canvas coordinate space.
     *
     * @param width the width, in pixels
     * @see #getCoordinateSpaceWidth()
     */
    public void setCoordinateSpaceWidth(int width) {
        getCanvasElement().setWidth(width);
    }

    /**
     * Returns a data URL for the current content of the canvas element.
     *
     * @return a data URL for the current content of this element.
     */
    public String toDataUrl() {
        return getCanvasElement().toDataUrl();
    }

    /**
     * Returns a data URL for the current content of the canvas element, with a
     * specified type.
     *
     * @param type the type of the data url, e.g., image/jpeg or image/png.
     * @return a data URL for the current content of this element with the
     * specified type.
     */
    public String toDataUrl(String type) {
        return getCanvasElement().toDataUrl(type);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
        setCoordinateSpaceHeight(height);
        setCoordinateSpaceWidth(width);
    }

    @Override
    protected void onUnload() {
        super.onUnload();
       setContinueDraw(false);

    }

    @Override
    public void execute(double timestamp) {
        onDraw(timestamp);
        if (continueDraw) {
            animationHandle = AnimationScheduler.get().requestAnimationFrame(this, getCanvasElement());
        }
    }

    public void setContinueDraw(boolean continueDraw) {
        this.continueDraw = continueDraw;
        if (continueDraw) {
            if(animationHandle == null) {
                animationHandle = AnimationScheduler.get().requestAnimationFrame(this, this.getCanvasElement());
            }
        }
        else {
            if (animationHandle != null) {
                animationHandle.cancel();
                animationHandle = null;
            }
        }
    }

    public void redraw() {
        onDraw(0);
    }

    /**
     * 需要绘制画布
     *
     * @param timestamp
     */
    protected void onDraw(double timestamp) {

    }
}
