package cn.mapway.common.geo.gdal;

import cn.mapway.geo.shared.vector.Point;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.gdal;
import org.gdal.gdal.gdalJNI;
import org.gdal.gdalconst.gdalconstJNI;
import org.nutz.lang.Files;

import java.util.List;

/**
 * Gdal工具类
 */
@Slf4j
public class GdalUtil {

    public static void main(String[] args) {
        GdalUtil.init();
        int GDT_Unknown = gdalconstJNI.GDT_Unknown_get();
        int GDT_Byte = gdalconstJNI.GDT_Byte_get();
        int GDT_UInt16 = gdalconstJNI.GDT_UInt16_get();
        int GDT_Int16 = gdalconstJNI.GDT_Int16_get();
        int GDT_UInt32 = gdalconstJNI.GDT_UInt32_get();
        int GDT_Int32 = gdalconstJNI.GDT_Int32_get();
        int GDT_Float32 = gdalconstJNI.GDT_Float32_get();
        int GDT_Float64 = gdalconstJNI.GDT_Float64_get();
        int GDT_CInt16 = gdalconstJNI.GDT_CInt16_get();
        int GDT_CInt32 = gdalconstJNI.GDT_CInt32_get();
        int GDT_CFloat32 = gdalconstJNI.GDT_CFloat32_get();
        int GDT_CFloat64 = gdalconstJNI.GDT_CFloat64_get();

        System.out.println("GDT_Byte:"+ GDT_Byte);
        System.out.println("GDT_UInt16:"+ GDT_UInt16);
        System.out.println("GDT_Int16:"+ GDT_Int16);
        System.out.println("GDT_UInt32:"+ GDT_UInt32);
        System.out.println("GDT_Int32:"+ GDT_Int32);
        System.out.println("GDT_Float32:"+ GDT_Float32);
        System.out.println("GDT_Float64:"+ GDT_Float64);
        System.out.println("GDT_CInt16:"+ GDT_CInt16);
        System.out.println("GDT_CInt32:"+ GDT_CInt32);
        System.out.println("GDT_CFloat32:"+ GDT_CFloat32);
        System.out.println("GDT_CFloat64:"+ GDT_CFloat64);
        System.out.println("GDT_Unknown:"+ GDT_Unknown);

    }
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
     * 设置全局 PAM 数据  Persistent Auxiliary  MetaData
     *
     * @param enable
     * @param pamDirectory
     */
    public static void setPAM(boolean enable, String pamDirectory) {
        if (enable) {
            System.setProperty("GDAL_PAM_ENABLED", "TRUE");
        } else {
            System.setProperty("GDAL_PAM_ENABLED", "FALSE");
        }

        if (pamDirectory != null && pamDirectory.length() > 0) {
            Files.createDirIfNoExists(pamDirectory);//检查目录
            System.setProperty("GDAL_PAM_PROXY_DIR", pamDirectory);
        }
    }

    public static void enablePam(String pamDirectory)
    {
        setPAM(true,pamDirectory);
    }

    /**
     * 根据传入的坐标计算所在的行列号
     * 若geoTransform不是长度为6位的数组会报错
     *
     * @param y            109580123.7496164E7
     * @param x
     * @param geoTransform
     * @return
     */
    public static double[] calcPointByLonAndlat(double x, double y, double[] geoTransform) {
        double dTemp = geoTransform[1] * geoTransform[5] - geoTransform[2] * geoTransform[4];
        double xPixel = (geoTransform[5] * (x - geoTransform[0]) - geoTransform[2] * (x - geoTransform[3])) / dTemp;
        double yPixel = (geoTransform[1] * (y - geoTransform[3]) - geoTransform[4] * (y - geoTransform[0])) / dTemp;

        double[] result = new double[2];
        result[0] = xPixel;
        result[1] = yPixel;
        return result;
    }

    /**
     * 根据传入的坐标计算所在的行列号
     * 若geoTransform不是长度为6位的list会报错
     *
     * @param y
     * @param x
     * @param geoTransform
     * @return
     */
    public static double[] calcPointByLonAndlat(double x, double y, List<Double> geoTransform) {
        double[] doubles = transformDoubleArrays(geoTransform);
        return calcPointByLonAndlat(x, y, doubles);
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     *
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(int x, int y, double[] geoTransform) {
        double px = geoTransform[0] + x * geoTransform[1] + y * geoTransform[2];
        double py = geoTransform[3] + x * geoTransform[4] + y * geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     *
     * @param y            行号
     * @param x            列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(int x, int y, Double[] geoTransform) {
        double px = geoTransform[0] + x * geoTransform[1] + y * geoTransform[2];
        double py = geoTransform[3] + x * geoTransform[4] + y * geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     *
     * @param y            行号
     * @param x            列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(double x, double y, Double[] geoTransform) {
        double px = geoTransform[0] + x * geoTransform[1] + y * geoTransform[2];
        double py = geoTransform[3] + x * geoTransform[4] + y * geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }

    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的数组会报错
     *
     * @param y            行号
     * @param x            列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(double x, double y, double[] geoTransform) {
        double px = geoTransform[0] + x * geoTransform[1] + y * geoTransform[2];
        double py = geoTransform[3] + x * geoTransform[4] + y * geoTransform[5];
        Point coordinate = new Point(px, py);
        return coordinate;
    }


    /**
     * 根据行列号计算经纬度
     * 若geoTransform不是长度为6位的list会报错
     *
     * @param i            行号
     * @param j            列号
     * @param geoTransform
     * @return
     */
    public static Point calcPointByRowAndCol(int i, int j, List<Double> geoTransform) {
        double[] doubles = transformDoubleArrays(geoTransform);
        return calcPointByRowAndCol(i, j, doubles);
    }

    private static double[] transformDoubleArrays(List<Double> geoTransform) {
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
