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
     * Sizes="50,0 21,90 98,35 2,35 79,90"
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

    /**
     * Calculates the two base coordinates of an arrowhead given a line segment.
     * The arrowhead tip is at 'endSize'.
     *
     * @param startSize    The starting Size of the line segment.
     * @param endSize      The ending Size of the line segment (arrowhead tip).
     * @param arrowLength   The length of the arrowhead (distance from tip to base).
     * @param arrowHalfWidth The half-width of the arrowhead at its base.
     * @return An array of two Size objects representing the base corners of the arrowhead.
     * Returns null if the line segment has zero length (start and end Sizes are the same).
     */
    public static Size[] calculateArrowheadSizes(Size startSize, Size endSize,
                                                   double arrowLength, double arrowHalfWidth) {

        double x1 = startSize.x;
        double y1 = startSize.y;
        double x2 = endSize.x;
        double y2 = endSize.y;

        // 1. Calculate the vector of the segment (P1 to P2)
        double Vx = x2 - x1;
        double Vy = y2 - y1;

        // 2. Calculate the length of the segment vector
        double segmentLength = Math.sqrt(Vx * Vx + Vy * Vy);

        // Handle zero-length segment to avoid division by zero
        if (segmentLength == 0) {
            return null;
        }

        // 3. Normalize the segment vector
        double Ux = Vx / segmentLength;
        double Uy = Vy / segmentLength;

        // 4. Calculate a perpendicular unit vector (rotated 90 degrees clockwise)
        double U_perp_x = -Uy;
        double U_perp_y = Ux;

        // 5. Calculate the intermediate Size for the base (back from P2 along the segment)
        double P_base_x = x2 - arrowLength * Ux;
        double P_base_y = y2 - arrowLength * Uy;

        // 6. Calculate the two base Sizes of the arrowhead
        Size B1 = new Size(
                P_base_x + arrowHalfWidth * U_perp_x,
                P_base_y + arrowHalfWidth * U_perp_y
        );

        Size B2 = new Size(
                P_base_x - arrowHalfWidth * U_perp_x, // Subtract for the other side
                P_base_y - arrowHalfWidth * U_perp_y
        );

        return new Size[]{endSize,B1, B2};
    }
}