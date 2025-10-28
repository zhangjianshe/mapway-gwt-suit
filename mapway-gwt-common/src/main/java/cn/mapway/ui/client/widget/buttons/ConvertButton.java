package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import com.google.gwt.dom.client.Style;

/**
 * @Author baoshuaiZealot@163.com  2023/4/21
 */
public class ConvertButton  extends FontIcon {
    public ConvertButton() {
        setIconUnicode(Fonts.SORT1);
        Style style = getElement().getStyle();
        style.setProperty("transform","rotate(-90deg)");
    }
}
