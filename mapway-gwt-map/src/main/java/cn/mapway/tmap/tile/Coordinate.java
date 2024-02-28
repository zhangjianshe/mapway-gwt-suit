package cn.mapway.tmap.tile;

import lombok.Data;

/**
 * An array of numbers representing an xy coordinate. Example: `[16, 48]`
 */
@Data
public class Coordinate {
    double x;
    double y;

    public Coordinate add(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }
    public Coordinate add(Coordinate source) {
        this.x += source.x;
        this.y += source.y;
        return this;
    }

    /**
     * Calculates the point closest to the passed coordinate on the passed circle.
     * @param dx
     * @param dy
     * @return
     */
    public Coordinate closestOnCircle(double dx, double dy) {
        return null;
    }
}
