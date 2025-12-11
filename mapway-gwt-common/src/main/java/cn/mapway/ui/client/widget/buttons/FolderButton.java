package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;


/**
 * DeleteButton
 *
 * @author zhangjianshe@gmail.com
 */
public class FolderButton extends FontIcon {

    Object tag;

    public FolderButton() {
        setIconUnicode(Fonts.FOLDER);
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

}
