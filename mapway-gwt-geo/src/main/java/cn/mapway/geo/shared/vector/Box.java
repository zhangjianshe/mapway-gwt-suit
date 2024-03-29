package cn.mapway.geo.shared.vector;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * Box
 * 这个box里 判断的前提是 坐标轴方向 为 向上 和 向右
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class Box implements Serializable, IsSerializable {
    public double xmin;
    public double ymin;
    public double xmax;
    public double ymax;

    /**
     * 初始化
     */
    public Box() {
        setValue(Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
    }

    public Box(double xmin, double ymin, double xmax, double ymax) {
        setValue(xmin, ymin, xmax, ymax);
    }


    public void setValue(double xmin, double ymin, double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }


    public double width() {
        return xmax - xmin;
    }

    public double height() {
        return ymax - ymin;
    }

    public Point minPoint() {
        return new Point(xmin, ymin);
    }

    public Point maxPoint() {
        return new Point(xmax, ymax);
    }

    public void offset(double ox, double oy) {
        this.xmin += ox;
        this.xmax += ox;
        this.ymin += oy;
        this.ymax += oy;
    }

    public void magnify(double scale) {
        if (scale <= 0)
            return;
        double xOff = width() * (scale - 1) / 2;
        double yOff = height() * (scale - 1) / 2;
        inflate(xOff, yOff);
    }

    public void inflate(double dx, double dy) {
        xmin -= dx;
        xmax += dx;
        ymin -= dy;
        ymax += dy;
    }

    public Point center() {
        return new Point((xmin + xmax) / 2, (ymin + ymax) / 2);
    }

    public String toString() {
        return xmin + "," + ymin + "," + xmax + "," + ymax;
    }

    public String toGeoJSON() {
        return "{\"type\":\"Polygon\",\"coordinates\":[[[" + xmin + "," + ymin + "],[" + xmin + "," + ymax + "],[" + xmax + "," + ymax + "],[" + xmax + "," + ymin + "],[" + xmin + "," + ymin + "]]]}";
    }

    public String toWKT() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("POLYGON (");
        sb.append("(");
        sb.append(xmin).append(" ").append(ymin).append(",");
        sb.append(xmin).append(" ").append(ymax).append(",");
        sb.append(xmax).append(" ").append(ymax).append(",");
        sb.append(xmax).append(" ").append(ymin).append(",");
        sb.append(xmin).append(" ").append(ymin);
        sb.append(")");
        sb.append(")");
        return sb.toString();
    }

    public void merge(double minx, double maxx, double miny, double maxy) {
        if (this.xmin > minx) {
            this.xmin = minx;
        }
        if (this.xmax < maxx) {
            this.xmax = maxx;
        }
        if (this.ymin > miny) {
            this.ymin = miny;
        }
        if (this.ymax < maxy) {
            this.ymax = maxy;
        }
    }

    public void merge(Box box) {
        merge(box.xmin, box.xmax, box.ymin, box.ymax);
    }

    public void copyFrom(Box box) {
        this.xmin = box.xmin;
        this.xmax = box.xmax;
        this.ymin = box.ymin;
        this.ymax = box.ymax;
    }

    public void copyTo(Box box) {
        box.xmin=this.xmin;
        box.xmax=this.xmax;
        box.ymin=this.ymin;
        box.ymax=this.ymax;
    }

    public Box clone(){
        Box ret = new Box();
        copyTo(ret);
        return ret;
    }

    /**
     * 判断两个 box是否相交
     *
     * @param tileBox
     * @return
     */
    public boolean isCross(Box tileBox) {
        if (tileBox == null) {
            return false;
        }
        return !(this.xmax < tileBox.xmin) && !(this.xmin > tileBox.xmax)
                && !(this.ymin > tileBox.ymax) && !(this.ymax < tileBox.ymin);
    }

    public void expand(double x, double y) {
        if (x < xmin) xmin = x;
        if (x > xmax) xmax = x;
        if (y < ymin) ymin = y;
        if (y > ymax) ymax = y;
    }

    /**
     * 判断坐标是否在 wgs84范围内
     * @return
     */
    public boolean isInWgs84(){
        return -180 <=xmin && xmin<xmax && xmax<=180
              && -90<ymin && ymin<ymax && ymax<=90;
    }


    public double getWidth(){
        return Math.abs(xmax-xmin);
    }

    public double getHeight(){
        return Math.abs(ymax-ymin);
    }

    /**
     * 按比例扩展
     * @param scale
     * @return
     */
    public Box expand(double scale)
    {
        double width=getWidth();
        double height=getHeight();
        xmin=xmin-width*scale;
        xmax=xmax+width*scale;
        ymin=ymin-height*scale;
        ymax=ymax+height*scale;
        return this;
    }
    public boolean intersect(Box box) {
        return isCross(box);
    }

    public String toViewBox() {
        return xmin + " " + ymax + " " + (xmax - xmin) + " " + (ymax - ymin);
    }

    public void expand(Box extend) {
        if (xmin > extend.xmin) xmin = extend.xmin;
        if (xmax < extend.xmax) xmax = extend.xmax;
        if (ymin > extend.ymin) ymin = extend.ymin;
        if (ymax < extend.ymax) ymax = extend.ymax;
    }
}
