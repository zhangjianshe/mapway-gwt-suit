package cn.mapway.ui.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 系统资源
 */
public interface MapwayResource extends ClientBundle {
    MapwayResource INSTANCE = GWT.create(MapwayResource.class);

    @Source({"image/default.png"})
    ImageResource defaultImage();

    @Source({"image/return1.png"})
    ImageResource return1();

    @Source({"image/return2.png"})
    ImageResource return2();

    @Source("image/hueSaturation.png")
    ImageResource hueSaturation();

    @Source("image/lightness.png")
    ImageResource lightness();

    @Source("image/upload.png")
    ImageResource upload();

    @Source("image/module.png")
    ImageResource module();

    @Source("image/loading.gif")
    ImageResource loading();
}
