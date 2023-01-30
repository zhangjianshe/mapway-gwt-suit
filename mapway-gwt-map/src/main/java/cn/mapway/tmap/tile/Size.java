package cn.mapway.tmap.tile;

import lombok.Data;

@Data
public class Size {
    double x;
    double y;
    public Size(double x, double y) {
        set(x,y);
    }
    public Size()
    {
        set(0.0,0.0);
    }
    public Size set(double x, double y)
    {
        this.x = x;
        this.y = y;
        return this;
    }
}
