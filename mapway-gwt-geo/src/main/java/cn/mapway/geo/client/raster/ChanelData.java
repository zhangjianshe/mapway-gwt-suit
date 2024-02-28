package cn.mapway.geo.client.raster;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * ChanlData
 * 波段数据 是以 1为基地
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class ChanelData implements Serializable, IsSerializable {
    public int redChanel;
    public int greenChanel;
    public int blueChanel;

    public ChanelData() {
        setData(1, 2, 3);
    }
    public ChanelData(int r, int g, int b) {
        setData(r,g,b);
    }
    public void setData(int r, int g, int b) {
        redChanel = r;
        greenChanel = g;
        blueChanel = b;
    }

    public String toString() {
        return redChanel + "," + greenChanel + "," + blueChanel;
    }
}
