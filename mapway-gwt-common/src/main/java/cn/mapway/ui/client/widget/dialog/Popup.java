package cn.mapway.ui.client.widget.dialog;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.window.IProvideSize;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup
 *
 * @author zhang
 */
public class Popup<T extends Widget> extends PopupPanel implements HasCommonHandlers, IData {
    T content;
    Object object;
    HandlerRegistration old;

    public Popup(T content) {
        super(true);
        this.content = content;
        setWidget(content);
        if (content instanceof HasCommonHandlers) {
            //代理内部容器发送事件
            CommonEventHandler commonHandler = event -> {
                if (event.isResize()) {
                    if (event.getValue() instanceof Size) {
                        Size size = event.getValue();
                        setPixelSize(size.getXAsInt(), size.getYAsInt());
                    } else {
                        Logs.info("resize event' data is not Size object");
                    }
                } else {
                    //代理内部容器发送事件
                    fireEvent(event);
                }
            };
            ((HasCommonHandlers) content).addCommonHandler(commonHandler);
        }
        if (content instanceof IProvideSize) {
            IProvideSize w2 = (IProvideSize) content;
            Size size = w2.requireDefaultSize();
            if (size != null) {
                setPixelSize(size.getXAsInt(), size.getYAsInt());
            } else {
                setPixelSize(900, 500);
            }
        } else {
            setPixelSize(900, 500);
        }
    }

    @Override
    public void setPixelSize(int width, int height) {
        content.setPixelSize(width, height);
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        if (old != null) {
            old.removeHandler();
        }
        old = addHandler(handler, CommonEvent.TYPE);
        return old;
    }

    @Override
    public Object getData() {
        return object;
    }

    @Override
    public void setData(Object obj) {
        object = obj;
    }
}
