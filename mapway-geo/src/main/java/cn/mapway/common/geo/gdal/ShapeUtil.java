package cn.mapway.common.geo.gdal;

import org.gdal.ogr.Driver;
import org.gdal.ogr.GeomTransformer;
import org.gdal.ogr.ogr;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.nutz.lang.Files;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import static org.gdal.ogr.ogrConstants.wkbMultiPolygon;

/**
 * ShapeUtil
 * 处理ShapeFile
 * GDAL 关于多线程的信息
 * https://trac.osgeo.org/gdal/wiki/FAQMiscellaneous
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class ShapeUtil extends VectorUtil {

    public ShapeUtil(String path) {
        super(path);
        try {
            source = getDriver().Open(fileName);
            if (source == null) {
                throw new IllegalStateException("Unable to open SHP data source: " + fileName);
            }
            layer = source.GetLayer(0);
            if (layer == null) {
                throw new IllegalStateException("SHP data source has no readable layer: " + fileName);
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
            String tempEncoding = "";
            Hashtable hashtable = layer.GetMetadata_Dict("SHAPEFILE");
            if (hashtable != null) {
                tempEncoding = (String) hashtable.get("ENCODING_FROM_LDID");
                if (tempEncoding == null || tempEncoding.isEmpty()) {
                    tempEncoding = (String) hashtable.get("ENCODING_FROM_CPG");
                }
            }

            if (tempEncoding == null || tempEncoding.isEmpty()) {
                tempEncoding = "UTF-8";
            }
            if (tempEncoding.compareToIgnoreCase("cp936") == 0) {
                tempEncoding = "GBK";
            }
            setEncoding(tempEncoding);
        } catch (RuntimeException error) {
            close();
            throw new IllegalStateException("Unable to initialize SHP data source: " + fileName, error);
        }
    }

    /**
     * @param fileName
     * @param geoType
     * @param epsgCode
     */
    public ShapeUtil(String fileName, Integer geoType, Integer epsgCode) {
        super(fileName, geoType, epsgCode);
        SpatialReference spatialReference = null;
        boolean completed = false;
        try {
            if (Files.isFile(new File(fileName))) {
                getDriver().DeleteDataSource(fileName);
            }
            spatialReference = new SpatialReference();
            spatialReference.ImportFromEPSG(this.epsgCode);
            source = getDriver().CreateDataSource(fileName);
            if (source == null) {
                throw new IllegalStateException("Unable to create SHP data source: " + fileName);
            }
            Vector<String> options1 = new Vector<>();
            options1.add("ENCODING=UTF-8");
            options1.add("SHAPE_RESTORE_SHX=YES");
            layer = source.CreateLayer("Layer", spatialReference, this.geoType, options1);
            if (layer == null) {
                throw new IllegalStateException("Unable to create SHP layer: " + fileName);
            }
            completed = true;
        } catch (RuntimeException error) {
            throw new IllegalStateException("Unable to initialize SHP data source: " + fileName, error);
        } finally {
            if (spatialReference != null) {
                spatialReference.delete();
                spatialReference = null;
            }
            if (!completed) {
                close();
            }
        }
    }


    protected Driver getDriver() {
        Driver odriver = ogr.GetDriverByName("ESRI Shapefile");
        if (odriver == null) {
            throw new IllegalStateException("GDAL ESRI Shapefile driver is unavailable");
        }
        return odriver;
    }

}
