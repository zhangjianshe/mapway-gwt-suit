package cn.mapway.geo.client.raster;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * @Author baoshuaiZealot@163.com  2023/12/21
 */
public class MetadataResult implements Serializable, IsSerializable {
    public String key;
    public String value;

    public MetadataResult(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public MetadataResult() {
    }
}
