package cn.mapway.ui.server.font.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * FontFile
 * "id": "3373006",
 * "name": "ib-font",
 * "font_family": "ib-font",
 * "css_prefix_text": "icon-",
 * "description": "imagebot font collections",
 * "glyphs": [
 *
 * @author zhang
 */
@Data
public class FontFile implements Serializable {
    public String id;
    public String name;
    public String font_family;
    public String css_prefix_text;
    public String description;
    public List<FontItem> glyphs;
}
