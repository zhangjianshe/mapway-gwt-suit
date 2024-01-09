package cn.mapway.ui.client.mvc.decorator.link;


import cn.mapway.ui.client.mvc.Rect;

/**
 * AnchorInfo
 * 窗口连接器的锚点信息
 *
 * @author zhang
 */
public class AnchorInfo {
    public static int ANCHOR_IN = 0;
    public static int ANCHOR_OUT = 1;
    public int anchorId;
    public Rect rect;
    public int type;
    Object data;

    public AnchorInfo(int type) {
        this.rect = new Rect().set(0, 0, 10, 10);
        this.type = type;
    }

    public String toString() {
        return "AnchorInfo: anchorId=" + anchorId + ", rect=" + rect.toString() + ", type=" + type;
    }

    public <T> T getData() {
        return (T) data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void offset(double x, double y) {
        this.rect.offset(x, y);
    }

    public Rect getRect() {
        return this.rect;
    }
}
