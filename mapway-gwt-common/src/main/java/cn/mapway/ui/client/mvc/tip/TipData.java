package cn.mapway.ui.client.mvc.tip;


import com.google.gwt.user.client.ui.Widget;
import lombok.Data;

/**
 * TipData
 *
 * @author zhang
 */
@Data
public class TipData {
    public static final int TIP_TYPE_HTML = 0;
    public static final int TIP_TYPE_WIDGET = 1;
    public static final int TIP_TYPE_URL = 2;
    Widget target;
    String html;
    String header;
    String url;
    Widget content;
    int type;

    public TipData(Widget target, String header) {
        this.target = target;
        this.header = header;
    }

    public TipData setHtml(String html) {
        type = TIP_TYPE_HTML;
        this.html = html;
        return this;
    }

    public TipData setUrl(String url) {
        type = TIP_TYPE_URL;
        this.url = url;
        return this;
    }

    public TipData setWidget(Widget content) {
        type = TIP_TYPE_WIDGET;
        this.content = content;
        return this;
    }
}
