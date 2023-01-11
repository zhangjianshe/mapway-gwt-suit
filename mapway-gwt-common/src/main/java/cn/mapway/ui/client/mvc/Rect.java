package cn.mapway.ui.client.mvc;

import java.io.Serializable;

public class Rect implements Serializable {
    public double x;
    public double y;
    public double width;
    public double height;

    public Rect(){
        x=0;
        y=0;
        width=0;
        height=0;
    }
    public Rect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * output json format
     *
     * @return
     */
    @Override
    public String toString() {
        return "{\"x\":" + x + ",\"y\":" + y + ",\"width\":" + width + ",\"height\":" + height + "}";
    }
    public Rect offset(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Rect expand(double top, double right, double bottom, double left) {
        this.x -= left;
        this.y -= top;
        this.width -= (left + right);
        this.height -= (top + bottom);
        return this;
    }

    public Size getCenter() {
        return new Size(x + width / 2, y + height / 2);
    }

    public Rect clone() {
        Rect clone = new Rect(x,y,width,height);
        return clone;
    }

    public boolean contains(Size point)
    {
        return point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height;
    }

    public boolean contains(double x, double y)
    {
        return contains(new Size(x,y));
    }
    public Rect extend(double x,double y)
    {
       if(this.x>x){
           this.x = x;
       }
       if(this.x+this.width<x)
       {
           this.width=x-this.x;
       }
       if(this.y>y){
           this.y=y;
       }
       if(this.y+this.height<y)
       {
           this.height=y-this.y;
       }
       return this;
    }
    public Rect extend(Rect box)
    {
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
    public Rect normalize() {
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

    public boolean intersect(Rect box)
    {
        if (box == null) {
            return false;
        }
        return !(x + width < box.x) && !(box.x + box.width < x) &&
                !(y + height < box.y) && !(box.y + box.height < y);
    }
    public Rect copyFrom(Rect src) {
        this.x = src.x;
        this.y = src.y;
        this.width = src.width;
        this.height = src.height;
        return this;
    }

    public Rect copyTo(Rect dest) {
        dest.x = this.x;
        dest.y = this.y;
        dest.width = this.width;
        dest.height = this.height;
        return this;
    }


    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void set(double startX, double startY, double width, double height) {
        this.x = startX;
        this.y = startY;
        this.width = width;
        this.height = height;
    }
}