package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;


/**
 * DeleteButton
 *
 * @author zhangjianshe@gmail.com
 */
public class FileButton extends FontIcon {

    Object tag;

    public FileButton() {
        setIconUnicode(Fonts.FILE);
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

}
