package cn.mapway.ui.client.widget.color;


import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.Colors;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.Dropdown;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ColorChooser
 * 颜色选择器 比较高级
 *
 * @author zhang
 */
public class ColorChooser extends CommonEventComposite implements IData<ColorData>, RequiresResize {
    public final static Integer SHOW_TYPE_RGB = 0;
    public final static Integer SHOW_TYPE_HSV = 1;
    public final static Integer SHOW_TYPE_HEX = 2;
    private static final ColorChooserUiBinder ourUiBinder = GWT.create(ColorChooserUiBinder.class);
    private static Dialog<ColorChooser> GLOBAL_CHOOSER;
    private static Popup<ColorChooser> POPUP_CHOOSER;
    Integer showType = SHOW_TYPE_RGB;
    @UiField
    ColorCanvas colorCanvas;
    private final ValueChangeHandler<Double> sliderHandler = (event) -> {
        double v = event.getValue();
        colorCanvas.draw(v / 360, true);
    };
    @UiField
    HueChooser hueSelector;
    @UiField
    TransparentChooser transparentSelector;
    @UiField
    TextBox txtR;
    @UiField
    TextBox txtG;
    @UiField
    TextBox txtB;
    @UiField
    TextBox txtAlpha;
    private final ValueChangeHandler<Double> alphaHandler = (event) -> {
        double alpha = event.getValue();
        // alpha is [0-255]
        ColorData color = colorCanvas.getColor();
        color.setAlpha((int) (alpha * 0xFF));
        showColor(color);
        fireEvent(CommonEvent.colorEvent(color));
    };
    @UiField
    Dropdown ddlType;
    @UiField
    HorizontalPanel colorPanel;
    @UiField
    DockLayoutPanel root;
    ColorData colorData;
    FocusHandler focusHandler = (event) -> {
        colorPanel.getElement().setAttribute(SELECT_ATTRIBUTE, "true");
        TextBox textBox = (TextBox) event.getSource();
        textBox.setSelectionRange(0, textBox.getValue().length());
    };
    BlurHandler blurHandler = (event) -> {
        colorPanel.getElement().removeAttribute(SELECT_ATTRIBUTE);
        parseValueAndFire();
    };
    KeyDownHandler keyDownHandler = (event) -> {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            parseValueAndFire();
            TextBox textBox = (TextBox) event.getSource();
            textBox.setSelectionRange(0, textBox.getValue().length());
        }
    };

    public ColorChooser() {
        initWidget(ourUiBinder.createAndBindUi(this));
        hueSelector.addValueChangeHandler(sliderHandler);
        transparentSelector.addValueChangeHandler(alphaHandler);
        setAttribute(txtR.getElement(), "h", "true");
        setAttribute(txtG.getElement(), "h", "true");
        setAttribute(txtB.getElement(), "h", "true");
        installEvent();
        ddlType.addItem("", "RGB", SHOW_TYPE_RGB);
        ddlType.addItem("", "HSL", SHOW_TYPE_HSV);
        ddlType.addItem("", "HEX", SHOW_TYPE_HEX);
        ddlType.setSelectedIndex(0);
        ddlType.addValueChangeHandler(event -> {
            showType = (Integer) event.getValue();
            showColor(colorData);
        });
    }

    public static Dialog<ColorChooser> getDialog(boolean reuse) {
        if (reuse) {

            if (GLOBAL_CHOOSER == null) {
                GLOBAL_CHOOSER = createOne();
            }
            return GLOBAL_CHOOSER;
        } else {
            return createOne();
        }
    }

    private static Dialog<ColorChooser> createOne() {
        ColorChooser colorChooser = new ColorChooser();
        Dialog<ColorChooser> dialog = new Dialog<>(colorChooser, "选择颜色");
        dialog.setGlassEnabled(false);
        return dialog;

    }

    public static Popup<ColorChooser> getPopup(boolean reuse) {
        if (reuse) {
            if (POPUP_CHOOSER == null) {
                POPUP_CHOOSER = createPopup();
            }
            return POPUP_CHOOSER;
        } else {
            return createPopup();
        }
    }

    private static Popup<ColorChooser> createPopup() {
        ColorChooser colorChooser = new ColorChooser();
        Popup<ColorChooser> popup = new Popup<>(colorChooser);
        popup.setGlassEnabled(false);
        popup.setAutoHideEnabled(true);
        return popup;
    }

    /**
     * 输入事件
     */
    private void installEvent() {
        txtAlpha.addFocusHandler(event -> {
            colorPanel.getElement().setAttribute(SELECT_ATTRIBUTE, "true");
            txtAlpha.setSelectionRange(0, txtAlpha.getValue().length());
        });
        //用户编辑了透明度 0-255 我们将
        txtAlpha.addBlurHandler(event -> {
            colorPanel.getElement().removeAttribute(SELECT_ATTRIBUTE);
            int alpha = Colors.parseAlpha(txtAlpha.getValue());
            colorData.setAlpha(alpha);
            toUI();
            showColor(colorData);
            fireEvent(CommonEvent.colorEvent(colorData));
        });
        txtAlpha.addKeyDownHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                int alpha = Colors.parseAlpha(txtAlpha.getValue());
                colorData.setAlpha(alpha);
                toUI();
                showColor(colorData);
                fireEvent(CommonEvent.colorEvent(colorData));
            }
        });
        txtAlpha.addMouseWheelHandler(event -> {
            //下面的代码是CHATGPT优化过的
            int v = colorData.getAlpha();
            String value = txtAlpha.getValue();
            String temp = value.trim();

            if (temp.endsWith("%")) {
                temp = temp.substring(0, temp.length() - 1).trim();
                if (!temp.isEmpty()) {
                    int percent = Integer.parseInt(temp); // 50%->50
                    int delta = event.getDeltaY() > 0 ? -1 : 1;
                    v = Math.min(255, Math.max(0, (int) (2.55 * (delta + percent))));
                }
            } else {
                if (!temp.isEmpty()) {
                    int delta = event.getDeltaY() > 0 ? -1 : 1;
                    v = Math.min(255, Math.max(0, v + delta));
                }
            }

            colorData.setAlpha(v);
            toUI();
            showColor(colorData);
            fireEvent(CommonEvent.colorEvent(colorData));
        });

        txtR.addFocusHandler(focusHandler);
        txtG.addBlurHandler(blurHandler);
        txtR.addKeyDownHandler(keyDownHandler);
        txtG.addFocusHandler(focusHandler);
        txtG.addBlurHandler(blurHandler);
        txtG.addKeyDownHandler(keyDownHandler);
        txtB.addFocusHandler(focusHandler);
        txtB.addBlurHandler(blurHandler);
        txtB.addKeyDownHandler(keyDownHandler);
    }

    public void parseValueAndFire() {
        // alpha is [0,255]
        Integer alpha = Colors.parseAlpha(txtAlpha.getValue());

        if (showType.equals(SHOW_TYPE_RGB)) {
            colorData.setRGBA(parseValue(txtR.getValue()), parseValue(txtG.getValue()), parseValue(txtB.getValue()), alpha);
        } else if (showType.equals(SHOW_TYPE_HEX)) {
            colorData.setRGBA(parseHex(txtR.getValue()), parseHex(txtG.getValue()), parseHex(txtB.getValue()), alpha);
        } else if (showType.equals(SHOW_TYPE_HSV)) {
            double hue = parseDouble(txtR.getValue()) / 360;
            double saturation = parseDouble(txtG.getValue());
            double value = parseDouble(txtB.getValue());
            int[] hsv = Colors.hsv2rgb(hue, saturation, value);
            colorData.setRGBA(hsv[0], hsv[1], hsv[2], alpha);
        }

        toUI();
        showColor(colorData);
        fireEvent(CommonEvent.colorEvent(colorData));
    }

    private double parseDouble(String color) {
        if (color == null || color.length() == 0) {
            return .0;
        }
        try {
            return Double.parseDouble(color);
        } catch (Exception e) {
            return .0;
        }
    }

    private int parseValue(String color) {
        if (color == null || color.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(color);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parseHex(String color) {
        if (color == null || color.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(color, 16);
        } catch (Exception e) {
            return 0;
        }
    }

    private int parsePercent(String value) {
        if (value != null && value.length() > 0) {
            int pos = value.lastIndexOf("%");
            if (pos > 0) {
                value = value.substring(0, pos);
            }
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }


    private void setAttribute(Element element, String attr, String value) {
        element.setAttribute(attr, value);
    }

    private void showColor(ColorData color) {
        if (showType.equals(SHOW_TYPE_RGB)) {
            txtR.setValue(String.valueOf(color.r()));
            txtG.setValue(String.valueOf(color.g()));
            txtB.setValue(String.valueOf(color.b()));
        } else if (showType.equals(SHOW_TYPE_HEX)) {
            txtR.setValue(toHex(color.r()));
            txtG.setValue(toHex(color.g()));
            txtB.setValue(toHex(color.b()));
        } else if (showType.equals(SHOW_TYPE_HSV)) {
            double[] hsv = Colors.rgb2hsv(color.r(), color.g(), color.b());
            txtR.setValue(StringUtil.formatDouble(hsv[0] * 360, 0));
            txtG.setValue(StringUtil.formatDouble(hsv[1], 2));
            txtB.setValue(StringUtil.formatDouble(hsv[2], 2));
        }
        txtAlpha.setValue(color.formatAlpha());
    }

    private String toHex(int r) {
        String v = Integer.toHexString(r);
        if (v.length() == 1) {
            v = "0" + v;
        }
        return v;
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(300, 450);
    }

    @UiHandler("colorCanvas")
    public void colorCanvasCommon(CommonEvent event) {
        if (event.isColor()) {
            ColorData color = event.getValue();
            color.setAlpha((int) (255 * transparentSelector.getValue()));
            transparentSelector.setColor(color, false);
            showColor(color);
            fireEvent(event);
        }
    }

    @Override
    public ColorData getData() {
        return colorData;
    }

    @Override
    public void setData(ColorData obj) {
        colorData = obj;
        toUI();
        showColor(colorData);
    }

    private void toUI() {
        double[] hsv = Colors.rgb2hsv(colorData.r(), colorData.g(), colorData.b());
        hueSelector.setData(hsv[0] * 360);
        transparentSelector.setColor(colorData, false);
        colorCanvas.setColor(colorData);
    }

    @Override
    public void onResize() {
        root.onResize();
    }

    interface ColorChooserUiBinder extends UiBinder<DockLayoutPanel, ColorChooser> {
    }
}
