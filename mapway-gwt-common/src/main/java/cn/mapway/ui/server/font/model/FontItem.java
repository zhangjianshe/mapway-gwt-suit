package cn.mapway.ui.server.font.model;

import lombok.Data;

import java.io.Serializable;

/**
 * FontItem
 * from iconfont.cn output data format
 * "icon_id": "29432647",
 * "name": "double-left",
 * "font_class": "double-left",
 * "unicode": "e608",
 * "unicode_decimal": 58888
 *
 * @author zhang
 */
@Data
public class FontItem implements Serializable {
    public String icon_id;
    public String name;
    public String font_class;
    public String unicode;
    public Integer unicode_decimal;

    public String toName() {
        return font_class.toUpperCase().replaceAll("-", "_").replaceAll("\\s", "_");
    }
}
