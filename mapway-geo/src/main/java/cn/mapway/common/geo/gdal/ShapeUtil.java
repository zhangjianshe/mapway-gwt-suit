package cn.mapway.common.geo.gdal;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ShapeUtil extends VectorUtil {

    public ShapeUtil(String path) {
        super(path);
        try {
            source = getDriver().Open(fileName);
            layer = source.GetLayer(0);
            sourceRef = layer.GetSpatialRef();
            if (sourceRef == null) {
                //猜测
                sourceRef = srcWgs84;
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
        } catch (Exception e) {
            log.error("Open file {} {}", fileName, e.getMessage());
        }
    }

    /**
     * @param fileName
     * @param geoType
     * @param epsgCode
     */
    public ShapeUtil(String fileName, Integer geoType, Integer epsgCode) {
        super(fileName,geoType,epsgCode);
        try {
            if (Files.isFile(new File(fileName))) {
                getDriver().DeleteDataSource(fileName);
            }
            SpatialReference spatialReference = new SpatialReference();
            spatialReference.ImportFromEPSG(epsgCode);
            source = getDriver().CreateDataSource(fileName);
            Vector<String> options1 = new Vector<>();
            options1.add("ENCODING=UTF-8");
            layer = source.CreateLayer("Layer", spatialReference, geoType, options1);
        } catch (Exception e) {
            log.error("Open file {} {}", fileName, e.getMessage());
        }
    }


    protected Driver getDriver() {
        Driver odriver = ogr.GetDriverByName("ESRI Shapefile");
        return odriver;
    }
}
