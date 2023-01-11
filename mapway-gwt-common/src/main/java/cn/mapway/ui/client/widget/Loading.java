package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.resource.MapwayResource;
import com.google.gwt.user.client.ui.Image;

/**
 * Loading
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class Loading extends Image {
    public Loading() {
        super();
        setResource(MapwayResource.INSTANCE.loading());
    }
}
