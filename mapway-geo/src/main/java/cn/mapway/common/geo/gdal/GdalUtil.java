package cn.mapway.common.geo.gdal;

import cn.mapway.geo.shared.vector.Point;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.gdal;
import org.gdal.gdal.gdalJNI;

import java.util.List;

/**
 * Gdal工具类
 */
@Slf4j
public class GdalUtil {

    public GdalUtil() {
    }

    public static void init() {
        if (gdalJNI.isAvailable()) {
            //注册所有的驱动
            gdal.AllRegister();
           // gdal.SetConfigOption("SHAPE_RESTORE_SHX", "YES");
            gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
            gdal.SetConfigOption("SHAPE_ENCODING", "");
            log.info("GDAL Java 工具初始化");
        } else {
            log.warn("本地GDAL动态链接库不存在");
        }
    }


    /**
     * 根据传入的坐标计算所在的行列号
     * 若geoTransform不是长度为6位的数组会报错
     * @param y   109580123.7496164E7
     * @param x
     * @param geoTransform
     * @return
     */
    public static double[] calcPointByLonAndlat(double x, double y, double[] geoTransform){
        double dTemp = geoTransform[1] * geoTransform[5] - geoTransform[2] *geoTransform[4];
        double xPixel= (geoTransform[5] * (x - geoTransform[0]) - geoTransform[2] * (x - geoTransform[3])) / dTemp;
        double yPixel = (geoTransform[1] * (y - geoTransform[3]) - geoTransform[4] * (y - geoTransform[0])) / dTemp;

        double[] result = new double[2];
        result[0] = xPixel;
        result[1] = yPixel;
        return result;
    }

    /**
     * 根据传入的坐标计算所在的行列号
     * 若geoTransform不是长度为6位的list会报错
     * @param y
     * @param x
     * @param geoTransform
     * @return
     */
    public static double[] calcPointByLonAndlat(double x, double y, List<Double> geoTransform){
        double[] doubles = transformDoubleArrays(geoTransform);
        return calcPointByLonAndlat(x, y , doubles);
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(int x, int y, double[] geoTransform){
        double px = geoTransform[0] + x * geoTransform[1] + y * geoTransform[2];
        double py = geoTransform[3] + x * geoTransform[4] + y * geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     * @param y 行号
     * @param x 列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(int x, int y, Double[] geoTransform){
        double px = geoTransform[0]+x*geoTransform[1]+y*geoTransform[2];
        double py = geoTransform[3]+x*geoTransform[4]+y*geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     * @param y 行号
     * @param x 列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(double x, double y, Double[] geoTransform){
        double px = geoTransform[0]+x*geoTransform[1]+y*geoTransform[2];
        double py = geoTransform[3]+x*geoTransform[4]+y*geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }


    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的list会报错
     * @param i 行号
     * @param j 列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(int i, int j, List<Double> geoTransform){
        double[] doubles = transformDoubleArrays(geoTransform);;
        return calcPointByRowAndCol(i , j , doubles);
    }

    private static double[] transformDoubleArrays(List<Double> geoTransform){
        double[] geoTransformArray = new double[6];
        geoTransformArray[0] = geoTransform.get(0);
        geoTransformArray[1] = geoTransform.get(1);
        geoTransformArray[2] = geoTransform.get(2);
        geoTransformArray[3] = geoTransform.get(3);
        geoTransformArray[4] = geoTransform.get(4);
        geoTransformArray[5] = geoTransform.get(5);
        return geoTransformArray;
    }
}
