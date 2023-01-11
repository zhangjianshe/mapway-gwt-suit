package cn.mapway.ui.client.mvc.decorator.link;


import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * LinkEvent
 *
 * @author zhang
 */
public class LinkEvent {
    public final static int LINK_START = 0;
    public final static int LINK_MOVE = 1;
    public final static int LINK_END = 2;
    public final static int LINK_CANCEL = 3;
    public MouseEvent mouseEvent;
    public int type;
    public Link currentLink;
    public IWindowDecorator startNode;
    public IWindowDecorator endNode;
    //起点的 anchorId
    public AnchorInfo startAnchorInfo;
    //终点的AnchorID
    public AnchorInfo endAnchorInfo;

    public LinkEvent() {

    }

    public LinkEvent(int type, MouseEvent mouseEvent) {
        this.type = type;
        this.mouseEvent = mouseEvent;
    }
}
