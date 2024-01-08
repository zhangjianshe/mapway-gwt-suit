package cn.mapway.ui.client.mvc.decorator.link;


import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.tools.IData;

import java.util.ArrayList;
import java.util.List;

/**
 * Link
 *
 * @author zhang
 */
public class Link implements IData {
    //起始窗口ID
    public String startId;
    public int startAnchorIndex;
    //结束窗口ID
    public String endId;
    public int endAnchorIndex;
    List<Size> points;
    String id;
    Object data;

    public Link(String id) {
        this.id = id;
        points = new ArrayList<>();
        points.add(new Size(0, 0));
        points.add(new Size(0, 0));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStart(double x, double y) {
        points.get(0).set(x, y);
    }

    public void setStart(Size pt) {
        setStart(pt.x, pt.y);
    }

    public void setEnd(Size pt) {
        points.get(points.size() - 1).set(pt.x, pt.y);
    }

    public void setEnd(double x, double y) {
        points.get(points.size() - 1).set(x, y);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Size point : points) {
            stringBuilder.append(point.toSVGString()).append(" ");
        }
        return stringBuilder.toString();
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    public void offsetStart(double offsetX, double offsetY) {
        points.get(0).offset(offsetX, offsetY);
    }

    public void offsetEnd(double offsetX, double offsetY) {
        points.get(points.size() - 1).offset(offsetX, offsetY);
    }

    public void offset(double offsetX, double offsetY) {
        for (Size point : points) {
            point.offset(offsetX, offsetY);
        }
    }

}
