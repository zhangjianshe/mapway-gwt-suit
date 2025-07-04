package cn.mapway.ui.client.mvc;

import jsinterop.annotations.JsType;

/**
 * Size 该size可以被javascript使用
 */
@JsType
public class Size {
    public double x;
    public double y;
    public Size(){
        x=0;
        y=0;
    }
    public double distanceTo(double tx, double ty) {
        return Math.sqrt((tx - x) * (tx - x) + (ty - y) * (ty - y));
    }
    public double distanceTo(Size target){
        return distanceTo(target.x, target.y);
    }
    public Size(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public String toString() {
        return "Size{" +
                "x=" + x +
                ", y=" + y +
                "}";
    }

    /**
     *  SVG representation
     *  points="50,0 21,90 98,35 2,35 79,90"
     *  x,y format
     * @return
     */
    public String toSVGString() {
         return x+","+y;
    }

    public Size offset(double offsetX, double offsetY) {
        this.x += offsetX;
        this.y += offsetY;
        return this;
    }

    public Size set(double x, double y) {
        this.x =  x;
        this.y =  y;
        return this;

    }
    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }
    public int getXAsInt()
    {
        return (int) x;
    }
    public int getYAsInt()
    {
        return (int) y;
    }

    public Size copyFrom(Size src)
    {
        this.x = src.getX();
        this.y = src.getY();
        return this;
    }
    public Size clone(){
        return new Size(x, y);
    }
    public Size copyTo(Size dest)
    {
        if(dest!=null)
        {
            dest.set(x,y);
        }
        return this;
    }

    public Size scale(double scaleX, double scaleY)
    {
        x=x*scaleX;
        y=y*scaleY;
        return this;
    }
}
