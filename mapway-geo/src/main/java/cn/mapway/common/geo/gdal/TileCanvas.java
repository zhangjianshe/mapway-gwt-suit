package cn.mapway.common.geo.gdal;


import cn.mapway.common.geo.sfile.TileNo;
import cn.mapway.geo.client.style.BorderStyle;
import cn.mapway.geo.client.style.FillStyle;
import cn.mapway.geo.client.style.MapStyle;
import cn.mapway.geo.client.style.StyleLayer;
import cn.mapway.geo.shared.vector.*;
import lombok.extern.slf4j.Slf4j;
import org.nutz.img.Colors;
import org.nutz.img.Images;

import java.awt.Polygon;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

/**
 * TileCanvas
 *
 * @author zhang
 */
@Slf4j
public class TileCanvas {
    TileNo tileNo;
    BufferedImage buffer;
    Graphics2D graphics;
    Box mercatorBox;
    double res;

    public TileCanvas(long x, long y, int zoom) {
        tileNo = new TileNo(x, y, zoom);
        mercatorBox = WebMercator.tileBoundMercator(x, y, zoom);
        res = WebMercator.resolution(tileNo.getZoom());
        buffer = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR);
        graphics = buffer.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private static String toHex(double opacity) {
        if (opacity <= 0) {
            return "00";
        }
        if (opacity >= 1.0) {
            return "FF";
        }
        int a = (int) (opacity * 256);
        return String.format("%H", a);
    }

    public Box getMercatorBox() {
        return mercatorBox;
    }

    public void writeToOutputStream(OutputStream out) {
        Images.write(buffer, "png", out);
    }



    private Polygon toPolygon(Line line) {
        Polygon p = new Polygon();
        for (int nc = 0; nc < line.getCount(); nc++) {
            cn.mapway.geo.shared.vector.Point pt = line.getPoint(nc);
            if (pt != null) {
                //不能改变原来的值
                cn.mapway.geo.shared.vector.Point pt1 = pt.clone();
                transform(pt1);
                p.addPoint((int) pt1.x, (int) pt1.y);
            }
        }
        return p;
    }

    Color colorFromString(double opacity, String color) {

        //color #RRGGBB  转换为 #AARRGGBB
        String opacityHex = toHex(opacity);
        String tempColor = "#" + opacityHex + color.substring(1);
        return Colors.as(tempColor);
    }

    /**
     * 墨卡托坐标 专为 tile的像素坐标
     *
     * @param pt
     */
    private void transform(cn.mapway.geo.shared.vector.Point pt) {
        pt.x = (pt.x - mercatorBox.getXmin()) / res;
        pt.y = 256-(pt.y- mercatorBox.getYmin()) / res;

//        double res = WebMercator.resolution(tileNo.getZoom());
//        pt.x = (pt.x + WebMercator.originShift) / res - tileNo.getTileX() * 256;
//        pt.y = (WebMercator.originShift - pt.y) / res - tileNo.getTileY() * 256;
    }

    public void drawFeature(Feature featureDraw, MapStyle mapStyle) {
        StyleLayer styleLayer = mapStyle.getStyles().get(0);
        BorderStyle borderStyle = styleLayer.getBorderStyle();
        FillStyle fillStyle = styleLayer.getFillStyle();
        Color fillColor = colorFromString(fillStyle.getOpacity(), fillStyle.getColor());
        Color borderColor = colorFromString(borderStyle.getOpacity(), borderStyle.getColor());
        Stroke stroke = null;
        if (borderStyle.getWidth() > 0) {
            stroke = new BasicStroke(borderStyle.getWidth());
        }

        GeoObject geometry = featureDraw.getGeometry();
        if (geometry instanceof Lines) {
            Lines lines = (Lines) geometry;
            if (lines.getCount() == 0) {
                return;
            }
            Area geoobj = null;
            //画多边形
            if (lines.getCount() == 1) {
                Polygon polygon = toPolygon(lines.getLine(0));
                //先填充
                if (fillStyle.getOpacity() > 0) {
                    graphics.setColor(fillColor);
                    graphics.fillPolygon(polygon);
                }
                //再画边框
                if (borderStyle.getWidth() > 0) {
                    graphics.setColor(borderColor);
                    graphics.setStroke(stroke);
                    graphics.drawPolygon(polygon);
                }
            } else {
                if (lines.getCount() > 30) {
                    log.warn("drawFeature lines.getCount()>30");
                    log.warn("多变嵌套太多,不支持");
                } else {
                    for (int i = 0; i < lines.getCount(); i++) {
                        if (i == 0) {
                            Polygon polygon = toPolygon(lines.getLine(0));
                            geoobj = new Area(polygon);
                        } else {
                            Polygon polygon = toPolygon(lines.getLine(i));
                            geoobj.subtract(new Area(polygon));
                        }
                    }
                    //先填充
                    if (fillStyle.getOpacity() > 0) {
                        graphics.setColor(fillColor);
                        graphics.fill(geoobj);
                    }
                    //再画边框
                    if (borderStyle.getWidth() > 0) {
                        graphics.setColor(borderColor);
                        graphics.setStroke(stroke);
                        graphics.draw(geoobj);
                    }
                }
            }
        }
        else if(geometry instanceof Line){
            Line line = (Line) geometry;
            if(line.getCount()>2)
            {
                graphics.setColor(borderColor);
                graphics.setStroke(stroke);
                cn.mapway.geo.shared.vector.Point point = line.getPoint(0).clone();
                transform(point);
                for(int index=1;index<line.getCount();index++)
                {
                    cn.mapway.geo.shared.vector.Point end=line.getPoint(index).clone();
                    transform(end);
                    graphics.drawLine((int) point.x, (int) point.y, (int) end.x, (int) end.y);
                    point.copyFrom(end);
                }
            }
        }else if(geometry instanceof cn.mapway.geo.shared.vector.Point)
        {
            cn.mapway.geo.shared.vector.Point pt = (cn.mapway.geo.shared.vector.Point) geometry;
            transform(pt);
            if (pt.getX() > 0) {
                graphics.setColor(fillColor);
                graphics.fillArc((int) (pt.x-5), (int) (pt.y-5),10,10,0,360);
            }
        }

    }
}
