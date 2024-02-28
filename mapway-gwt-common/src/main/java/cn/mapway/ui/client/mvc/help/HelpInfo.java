package cn.mapway.ui.client.mvc.help;


import com.google.gwt.user.client.ui.Widget;

/**
 * HelpInfo
 * 帮助信息
 *
 * @author zhang
 */
public class HelpInfo {
    public static final int HELP_TYPE_NODE = 0;
    public static final int HELP_TYPE_HTML = 1;
    public static final int HELP_TYPE_PAGE_URL = 2;
    public static final int HELP_TYPE_WIDGET = 3;
    public static final int HELP_TYPE_IMAGE_URL = 4;
    public int type;
    public String title;
    public String url;
    public String html;
    public Widget widget;

    public final static HelpInfo createPageUrl(String title, String url) {
        HelpInfo info = new HelpInfo();
        info.type = HELP_TYPE_PAGE_URL;
        info.title = title;
        info.url = url;
        return info;
    }

    public final static HelpInfo createImageUrl(String title, String url) {
        HelpInfo info = new HelpInfo();
        info.type = HELP_TYPE_IMAGE_URL;
        info.title = title;
        info.url = url;
        return info;
    }

    public final static HelpInfo createHtml(String title, String html) {
        HelpInfo info = new HelpInfo();
        info.type = HELP_TYPE_HTML;
        info.title = title;
        info.html = html;
        return info;
    }

    public final static HelpInfo createWidget(String title, Widget widget) {
        HelpInfo info = new HelpInfo();
        info.type = HELP_TYPE_WIDGET;
        info.title = title;
        info.widget = widget;
        return info;
    }
}
