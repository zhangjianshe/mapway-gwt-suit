package cn.mapway.ui.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Random;

/**
 * Colors
 * 颜色处理
 *
 * @author zhangjianshe@gmail.com
 */
public class Colors {
    public final static String COLOR_BLACK = "#4f4f4d";
    public final static String COLOR_RED = "#f1625a";
    public final static String COLOR_YELLOW = "#fae069";
    public final static String COLOR_BLUE = "#317aa3";
    public final static String COLOR_GREEN = "#7ac1b1";
    private static final String[] basicColors = {"#000000",
            "#FFFFFF",
            "#FF0000",
            "#00FF00",
            "#0000FF",
            "#FFFF00",
            "#00FFFF",
            "#FF00FF",
            "#C0C0C0",
            "#808080",
            "#800000",
            "#808000",
            "#008000",
            "#800080",
            "#008080",
            "#000080"};
    private static final String[] allColors = {
            "#800000",
            "#8B0000",
            "#A52A2A",
            "#B22222",
            "#DC143C",
            "#FF0000",
            "#FF6347",
            "#FF7F50",
            "#CD5C5C",
            "#F08080",
            "#E9967A",
            "#FA8072",
            "#FFA07A",
            "#FF4500",
            "#FF8C00",
            "#FFA500",
            "#FFD700",
            "#B8860B",
            "#DAA520",
            "#EEE8AA",
            "#BDB76B",
            "#F0E68C",
            "#808000",
            "#FFFF00",
            "#9ACD32",
            "#556B2F",
            "#6B8E23",
            "#7CFC00",
            "#7FFF00",
            "#ADFF2F",
            "#006400",
            "#008000",
            "#228B22",
            "#00FF00",
            "#32CD32",
            "#90EE90",
            "#98FB98",
            "#8FBC8F",
            "#00FA9A",
            "#00FF7F",
            "#2E8B57",
            "#66CDAA",
            "#3CB371",
            "#20B2AA",
            "#2F4F4F",
            "#008080",
            "#008B8B",
            "#00FFFF",
            "#00FFFF",
            "#E0FFFF",
            "#00CED1",
            "#40E0D0",
            "#48D1CC",
            "#AFEEEE",
            "#7FFFD4",
            "#B0E0E6",
            "#5F9EA0",
            "#4682B4",
            "#6495ED",
            "#00BFFF",
            "#1E90FF",
            "#ADD8E6",
            "#87CEEB",
            "#87CEFA",
            "#191970",
            "#000080",
            "#00008B",
            "#0000CD",
            "#0000FF",
            "#4169E1",
            "#8A2BE2",
            "#4B0082",
            "#483D8B",
            "#6A5ACD",
            "#7B68EE",
            "#9370DB",
            "#8B008B",
            "#9400D3",
            "#9932CC",
            "#BA55D3",
            "#800080",
            "#D8BFD8",
            "#DDA0DD",
            "#EE82EE",
            "#FF00FF",
            "#DA70D6",
            "#C71585",
            "#DB7093",
            "#FF1493",
            "#FF69B4",
            "#FFB6C1",
            "#FFC0CB",
            "#FAEBD7",
            "#F5F5DC",
            "#FFE4C4",
            "#FFEBCD",
            "#F5DEB3",
            "#FFF8DC",
            "#FFFACD",
            "#FAFAD2",
            "#FFFFE0",
            "#8B4513",
            "#A0522D",
            "#D2691E",
            "#CD853F",
            "#F4A460",
            "#DEB887",
            "#D2B48C",
            "#BC8F8F",
            "#FFE4B5",
            "#FFDEAD",
            "#FFDAB9",
            "#FFE4E1",
            "#FFF0F5",
            "#FAF0E6",
            "#FDF5E6",
            "#FFEFD5",
            "#FFF5EE",
            "#F5FFFA",
            "#708090",
            "#778899",
            "#B0C4DE",
            "#E6E6FA",
            "#FFFAF0",
            "#F0F8FF",
            "#F8F8FF",
            "#F0FFF0",
            "#FFFFF0",
            "#F0FFFF",
            "#FFFAFA",
            "#000000",
            "#696969",
            "#808080",
            "#A9A9A9",
            "#C0C0C0",
            "#D3D3D3",
            "#DCDCDC",
            "#F5F5F5",
            "#FFFFFF"
    };
    /**
     * rgba(r, g, b, a)
     * rgb(r, g, b)
     *
     * @param color
     * @return
     */
    static RegExp rgbExp = RegExp.compile("rgb\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*\\)");
    static RegExp rgbaExp = RegExp.compile("rgba\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3}\\.[0-9]*)\\s*\\)");
    static RegExp rgbHex = RegExp.compile("#([0-9A-F]{2,2})([0-9A-F]{2,2})([0-9A-F]{2,2})");

    public static String randomBasicColor() {
        int index = Random.nextInt(basicColors.length);
        return basicColors[index];
    }

    public static String randomColor() {
        int index = Random.nextInt(allColors.length);
        return allColors[index];
    }

    public static String color(int index) {
        if (index >= 0 && index < allColors.length) return allColors[index];
        return allColors[0];
    }

    /**
     * 颜色转换 rgb(12,12,12) -> #112233
     *
     * @param bgColor
     * @return
     */
    public static String toHex(String bgColor) {
        if (bgColor == null || bgColor.length() == 0)
            return "#ffffff";
        if (bgColor.startsWith("#")) {
            return bgColor;
        }
        if (bgColor.startsWith("rgb(")) {
            String[] colors = bgColor.substring(4, bgColor.length() - 1).split(",");
            if (colors.length == 3) {
                int r = Integer.parseInt(colors[0].trim());
                int g = Integer.parseInt(colors[1].trim());
                int b = Integer.parseInt(colors[2].trim());
                return "#" + toHex(r) + toHex(g) + toHex(b);
            } else {
                Logs.info("color format wrong " + bgColor);
                return "#ffffff";
            }
        }
        Logs.info("unknown color format " + bgColor);
        return "#ffffff";
    }

    private static String toHex(int r) {
        String s = Integer.toHexString(r);
        if (s.length() == 1)
            return "0" + s;
        else
            return s;
    }

    public static int[] hsv2rgb(double hue, double saturation, double value) {
        double r = 0., g = 0., b = 0., f, p, q, t;
        int i = (int) Math.floor(hue * 6);
        f = hue * 6 - i;
        p = value * (1 - saturation);
        q = value * (1 - f * saturation);
        t = value * (1 - (1 - f) * saturation);
        switch (i % 6) {
            case 0:
                r = value;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = value;
                b = p;
                break;
            case 2:
                r = p;
                g = value;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = value;
                break;
            case 4:
                r = t;
                g = p;
                b = value;
                break;
            case 5:
                r = value;
                g = p;
                b = q;
                break;
        }

        int[] rgb = {
                (int) Math.round(r * 255),
                (int) Math.round(g * 255),
                (int) Math.round(b * 255)};
        return rgb;
    }

    public static double[] rgb2hsv(int r, int g, int b) {
        double[] hsbvals = new double[3];
        float hue, saturation, brightness;

        int cmax = Math.max(r, g);
        if (b > cmax) cmax = b;
        int cmin = Math.min(r, g);
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    public static String toRGB(int[] rgb) {
        return "#" + toHex(rgb[0]) + toHex(rgb[1]) + toHex(rgb[2]);
    }

    public static String bytesToRGB(byte[] rgb) {
        return "#" + toHex(rgb[0] & 0xFF) + toHex(rgb[1] & 0xFF) + toHex(rgb[2] & 0xFF);
    }

    public static String index(int i) {
        return allColors[i % allColors.length];
    }

    /**
     * @param color #FF00EE
     * @return
     */
    public static String invertColor(String color, boolean bw) {
        byte[] rgb = fromRgba(color);
        if (rgb == null) {
            return "#000000";
        }
        int r = rgb[0] & 0xFF;
        int g = rgb[1] & 0xFF;
        int b = rgb[2] & 0xFF;
        Logs.info(r + " " + g + " " + b);
        if (bw) {
            return (r * 0.299 + g * 0.587 + b * 0.114) > 186
                    ? "#000000"
                    : "#FFFFFF";
        }
        rgb[0] = (byte) (0xFF & (255 - r));
        rgb[1] = (byte) (0xFF & (255 - g));
        rgb[2] = (byte) (0xFF & (255 - b));
        return toColor(rgb);
    }

    public static byte[] fromRgba(String color) {
        color = color.toLowerCase();
        String r;
        String g;
        String b;
        String a;
        if (color.startsWith("rgba")) {
            MatchResult exec = rgbaExp.exec(color);
            if (exec == null) {

                return null;
            }
            r = exec.getGroup(1);
            g = exec.getGroup(2);
            b = exec.getGroup(3);
            a = exec.getGroup(4);
        } else if (color.startsWith("rgb")) {
            MatchResult exec = rgbExp.exec(color);
            if (exec == null) {
                return null;
            }
            r = exec.getGroup(1);
            g = exec.getGroup(2);
            b = exec.getGroup(3);
            a = "";
        } else {
            return null;
        }
        byte[] rgb;
        if (a.length() > 0) {
            rgb = new byte[4];
            rgb[0] = (byte) (0xff & Integer.parseInt(r));
            rgb[1] = (byte) (0xff & Integer.parseInt(g));
            rgb[2] = (byte) (0xff & Integer.parseInt(b));
            Double d = Double.parseDouble(a);
            if (d >= 0 && d < 1.0) {
                rgb[3] = (byte) (0xff & (int) Math.floor(d * 255));
            } else if (d < 0) {
                rgb[3] = (byte) (0xFF);
            } else if (d < 256) {
                rgb[3] = (byte) (0xff & (int) Math.floor(d));
            } else {
                rgb[3] = (byte) 0xFF;
            }
        } else {
            rgb = new byte[3];
            rgb[0] = (byte) (0xff & Integer.parseInt(r));
            rgb[1] = (byte) (0xff & Integer.parseInt(g));
            rgb[2] = (byte) (0xff & Integer.parseInt(b));
        }
        return rgb;
    }

    public static String toColor(byte[] singleColor) {
        if (singleColor == null) return "";
        StringBuilder color = new StringBuilder("#");
        for (int i = 0; i < singleColor.length; i++) {
            int number = singleColor[i] & 0xff;
            String v = Integer.toHexString(number);
            color.append(v.length() == 1 ? ("0" + v) : v);
        }
        return color.toString();
    }

    public static void main(String[] args) {
        byte[] bytes=fromColor("#FF0000");
        System.out.println((int)(bytes[0]&0xFF));
        System.out.println((int)(bytes[1]&0xFF));
        System.out.println((int)(bytes[2]&0xFF));

    }

    /**
     * from #AABBCC to bytes
     * @param fillColor
     * @return
     */
    public static byte[] fromColor(String fillColor) {
        if (fillColor == null || fillColor.length() == 0) return null;
        String temp = fillColor.toUpperCase();
        MatchResult matchResult = rgbHex.exec(temp);
        if (matchResult != null) {
            byte[] result = new byte[4];
            result[0] = (byte) (Integer.parseInt(matchResult.getGroup(1), 16) & 0xFF);
            result[1] = (byte) (Integer.parseInt(matchResult.getGroup(2), 16) & 0xFF);
            result[2] = (byte) (Integer.parseInt(matchResult.getGroup(3), 16) & 0xFF);
            result[3] = (byte) 0xFF;
            return result;
        }
        return null;
    }
}
