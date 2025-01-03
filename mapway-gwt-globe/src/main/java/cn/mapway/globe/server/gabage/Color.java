package cn.mapway.globe.server.gabage;

import cn.mapway.globe.client.core.GlobelConstant;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import elemental2.core.Float32Array;
import jsinterop.annotations.JsType;

@JsType(namespace = GlobelConstant.NAMESPACE, name = "Color")

public class Color {
    double red;
    double green;
    double blue;
    double alpha;

    public Color() {
        red = 0.0;
        green = 0.0;
        blue = 0.0;
        alpha = 1.0;
    }

    public static Color create(double red, double green, double blue, double alpha) {
        return new Color().set(red, green, blue, alpha);
    }

    public static Color fromVec4(Vec4 vec4) {
        return new Color().set(vec4.x, vec4.y, vec4.z, vec4.w);
    }

    /**
     * Converts a 'byte' color component in the range of 0 to 255 into
     * a 'float' color component in the range of 0 to 1.0.
     *
     * @param value
     * @return
     */
    public static double byteToFloat(double value) {
        return value / 255.0;
    }

    /**
     * Converts a 'float' color component in the range of 0 to 1.0 into
     * a 'byte' color component in the range of 0 to 255.
     *
     * @param value
     * @return
     */
    public static double floatToByte(double value) {
        return Math.round(value * 255.0);
    }

    private final static String rgbaMatcher = "^#([0-9a-f])([0-9a-f])([0-9a-f])([0-9a-f])?$";
    private final static String  rrggbbaaMatcher =
            "^#([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})([0-9a-f]{2})?$";
//rgb(), rgba(), or rgb%()
private final static String rgbParenthesesMatcher =
            "^rgba?\\s*\\(\\s*([0-9.]+%?)\\s*[,\\s]+\\s*([0-9.]+%?)\\s*[,\\s]+\\s*([0-9.]+%?)(?:\\s*[,\\s/]+\\s*([0-9.]+))?\\s*\\)$";
//hsl() or hsla()
private final static String hslParenthesesMatcher =
            "^hsla?\\s*\\(\\s*([0-9.]+)\\s*[,\\s]+\\s*([0-9.]+%)\\s*[,\\s]+\\s*([0-9.]+%)(?:\\s*[,\\s/]+\\s*([0-9.]+))?\\s*\\)$";

    /**
     * Creates a Color instance from a CSS color value.
     *
     * @param color
     * @return
     */
    public static Color fromCssColorString(String color) {
        color = color.trim();
        Color namedColor = Colors.get(color.toLowerCase());
        if (namedColor != null) {
            return namedColor.clone();
        }
        RegExp compile = RegExp.compile(rgbaMatcher, "i");
        MatchResult result = compile.exec(color);
        if (result != null) {
            String alpha="f";
            if(result.getGroupCount()>4)
            {
                alpha=result.getGroup(4);
            }
            return new Color().set(
                    Integer.parseInt(result.getGroup(1), 16)/15.,
                    Integer.parseInt(result.getGroup(2), 16)/15.,
                    Integer.parseInt(result.getGroup(3), 16)/15.,
                    Integer.parseInt(alpha, 16)/15.);
        }

        compile = RegExp.compile(rrggbbaaMatcher, "i");
        result = compile.exec(color);
        if (result != null) {
            String alpha="ff";
            if(result.getGroupCount()>4)
            {
                alpha=result.getGroup(4);
            }

            return new Color().set(
                    Integer.parseInt(result.getGroup(1), 16)/255.,
                    Integer.parseInt(result.getGroup(2), 16)/255.,
                    Integer.parseInt(result.getGroup(3), 16)/255.,
                    Integer.parseInt(alpha, 16)/255.);
        }

        compile = RegExp.compile(rgbParenthesesMatcher, "i");
        result = compile.exec(color);
        if (result != null) {
            Color color1 = new Color();
            String red=result.getGroup(1);
            double divide=255.;
            if(red.contains("%"))
            {
                divide=100.;
                red=red.replaceAll("%","");
            }
            color1.red=Double.parseDouble(red)/divide;

            String green=result.getGroup(2);
            if(green.contains("%"))
            {
                divide=100.;
                green=green.replaceAll("%","");
            }
            color1.green=Double.parseDouble(green)/divide;

            String blue=result.getGroup(3);
            if(blue.contains("%"))
            {
                divide=100.;
                blue=blue.replaceAll("%","");
            }
            color1.blue=Double.parseDouble(blue)/divide;

            if(result.getGroupCount()>4){
                String alpha=result.getGroup(4);
                if(alpha.contains("%"))
                {
                    divide=100.;
                    alpha=alpha.replaceAll("%","");
                }
                color1.alpha=Double.parseDouble(alpha)/divide;
            }
            else {
                color1.alpha=1.0;
            }
            return color1;
        }

        compile = RegExp.compile(hslParenthesesMatcher, "i");
        result = compile.exec(color);
        if (result != null) {

            String h=result.getGroup(1);
            String s=result.getGroup(2);
            String l=result.getGroup(3);
            double divide=360;
            if(h.contains("%"))
            {
                divide=100;
                h=h.replaceAll("%","");
            }
            double hslH=Double.parseDouble(h)/divide;

            divide=1.;
            if(s.contains("%"))
            {
                divide=100;
                s=s.replaceAll("%","");
            }
            double hslS=Double.parseDouble(s)/divide;

            divide=1.;
            if(l.contains("%"))
            {
                divide=100;
                l=l.replaceAll("%","");
            }
            double hslL=Double.parseDouble(l)/divide;

            double alpha=1.;
            if(result.getGroupCount()>4){
                String alphaStr=result.getGroup(4);
                 divide=255.;
                if(alphaStr.contains("%"))
                {
                    alphaStr=alphaStr.replaceAll("%","");
                    divide=100;
                }
                alpha=Double.parseDouble(alphaStr)/divide;
            }
            return fromHsl(hslH,hslS,hslL,alpha);
        }
        return null;
    }

    public Color copyTo(Color to) {
        to.red = red;
        to.green = green;
        to.blue = blue;
        to.alpha = alpha;
        return this;
    }
    public static double hue2rgb(double m1, double m2, double h) {
            if (h < 0) {
                h += 1;
            }
            if (h > 1) {
                h -= 1;
            }
            if (h * 6 < 1) {
                return m1 + (m2 - m1) * 6 * h;
            }
            if (h * 2 < 1) {
                return m2;
            }
            if (h * 3 < 2) {
                return m1 + (m2 - m1) * (2 / 3. - h) * 6;
            }
            return m1;
    }
    public static Color fromHsl(double h, double s, double l, double a) {
        double r=l, g=l, b=l;
        if(s != 0) {
            double m2;
            if(l < 0.5) {
                m2 = l * (1 + s);
            } else {
                m2 = l + s - l * s;
            }
            double m1 = 2 * l - m2;
            r = hue2rgb(m1, m2, h + 1 / 3.);
            g = hue2rgb(m1, m2, h);
            b = hue2rgb(m1, m2, h - 1 / 3.);
        }
        return new Color().set(r, g, b, a);
    }
    public Color copyFrom(Color from) {
        red = from.red;
        green = from.green;
        blue = from.blue;
        alpha = from.alpha;
        return this;
    }

    public Color clone() {
        return new Color().set(red, green, blue, alpha);
    }

    public Color fromBytes(int r, int g, int b, int a) {
        red = r / 255.0;
        green = g / 255.0;
        blue = b / 255.0;
        alpha = a / 255.0;
        return this;
    }

    public boolean equals(Color b) {
        return red == b.red && green == b.green && blue == b.blue && alpha == b.alpha;
    }

    public boolean equalsArray(Float32Array array, int offset) {
        return red == array.at(offset) &&
                green == array.at(offset + 1) &&
                blue == array.at(offset + 2) &&
                alpha == array.at(offset + 3);
    }

    public boolean equalsEpsilon(Color b, double epsilon) {
        return Math.abs(red - b.red) <= epsilon
                && Math.abs(green - b.green) <= epsilon
                && Math.abs(blue - b.blue) <= epsilon
                && Math.abs(alpha - b.alpha) <= epsilon;
    }

    public String toString() {
        return "(" + red + "," + green + "," + blue + "," + alpha + ")";
    }

    /**
     * Creates a string containing the CSS color value for this color.
     *
     * @return
     */
    public String toCssColorString() {
        if (alpha == 1.0) {
            return "rgb(" + floatToByte(red) + "," + floatToByte(green) + "," + floatToByte(blue) + ")";
        } else {
            return "rgba(" + floatToByte(red) + "," + floatToByte(green) + "," + floatToByte(blue) + "," + floatToByte(alpha) + ")";
        }
    }

    /**
     * Creates a string containing CSS hex string color value for this color.
     *
     * @return
     */
    public String toCssHexString() {
        String r = Double.toHexString(floatToByte(red));
        String g = Double.toHexString(floatToByte(green));
        String b = Double.toHexString(floatToByte(blue));
        if (r.length() < 2) {
            r = "0" + r;
        }
        if (g.length() < 2) {
            g = "0" + g;
        }
        if (b.length() < 2) {
            b = "0" + b;
        }
        if (alpha < 1) {
            String a = Double.toHexString(floatToByte(alpha));
            if (a.length() < 2) {
                a = "0" + a;
            }
            return "#" + r + g + b + a;
        }
        return "#" + r + g + b;
    }

    /**
     * Converts this color to an array of red, green, blue, and alpha values
     * that are in the range of 0 to 255.
     *
     * @return
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (0xFF & (int) floatToByte(red));
        bytes[1] = (byte) (0xFF & (int) floatToByte(green));
        bytes[2] = (byte) (0xFF & (int) floatToByte(blue));
        bytes[3] = (byte) (0xFF & (int) floatToByte(alpha));
        return bytes;
    }

    /**
     * Converts this color to a single numeric unsigned 32-bit RGBA value, using the endianness
     * of the system.
     *
     * @return
     */
    public long toRgba() {
        int r = (int) floatToByte(red) & 0xFF;
        int g = (int) floatToByte(green) & 0xFF;
        int b = (int) floatToByte(blue) & 0xFF;
        int a = (int) floatToByte(alpha) & 0xFF;
        return ((long) a << 24) | (b << 16) | (g << 8) | r;
    }

    public Float32Array pack(Float32Array array, int offset) {
        array.setAt(offset, red);
        array.setAt(offset + 1, green);
        array.setAt(offset + 2, blue);
        array.setAt(offset + 3, alpha);
        return array;
    }

    public Color unpack(Float32Array array, int offset) {
        red = array.at(offset);
        green = array.at(offset + 1);
        blue = array.at(offset + 2);
        alpha = array.at(offset + 3);
        return this;
    }

    /**
     * Brightens this color by the provided magnitude.
     *
     * @param magnitude
     * @return
     */
    public Color brighten(double magnitude) {
        magnitude = 1.0 - magnitude;
        red = 1.0 - (1.0 - this.red) * magnitude;
        green = 1.0 - (1.0 - this.green) * magnitude;
        blue = 1.0 - (1.0 - this.blue) * magnitude;
        return this;
    }

    /**
     * Darkens this color by the provided magnitude.
     *
     * @param magnitude
     * @return
     */
    public Color darken(double magnitude) {
        magnitude = 1.0 - magnitude;
        red = this.red * magnitude;
        green = this.green * magnitude;
        blue = this.blue * magnitude;
        return this;
    }

    /**
     * Creates a new Color that has the same red, green, and blue components
     * as this Color, but with the specified alpha value.m alpha
     *
     * @return
     */
    public Color copyWithAlpha(double alpha) {
        return new Color().set(red, green, blue, alpha);
    }

    /**
     * Computes the componentwise sum of two Colors.
     *
     * @param right
     * @return
     */
    public Color add(Color right) {
        red += right.red;
        green += right.green;
        blue += right.blue;
        alpha += right.alpha;
        return this;
    }

    /**
     * Computes the componentwise difference of two Colors.
     *
     * @param right
     * @return
     */
    public Color subtract(Color right) {
        red -= right.red;
        green -= right.green;
        blue -= right.blue;
        alpha -= right.alpha;
        return this;
    }

    /**
     * Computes the componentwise product of two Colors.
     *
     * @param right
     * @return
     */
    public Color multiply(Color right) {
        red *= right.red;
        green *= right.green;
        blue *= right.blue;
        alpha *= right.alpha;
        return this;
    }

    /**
     * Computes the componentwise quotient of two Colors.
     *
     * @param right
     * @return
     */
    public Color divide(Color right) {
        red /= right.red;
        green /= right.green;
        blue /= right.blue;
        alpha /= right.alpha;
        return this;
    }

    /**
     * Computes the componentwise modulus of two Colors
     *
     * @param right
     * @return
     */
    public Color mod(Color right) {

        red %= right.red;
        green %= right.green;
        blue %= right.blue;
        alpha %= right.alpha;
        return this;
    }

    /**
     * Computes the linear interpolation or extrapolation at t between the provided colors.
     *
     * @param right
     * @param t
     * @return
     */
    public Color lerp(Color right, double t) {

        red = Maths.lerp(red, right.red, t);
        green = Maths.lerp(green, right.green, t);
        blue = Maths.lerp(blue, right.blue, t);
        alpha = Maths.lerp(alpha, right.alpha, t);
        return this;
    }

    /**
     * Multiplies the provided Color componentwise by the provided scalar.
     *
     * @param scale
     * @return
     */
    public Color scale(double scale) {
        red *= scale;
        green *= scale;
        blue *= scale;
        alpha *= scale;
        return this;
    }

    private Color set(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        return this;
    }


}
