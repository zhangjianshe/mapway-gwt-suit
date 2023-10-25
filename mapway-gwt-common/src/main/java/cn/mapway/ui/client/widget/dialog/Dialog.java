
package cn.mapway.ui.client.widget.dialog;


import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup
 *
 * @author zhang
 */
public class Dialog<T extends Widget> extends AiDialog {
    private final CommonEventHandler commonHandler = event -> {
        if(event.isTitle()){
            setText(event.getValue());
        }
        else {
            //代理内部容器发送事件
            fireEvent(event);
        }
    };
    T content;

    public Dialog(T content, String title) {
        super();
        setText(title);
        setGlassEnabled(true);
        setModal(true);
        this.content = content;
        setWidget(content);
        if (content instanceof HasCommonHandlers) {
            ((HasCommonHandlers) content).addCommonHandler(commonHandler);
        }
    }


    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
