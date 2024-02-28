package cn.mapway.geo.shared.vector;

import lombok.Data;

/**
 * Rect
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class Rect {
    public double x;
    public double y;
    public double width;
    public double height;

    public Rect() {

    }

    public int getXAsInt() {
        return (int)x;
    }
    public int getYAsInt() {
        return (int)y;
    }
    public int getWidthAsInt() {
        return (int)width;
    }
    public int getHeightAsInt() {
        return (int)height;
    }
    public Rect(double x, double y, double width, double height) {
        setValue(x, y, width, height);
    }

    public Rect setValue(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public void moveTo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Rect offset(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Rect offset(Point pt) {
        this.x += pt.x;
        this.y += pt.y;
        return this;
    }

    /**
     * 矩形是否包含（x,y）
     *
     * @param x
     * @param y
     * @return
     */
    public boolean contains(double x, double y) {
        return x >= this.x && x <= (this.x + width) && y >= this.y && y <= (this.y + height);
    }

    /**
     * output json format
     *
     * @return
     */
    public String toString() {
        return "{\"x\":" + x + ",\"y\":" + y + ",\"width\":" + width + ",\"height\":" + height + "}";
    }



    public Rect clone() {
        return new Rect(x, y, width, height);
    }

    public Rect shrink(double left, double top, double right, double bottom) {
        x = x + left;
        y = y + top;
        width = width - left - right;
        height = height - top - bottom;
        return this;
    }
    /**
     * 正交化对象
     */
    public void normalize() {
        if (width < 0) {
            x = x + width;
            width = -width;
        }
        if (height < 0) {
            y = y + height;
            height = -height;
        }
    }

    /**
     * 两个矩形是否相交
     *
     * @param box
     * @return
     */
    public boolean intersect(Rect box) {
        if (box == null) {
            return false;
        }
        return !(x + width < box.x) && !(box.x + box.width < x) &&
                !(y + height < box.y) && !(box.y + box.height < y);
    }
    /**
     * 判断两个矩形是否交叉
     *
     * @param r
     * @return
     */
    public boolean isCross(Rect r) {
        return x + width > r.x
                && x < r.x + r.width
                && y <= r.y + r.height
                && y + height >= r.y;
    }

    public Rect extend(Point pt) {
        return extend(pt.x, pt.y);
    }

    public Rect extend(double x, double y) {
        if (this.x > x) {
            this.x = x;
        }
        if (this.y > y) {
            this.y = y;
        }
        if (this.x + this.width < x) {
            this.width = x - this.x;
        }
        if (this.y + this.height < y) {
            this.height = y - this.y;
        }
        return this;
    }

    /**
     * 扩展矩形范围
     *
     * @param box
     */
    public Rect extend(Rect box) {
        if (this.x > box.x) {
            this.x = box.x;
        }
        if (this.x + this.width < box.x + box.width) {
            this.width = box.x + box.width - this.x;
        }
        if (this.y > box.y) {
            this.y = box.y;
        }
        if (this.y + this.height < box.y + box.height) {
            this.height = box.y + box.height - this.y;
        }
        return this;
    }

    public Rect copyFrom(Rect from) {
        this.x = from.x;
        this.y = from.y;
        this.width = from.width;
        this.height = from.height;
        return this;
    }


    public Point getCenter() {
        return new Point(x + width / 2, y + height / 2);
    }
}
