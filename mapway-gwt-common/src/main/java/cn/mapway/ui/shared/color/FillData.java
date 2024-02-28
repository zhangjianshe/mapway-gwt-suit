package cn.mapway.ui.shared.color;

import cn.mapway.ui.client.widget.color.ColorData;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * FillData
 *
 * @author zhang
 */
@Data
public class FillData implements Serializable, IsSerializable {
    private List<ColorData> fills;

    public FillData() {
        fills = new ArrayList<ColorData>();
    }

    public void clear() {
        fills.clear();
    }

    public String toFillString() {
        StringBuilder sb = new StringBuilder();
        for (ColorData fill : fills) {
            if (fill.show) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(fill.toRGBA());
            }
        }
        return sb.toString();
    }
}
