package cn.mapway.ui.shared;

import lombok.Data;

/**
 * 组件和外包装之间的 消息传递数据
 */
@Data
public class WindowTitleData {
    public String caption;
    public String unicode;
    public String url;
    public boolean closeable;
    public boolean draggable;

    public WindowTitleData() {
        caption = "";
        unicode = "";
        url = "";
        closeable = true;
        draggable = true;
    }

    public static WindowTitleData create(String title, String unicode) {
        WindowTitleData data = new WindowTitleData();
        data.setCaption(title);
        data.setUnicode(unicode);
        return data;
    }
}
