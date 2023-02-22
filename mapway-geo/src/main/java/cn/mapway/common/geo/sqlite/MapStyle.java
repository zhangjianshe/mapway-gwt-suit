package cn.mapway.common.geo.sqlite;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * MapStyle
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
@Data
public class MapStyle {
    public Font font;
    boolean fill;
    Color borderColor;
    Color fillColor;
    boolean showName;
    Color fontColor;

    public MapStyle() {
        fill = false;
        borderColor = Color.GRAY;
        fillColor = Color.WHITE;
        showName = false;
        fontColor = Color.BLACK;
    }

    public MapStyle(Boolean fill, Color borderColor, Color fillColor, boolean showName) {
        this.fill = fill;
        this.borderColor = borderColor;
        this.fillColor = fillColor;
        this.showName = showName;
    }

}
