package cn.mapway.tmap.tile;

import lombok.Data;

/**
 * @author zhangjianshe@gmail.com
 * 瓦片范围
 */
@Data
public class TileRange {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public boolean contains(double x, double y)
    {
        return this.minX <= x && x <= this.maxX && this.minY <= y && y <= this.maxY;
    }

    public boolean containsTileRange(TileRange targetTileRange)
    {
        return this.minX <= targetTileRange.minX &&
                targetTileRange.maxX <= this.maxX &&
                this.minY <= targetTileRange.minY &&
                targetTileRange.maxY <= this.maxY;
    }

    public boolean equals(TileRange tileRange)
    {
        return  this.minX == tileRange.minX &&
                this.minY == tileRange.minY &&
                this.maxX == tileRange.maxX &&
                this.maxY == tileRange.maxY;
    }
    public TileRange extend(TileRange tileRange)
    {
        if (tileRange.minX < this.minX) {
            this.minX = tileRange.minX;
        }
        if (tileRange.maxX > this.maxX) {
            this.maxX = tileRange.maxX;
        }
        if (tileRange.minY < this.minY) {
            this.minY = tileRange.minY;
        }
        if (tileRange.maxY > this.maxY) {
            this.maxY = tileRange.maxY;
        }
        return this;
    }

    public double getHeight() {
        return this.maxY - this.minY + 1;
    }
    public double getWidth() {
        return this.maxX - this.minX + 1;
    }
    public boolean intersects(TileRange tileRange) {
        return (
                this.minX <= tileRange.maxX &&
                        this.maxX >= tileRange.minX &&
                        this.minY <= tileRange.maxY &&
                        this.maxY >= tileRange.minY
        );
    }

    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }
}
