package cn.mapway.common.geo.gdal;

import org.gdal.ogr.*;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.nutz.lang.*;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

/**
 * GeoPackageUtil
 * 处理GeoPackage
 * GDAL 关于多线程的信息
 * https://trac.osgeo.org/gdal/wiki/FAQMiscellaneous
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class GeoPackageUtil extends VectorUtil {

    public static void main(String[] args) {
        GdalUtil.init();
        GeoPackageUtil geoPackageUtil = new GeoPackageUtil("/data/personal/1/geopackage/example.gpkg");
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

    public GeoPackageUtil(String path) {
        super(path);
        try {
            source = getDriver().Open(fileName);
            if (source == null) {
                throw new IllegalStateException("Unable to open GeoPackage data source: " + fileName);
            }
            layer = source.GetLayer(0);
            if (layer == null) {
                throw new IllegalStateException("GeoPackage data source has no readable layer: " + fileName);
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
            throw new IllegalStateException("Unable to initialize GeoPackage data source: " + fileName, error);
        }
    }

    /**
     * @param fileName
     * @param geoType
     * @param epsgCode
     */
    public GeoPackageUtil(String fileName, Integer geoType, Integer epsgCode) {
        super(fileName, geoType, epsgCode);
        try {
            if (Files.isFile(new File(fileName))) {
                getDriver().DeleteDataSource(fileName);
            }
            sourceRef = new SpatialReference();
            sourceRef.ImportFromEPSG(epsgCode);
            source = getDriver().CreateDataSource(fileName);
            if (source == null) {
                throw new IllegalStateException("Unable to create GeoPackage data source: " + fileName);
            }
        } catch (RuntimeException error) {
            close();
            throw new IllegalStateException("Unable to initialize GeoPackage data source: " + fileName, error);
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
        Driver odriver = ogr.GetDriverByName("GPKG");
        if (odriver == null) {
            throw new IllegalStateException("GDAL GPKG driver is unavailable");
        }
        return odriver;
    }
}
