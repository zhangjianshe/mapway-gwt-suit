package cn.mapway.geo.shared.stretch;


import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;
import lombok.Data;

import java.io.Serializable;

@Data
@JsType
public class StretchParam implements Serializable, IsSerializable {

    protected Double min;

    protected Double max;

    protected Double minPct;

    protected Double maxPct;

}
