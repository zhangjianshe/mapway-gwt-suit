package cn.mapway.common.geo.gdal;

import org.gdal.ogr.*;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.nutz.lang.*;

import java.io.File;

/**
 * FileGdbUtil
 * 处理 ArcGIS OpenFileGDB
 * GDAL 关于多线程的信息
 * https://gdal.org/drivers/vector/filegdb.html
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class OpenFileGdbUtil extends VectorUtil {

    public static void main(String[] args) {
        GdalUtil.init();
        OpenFileGdbUtil geoPackageUtil = new OpenFileGdbUtil("/data/personal/1/geopackage/test.gdb");
        System.out.println("feature count "+ geoPackageUtil.count());
        geoPackageUtil.each(new Each<Feature>() {
            @Override
            public void invoke(int index, Feature ele, int length) throws ExitLoop, ContinueLoop, LoopException {
                System.out.println(ele.GetFID() + " " + ele.GetFieldAsString(1));
            }
        });
        Feature feature = geoPackageUtil.getFeature(1);
        System.out.println(feature.GetFID());

    }

    public OpenFileGdbUtil(String path) {
        super(path);
        try {
            source = getDriver().Open(fileName);
            if (source == null) {
                throw new IllegalStateException("Unable to open OpenFileGDB data source: " + fileName);
            }
            layer = source.GetLayer(0);
            if (layer == null) {
                throw new IllegalStateException("OpenFileGDB data source has no readable layer: " + fileName);
            }
            sourceRef = layer.GetSpatialRef();
            if (sourceRef == null) {
                //猜测
                sourceRef = srcWgs84.Clone();
            }
            if (sourceRef.IsSame(srcWebMercator) != 0) {
                coordinateTransformation = new CoordinateTransformation(sourceRef, srcWebMercator);
                geomTransformer = new GeomTransformer(coordinateTransformation);
            } else {
                geomTransformer = null;
            }
        } catch (RuntimeException error) {
            close();
            throw new IllegalStateException("Unable to initialize OpenFileGDB data source: " + fileName, error);
        }
    }

    /**
     * @param fileName
     * @param geoType
     * @param epsgCode
     */
    public OpenFileGdbUtil(String fileName, Integer geoType, Integer epsgCode) {
        super(fileName, geoType, epsgCode);
        try {
            if (Files.isFile(new File(fileName))) {
                getDriver().DeleteDataSource(fileName);
            }
            sourceRef = new SpatialReference();
            sourceRef.ImportFromEPSG(epsgCode);
            source = getDriver().CreateDataSource(fileName);
            if (source == null) {
                throw new IllegalStateException("Unable to create OpenFileGDB data source: " + fileName);
            }
        } catch (RuntimeException error) {
            close();
            throw new IllegalStateException("Unable to initialize OpenFileGDB data source: " + fileName, error);
        }
    }

    public void createLayer(String layerName)
    {
        layer = source.CreateLayer(layerName,sourceRef , geoType, null);
    }

    public Layer  createNormalTable(String tableName){
       return source.CreateLayer(tableName);
    }

    protected Driver getDriver() {
        Driver odriver = ogr.GetDriverByName("OpenFileGDB");
        if (odriver == null) {
            throw new IllegalStateException("GDAL OpenFileGDB driver is unavailable");
        }
        return odriver;
    }
}
