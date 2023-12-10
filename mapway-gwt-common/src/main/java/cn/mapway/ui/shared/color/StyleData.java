package cn.mapway.ui.shared.color;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;

/**
 * StyleData
 *
 * @author zhang
 */
@Data
public class StyleData implements Serializable, IsSerializable {
    private FillData fillData;

    public StyleData() {
        fillData = new FillData();
    }
}
