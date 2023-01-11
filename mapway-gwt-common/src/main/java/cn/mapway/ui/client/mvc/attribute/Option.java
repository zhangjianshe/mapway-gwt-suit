package cn.mapway.ui.client.mvc.attribute;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * Option
 * 选择项
 *
 * @author zhang
 */
@Data
public class Option implements Serializable, IsSerializable {
    String text;
    String value;
    String icon;
    boolean initSelected = false;

    public Option() {
    }

    public Option(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public Option(String text, String value, String icon) {
        this.text = text;
        this.value = value;
        this.icon = icon;
    }

    public Option setSelected(boolean selected) {
        initSelected = selected;
        return this;
    }
}
