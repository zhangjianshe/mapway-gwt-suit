package cn.mapway.common.geo.tools;

import cn.mapway.geo.geometry.GeoExtend;
import cn.mapway.geo.geometry.GeoPolygon;
import cn.mapway.geo.geometry.Line;
import cn.mapway.geo.geometry.Lines;
import org.nutz.lang.random.R;

import java.util.*;

/**
 * 地理相关的工具类
 */
public class GeoUtil {

    /**
     * 线性内插 y=x0+(x1-x0)*t
     *
     * @param x0
     * @param x1
     * @param t  [0,1]
     * @return
     */
    public static double slerp(double x0, double x1, double t) {
        return x0 + (x1 - x0) * t;
    }

    /**
     * 在 extend范围内 随机产生一个多边形
     *
     * @param extend
     * @return
     */
    public static GeoPolygon randomPolygon(GeoExtend extend) {
        Random random = R.get();
        double centerX = slerp(extend.minLng, extend.maxLng, random.nextDouble());
        double centerY = slerp(extend.minLat, extend.maxLat, random.nextDouble());

        int count = R.random(3, 8);

        List<Double> angles = new ArrayList<Double>();
        List<Double> radius = new ArrayList<Double>();

        for (int i = 0; i < count; i++) {
            angles.add(random.nextDouble());
            radius.add(slerp(extend.width() / 3, extend.width(), random.nextDouble()));
        }
        Collections.sort(angles, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        });

        //根据角度和半径 构造多白那些顶点

        GeoPolygon geoPolygon = new GeoPolygon();
        Lines lines = geoPolygon.getLines();
        Line line = new Line();
        lines.add(line);
        for (int i = 0; i < angles.size(); i++) {
            double angle = slerp(0, Math.PI * 2, angles.get(i));
            double radiu = radius.get(i);

            double x = centerX + radiu * Math.cos(angle);
            double y = centerY + radiu * Math.sin(angle);
            line.add(x, y);
        }
        line.close();

        return geoPolygon;
    }

    public static void main(String[] args) {
        GeoExtend geoExtend=new GeoExtend();
        geoExtend.set(10.0,20.0,20.0,10.0);
        GeoPolygon geoPolygon = GeoUtil.randomPolygon(geoExtend);
        System.out.println(geoPolygon.toGeoJson());

    }
}
