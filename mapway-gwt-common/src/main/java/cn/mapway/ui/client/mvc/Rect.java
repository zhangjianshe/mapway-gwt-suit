package cn.mapway.ui.client.mvc;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.io.Serializable;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class Rect implements Serializable {
    public double x;
    public double y;
    public double width;
    public double height;

    public Rect() {
    }

    /**
     * output json format
     *
     * @return
     */
    @Override
    @JsOverlay
    public final String toString() {
        return "{\"x\":" + x + ",\"y\":" + y + ",\"width\":" + width + ",\"height\":" + height + "}";
    }

    @JsOverlay
    public final Rect offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    @JsOverlay
    public final Rect expand(double top, double right, double bottom, double left) {
        this.x -= left;
        this.y -= top;
        this.width -= (left + right);
        this.height -= (top + bottom);
        return this;
    }

    @JsOverlay
    public final Size getCenter() {
        return new Size(x + width / 2, y + height / 2);
    }

    @JsOverlay
    public final Rect clone() {
        return new Rect().set(x, y, width, height);
    }

    @JsOverlay
    public final boolean contains(Size point) {
        return point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height;
    }

    @JsOverlay
    public final boolean contains(double x, double y) {
        return contains(new Size(x, y));
    }

    @JsOverlay
    public final Rect extend(double x, double y) {
        if (this.x > x) {
            this.x = x;
        }
        if (this.x + this.width < x) {
            this.width = x - this.x;
        }
        if (this.y > y) {
            this.y = y;
        }
        if (this.y + this.height < y) {
            this.height = y - this.y;
        }
        return this;
    }

    @JsOverlay
    public final Rect extend(Rect box) {
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

    @JsOverlay
    public final Rect shrink(double left, double top, double right, double bottom) {
        x = x + left;
        y = y + top;
        width = width - left - right;
        height = height - top - bottom;
        return this;
    }

    /**
     * 正交化对象
     */
    @JsOverlay
    public final Rect normalize() {
        if (width < 0) {
            x = x + width;
            width = -width;
        }
        if (height < 0) {
            y = y + height;
            height = -height;
        }
        return this;
    }

    @JsOverlay
    public final boolean intersect(Rect box) {
        if (box == null) {
            return false;
        }
        return !(x + width < box.x) && !(box.x + box.width < x) &&
                !(y + height < box.y) && !(box.y + box.height < y);
    }

    @JsOverlay
    public final Rect copyFrom(Rect src) {
        this.x = src.x;
        this.y = src.y;
        this.width = src.width;
        this.height = src.height;
        return this;
    }

    @JsOverlay
    public final Rect copyTo(Rect dest) {
        dest.x = this.x;
        dest.y = this.y;
        dest.width = this.width;
        dest.height = this.height;
        return this;
    }

    @JsOverlay
    public final double getX() {
        return x;
    }

    @JsOverlay
    public final void setX(double x) {
        this.x = x;
    }

    @JsOverlay
    public final double getY() {
        return y;
    }

    @JsOverlay
    public final void setY(double y) {
        this.y = y;
    }

    @JsOverlay
    public final double getWidth() {
        return width;
    }

    @JsOverlay
    public final Rect setWidth(double width) {
        this.width = width;
        return this;
    }

    @JsOverlay
    public final double getHeight() {
        return height;
    }

    @JsOverlay
    public final Rect setHeight(double height) {
        this.height = height;
        return this;
    }

    @JsOverlay
    public final Rect set(double startX, double startY, double width, double height) {
        this.x = startX;
        this.y = startY;
        this.width = width;
        this.height = height;
        return this;
    }
}
