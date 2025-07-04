package cn.mapway.ui.client.mvc;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod; // Import JsMethod for renaming
import jsinterop.annotations.JsType;

/**
 * Size 该size可以被javascript使用
 */
@JsType
public class Size {
    public double x;
    public double y;

    // The no-argument constructor will now explicitly call the JsConstructor.
    // We are no longer making this one the JsConstructor.
    protected Size(){
        this(0, 0);
    }
    @JsConstructor
    public Size(double x, double y){
        this.x=x;
        this.y=y;
    }
    // Renamed for clarity and to avoid JavaScript name collision
    @JsMethod(name = "distanceToXY")
    public double distanceTo(double tx, double ty) {
        return Math.sqrt((tx - x) * (tx - x) + (ty - y) * (ty - y));
    }

    // Renamed for clarity and to avoid JavaScript name collision
    @JsMethod(name = "distanceToOtherSize")
    public double distanceTo(Size target){
        return distanceTo(target.x, target.y);
    }

    // This is now the ONLY JsConstructor
    public static Size create(double x, double y) {
        Size size = new Size();
        size.x = x;
        size.y = y;
        return size;
    }

    public String toString() {
        return "Size{" +
                "x=" + x +
                ", y=" + y +
                "}";
    }

    /**
     * SVG representation
     * points="50,0 21,90 98,35 2,35 79,90"
     * x,y format
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
        return create(x, y);
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