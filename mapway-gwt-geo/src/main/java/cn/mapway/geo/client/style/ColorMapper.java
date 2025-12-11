package cn.mapway.geo.client.style;

import jsinterop.annotations.JsType;

import java.io.Serializable;

@JsType
public class ColorMapper implements Serializable {
    public double start;
    public double end;
    public String name;
    public BorderStyle bs;
    public FillStyle fs;
}
