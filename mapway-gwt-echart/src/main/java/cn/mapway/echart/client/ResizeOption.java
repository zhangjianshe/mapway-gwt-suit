package cn.mapway.echart.client;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * ResizeOption
 *
 * @author zhang
 */
@JsType
public class ResizeOption {
    @JsProperty
    public int width;
    @JsProperty
    public int height;
    @JsProperty
    public boolean silent;

    public final static ResizeOption create(int width, int height)
    {
        ResizeOption option = new ResizeOption();
        option.width = width;
        option.height = height;
        option.silent = false;
        return option;
    }
}
