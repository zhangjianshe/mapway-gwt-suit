package cn.mapway.ui.client.widget.color;


/**
 * The Class Color.
 */
public class Color {

    /**
     * The Constant WHITE.
     */
    public final static Color WHITE = new Color(255, 255, 255);

    /**
     * The Constant LIGHT_GRAY.
     */
    public final static Color LIGHT_GRAY = new Color(192, 192, 192);

    /**
     * The Constant GRAY.
     */
    public final static Color GRAY = new Color(128, 128, 128);

    /**
     * The Constant DARK_GRAY.
     */
    public final static Color DARK_GRAY = new Color(64, 64, 64);

    /**
     * The Constant BLACK.
     */
    public final static Color BLACK = new Color(0, 0, 0);

    /**
     * The Constant RED.
     */
    public final static Color RED = new Color(255, 0, 0);

    /**
     * The Constant PINK.
     */
    public final static Color PINK = new Color(255, 175, 175);

    /**
     * The Constant ORANGE.
     */
    public final static Color ORANGE = new Color(255, 200, 0);

    /**
     * The Constant YELLOW.
     */
    public final static Color YELLOW = new Color(255, 255, 0);

    /**
     * The Constant GREEN.
     */
    public final static Color GREEN = new Color(0, 255, 0);

    /**
     * The Constant MAGENTA.
     */
    public final static Color MAGENTA = new Color(255, 0, 255);

    /**
     * The Constant CYAN.
     */
    public final static Color CYAN = new Color(0, 255, 255);

    /**
     * The Constant BLUE.
     */
    public final static Color BLUE = new Color(0, 0, 255);

    /**
     * The b.
     */
    private int r, g, b;

    /**
     * Instantiates a new color.
     *
     * @param hex the hex
     */
    public Color(String hex) {
        if (hex.startsWith("#")) {
            try {
                int rgb = Integer.decode(hex);
                r = (rgb & 0xff0000) >> 16;
                g = (rgb & 0x00ff00) >> 8;
                b = rgb & 0x0000ff;
            } catch (Exception e) {
                r = g = b = 0xff;
            }
        } else if (hex.startsWith("rgb")){
            // "rgb(0, 0, 0)"
            try {
                hex = hex.substring(4, hex.length() - 1);
                // 0,0,0
                String[] split = hex.split(",");
                if(split.length != 3){
                    split = hex.split(" ");
                }
                r = Integer.parseInt(split[0].trim());
                g = Integer.parseInt(split[1].trim());
                b = Integer.parseInt(split[2].trim());
            } catch (Exception e) {
                r = g = b = 0xff;
            }
        } else if (hex.startsWith("rgba")){

        } else {
            r = g = b = 0xff;
        }
    }

    /**
     * Instantiates a new color.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     */
    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Instantiates a new color.
     *
     * @param h the h
     * @param s the s
     * @param l the l
     */
    public Color(float h, float s, float l) {
        float q = 0;
        if (l < 0.5f)
            q = l * (s + 1.0f);
        else
            q = l + s - (l * s);

        float p = 2.0f * l - q;
        float hk = h / 360.0f;

        float tr = hk + 1.0f / 3;
        float tg = hk;
        float tb = hk - 1.0f / 3;

        this.r = (int) (getComponent(tr, q, p) * 255f);
        this.g = (int) (getComponent(tg, q, p) * 255f);
        this.b = (int) (getComponent(tb, q, p) * 255f);
    }

    /**
     * Gets the component.
     *
     * @param tc the tc
     * @param q  the q
     * @param p  the p
     * @return the component
     */
    private float getComponent(float tc, float q, float p) {
        if (tc < 0)
            tc += 1;
        else if (tc > 1)
            tc -= 1;

        if (tc < (1f / 6f))
            tc = p + ((q - p) * 6f * tc);
        else if ((1f / 6f) <= tc && tc < 0.5f)
            tc = q;
        else if (0.5f <= tc && tc < (2f / 3f))
            tc = p + ((q - p) * 6.0f * (2f / 3f - tc));
        else
            tc = p;

        return tc;
    }

    /**
     * Gets the red.
     *
     * @return the red
     */
    public int getRed() {
        return r;
    }

    /**
     * Gets the green.
     *
     * @return the green
     */
    public int getGreen() {
        return g;
    }

    /**
     * Gets the blue.
     *
     * @return the blue
     */
    public int getBlue() {
        return b;
    }

    /**
     * Gets the hue.
     *
     * @return the hue
     */
    public float getHue() {
        float r = this.r / 255f;
        float g = this.g / 255f;
        float b = this.b / 255f;

        float max = getMax(r, g, b);
        float min = getMin(r, g, b);

        if (max == min) {
            return 0;
        } else if (max == r) {
            return (60f * ((g - b) / max - min) + 360) % 360;
        } else if (max == g) {
            return (60f * ((b - r) / max - min) + 120);
        } else if (max == b) {
            return (60f * ((r - g) / max - min) + 240);
        } else {
            return 0;
        }
    }

    /**
     * Gets the saturation.
     *
     * @return the saturation
     */
    public float getSaturation() {
        float r = this.r / 255f;
        float g = this.g / 255f;
        float b = this.b / 255f;

        float l = getLightness();
        float max = getMax(r, g, b);
        float min = getMin(r, g, b);

        if (max == min) {
            return 0;
        } else if (l <= 0.5) {
            return (max - min) / (2f * l);
        } else if (l > 0.5) {
            return (max - min) / (2f - 2f * l);
        } else {
            return 0;
        }
    }

    /**
     * Gets the lightness.
     *
     * @return the lightness
     */
    public float getLightness() {
        float r = this.r / 255f;
        float g = this.g / 255f;
        float b = this.b / 255f;

        return (getMax(r, g, b) + getMin(r, g, b)) / 2f;
    }

    /**
     * Gets the max.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @return the max
     */
    private float getMax(float r, float g, float b) {
        return r > g ? (r > b ? r : b) : (g > b ? g : b);
    }

    /**
     * Gets the min.
     *
     * @param r the r
     * @param g the g
     * @param b the b
     * @return the min
     */
    private float getMin(float r, float g, float b) {
        return r < g ? (r < b ? r : b) : (g < b ? g : b);
    }

    /**
     * Pad.
     *
     * @param in the in
     * @return the string
     */
    private String pad(String in) {
        if (in.length() == 0) {
            return "00";
        }
        if (in.length() == 1) {
            return "0" + in;
        }
        return in;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "#"
                + pad(Integer.toHexString(r))
                + pad(Integer.toHexString(g))
                + pad(Integer.toHexString(b));
    }

}
