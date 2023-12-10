package cn.mapway.ui.client.widget.color;

import cn.mapway.ui.client.util.Colors;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * ColorData
 *
 * @author zhang
 */
public class ColorData implements Serializable, IsSerializable {
    public boolean show = true;
    int color;

    public ColorData(Integer color) {
        this.color = color;
    }

    public ColorData() {
        this(0);
    }

    public String toHex() {
        return hex(Colors.r(color)) + hex(Colors.g(color)) + hex(Colors.b(color));
    }

    public int getAlpha() {
        return Colors.a(color);
    }

    /**
     * alpha is 0-255
     *
     * @param alpha
     * @return
     */
    public ColorData setAlpha(int alpha) {
        color=Colors.setA(color, 0xFF & alpha);
        return this;
    }

    public String formatAlpha() {
        int alpha=a();
        double b=alpha * 100.0 / 0xFF;
        return StringUtil.formatDouble(b, 0) + "%";
    }


    public String toRGBA() {
        return Colors.toRGBA(color);
    }

    private String hex(int value) {
        if (value < 0) {
            value = 0;
        }
        if (value > 255) {
            value = 255;
        }
        String string = Integer.toHexString(value);
        if (string.length() < 2) {
            return "0" + string;
        } else {
            return string;
        }
    }

    /**
     * RRGGBB
     *
     * @param value
     */
    public void parseHex(String value) {
        try {
            if (value.length() == 6) {
                int r = Integer.parseInt(value.substring(0, 2), 16);
                int g = Integer.parseInt(value.substring(2, 4), 16);
                int b = Integer.parseInt(value.substring(4, 6), 16);
                color = Colors.fromColorInt(r, g, b, 0xFF);
            }
        } catch (Exception e) {
        }
    }

    public byte[] toBytes() {
        return Colors.toBytes(color);
    }

    public ColorData fromBytes(byte[] bytes) {
        if (bytes == null) {
            color = Colors.fromColorInt(0, 0, 0, 0xFF);
        }
        if (bytes.length == 4) {
            color = Colors.fromColorInt(bytes[0], bytes[1], bytes[2], bytes[3]);
        } else if (bytes.length == 3) {
            color = Colors.fromColorInt(bytes[0], bytes[1], bytes[2], 0xFF);
        } else if (bytes.length == 2) {
            color = Colors.fromColorInt(bytes[0], bytes[1], bytes[1], 0xFF);
        } else if (bytes.length == 1) {
            color = Colors.fromColorInt(bytes[0], bytes[0], bytes[0], 0xFF);
        }
        return this;
    }
    public ColorData fromIntegers(int[] bytes) {
        if (bytes == null) {
            color = Colors.fromColorInt(0, 0, 0, 0xFF);
        }
        if (bytes.length == 4) {
            color = Colors.fromColorInt(bytes[0], bytes[1], bytes[2], bytes[3]);
        } else if (bytes.length == 3) {
            color = Colors.fromColorInt(bytes[0], bytes[1], bytes[2], 0xFF);
        } else if (bytes.length == 2) {
            color = Colors.fromColorInt(bytes[0], bytes[1], bytes[1], 0xFF);
        } else if (bytes.length == 1) {
            color = Colors.fromColorInt(bytes[0], bytes[0], bytes[0], 0xFF);
        }
        return this;
    }


    public int getColor() {
        return color;
    }

    public ColorData setColor(int color) {
        this.color = color;
        return this;

    }

    public int r() {
        return Colors.r(color);
    }

    public int g() {
        return Colors.g(color);
    }

    public int b() {
        return Colors.b(color);
    }

    public int a() {
        return Colors.a(color);
    }

    public ColorData setRGBA(int r,int g,int b,int a)
    {
       color= Colors.fromColorInt(r,g,b,a);
       return this;
    }
    public void copyDataFrom(ColorData data) {
        color = data.color;
    }
}
