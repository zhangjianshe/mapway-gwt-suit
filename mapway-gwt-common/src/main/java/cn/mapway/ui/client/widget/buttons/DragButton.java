package cn.mapway.ui.client.widget.buttons;


import cn.mapway.ui.client.fonts.Fonts;
import com.google.gwt.uibinder.client.UiConstructor;

/**
 * ResetButton
 *
 * @author zhangjianshe@gmail.com
 */
public class DragButton extends TextButton {
    @UiConstructor
    public DragButton(String text) {
        super(Fonts.MOVABLE, text);
        setEnabled(false);
    }
}
