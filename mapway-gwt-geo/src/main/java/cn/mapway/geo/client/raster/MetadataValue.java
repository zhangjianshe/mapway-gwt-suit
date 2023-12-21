package cn.mapway.geo.client.raster;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author baoshuaiZealot@163.com  2023/12/21
 */
@Data
public class MetadataValue implements Serializable, IsSerializable {

    private String key;

    private String name;

    public MetadataValue(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public MetadataValue() {
    }

}
