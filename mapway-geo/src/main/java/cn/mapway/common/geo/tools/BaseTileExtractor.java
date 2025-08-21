package cn.mapway.common.geo.tools;


import cn.mapway.common.geo.gdal.GdalUtil;
import cn.mapway.geo.client.raster.BandInfo;
import cn.mapway.geo.client.raster.ChanelData;
import cn.mapway.geo.client.raster.ImageInfo;
import cn.mapway.geo.shared.color.ColorMap;
import cn.mapway.geo.shared.color.ColorTable;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.util.Colors;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.gdalconst.gdalconstJNI;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.gdal.gdalconst.gdalconstConstants.*;
import static org.gdal.osr.osrConstants.OAMS_TRADITIONAL_GIS_ORDER;

/**
 * BaseTileExtractor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class BaseTileExtractor {

    private final int defaultColor = 0x808080FF;
    private byte[] blackBuffer;
    private ColorTable colorTable;

    /**
     * 根据仿射变换 将经纬度坐标转换到 影像的像素坐标空间
     * 利用下面的公式进行逆变换求解 已知(XP YP ) 求解  (P L)
     * * Xp = padfTransform[0] + P*padfTransform[1] + L*padfTransform[2];
     * * Yp = padfTransform[3] + P*padfTransform[4] + L*padfTransform[5];
     * <p>
     * <p>
     * L=(a1*a3-a4*a0+a4*Xp-a1*Yp)/(a2*a4-a1*a5)
     * P=-(a2*a3-a5*a0+a5*Xp-a2*Yp)/(a2*a4-a1*a5)
     *
     * @param adfGeoTransform
     * @param pt
     * @return
     */
    public static Point imageSpaceToSourceSpace(double[] adfGeoTransform, Point pt) {
        double a1a3 = adfGeoTransform[1] * adfGeoTransform[3];
        double a4a0 = adfGeoTransform[4] * adfGeoTransform[0];
        double a2a3 = adfGeoTransform[2] * adfGeoTransform[3];
        double a5a0 = adfGeoTransform[5] * adfGeoTransform[0];
        double a2a4 = adfGeoTransform[2] * adfGeoTransform[4];
        double a1a5 = adfGeoTransform[1] * adfGeoTransform[5];
        // TL
        double L = (a1a3 - a4a0 + adfGeoTransform[4] * pt.getX() - adfGeoTransform[1] * pt.getY()) / (a2a4 - a1a5);
        double P = -(a2a3 - a5a0 + adfGeoTransform[5] * pt.getX() - adfGeoTransform[2] * pt.getY()) / (a2a4 - a1a5);
        return new Point(P, L);
    }

    /**
     * 根据仿射变换 将像素空间 转化为影像所在参考系下的坐标
     * 利用 放射变化6参数进行变换
     * Xgeo = GT(0) + Xpixel*GT(1) + Yline*GT(2)
     * Ygeo = GT(3) + Xpixel*GT(4) + Yline*GT(5)
     *
     * @param adfGeoTransform
     * @param pt
     * @return
     */
    public static Point rasterSpaceToImageSpace(double[] adfGeoTransform, Point pt) {
        double Xgeo0 = adfGeoTransform[0] + pt.x * adfGeoTransform[1] + pt.y * adfGeoTransform[2];
        double Ygeo0 = adfGeoTransform[3] + pt.x * adfGeoTransform[4] + pt.y * adfGeoTransform[5];
        return new Point(Xgeo0, Ygeo0);
    }

    public static void main(String[] args) {
        BaseTileExtractor extractor = new BaseTileExtractor();
        GdalUtil.init();
        Dataset dataset = gdal.Open("F:\\data\\personal\\1\\hsi_data\\Ortho_P1C_20230416083433516_0002_VNIR.bsq", GA_ReadOnly);
        Box tileLngLatExtent = new Box(116.77505493164064, 35.88126165890353, 116.77642822265626, 35.88237433729238);
        Box box = extractor.locationBoxPixelExtentFromWgs84(dataset, tileLngLatExtent);
        System.out.println(box.toString());
    }

    /**
     * 根据 wgs84坐标范围 转化为 影像 像素空间坐标
     *
     * @param sourceDataset    影像
     * @param tileLngLatExtent 经纬度范围 wgs84
     * @return
     */
    public Box locationBoxPixelExtentFromWgs84(Dataset sourceDataset, Box tileLngLatExtent) {
        SpatialReference reference = new SpatialReference();
        reference.ImportFromWkt(sourceDataset.GetProjection());
        reference.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);
        SpatialReference wgs84 = new SpatialReference();
        wgs84.ImportFromEPSG(4326);
        wgs84.SetAxisMappingStrategy(OAMS_TRADITIONAL_GIS_ORDER);

        CoordinateTransformation coordinateTransformation = CoordinateTransformation.CreateCoordinateTransformation(wgs84, reference);
        System.out.println("[INFO] tile extend in WGS84 left bottom [" + tileLngLatExtent.xmin + " " + tileLngLatExtent.ymin + "]");
        System.out.println("[INFO] tile extend in WGS84 right top   [" + tileLngLatExtent.xmax + " " + tileLngLatExtent.ymax + "]");

        // becarefull  the order of input parameter , (lat,lng)[GDAL>3.0] instead of (lng,lat)
        // return value order id  (lat lng)
        double[] bottomLeft = coordinateTransformation.TransformPoint(tileLngLatExtent.xmin, tileLngLatExtent.ymin);
        double[] topRight = coordinateTransformation.TransformPoint(tileLngLatExtent.xmax, tileLngLatExtent.ymax);

        System.out.println("[INFO] tile extend in source left bottom [" + bottomLeft[0] + " " + bottomLeft[1] + "]");
        System.out.println("[INFO] tile extend in source right top   [" + topRight[0] + " " + topRight[1] + "]");

        // 影像参考系下的坐标范围
        Box source = new Box(bottomLeft[0], bottomLeft[1], topRight[0], topRight[1]);


        Point point = imageSpaceToSourceSpace(sourceDataset.GetGeoTransform(), new Point(bottomLeft[0], bottomLeft[1]));
        System.out.println(point);
        Point point1 = imageSpaceToSourceSpace(sourceDataset.GetGeoTransform(), new Point(topRight[0], topRight[1]));
        System.out.println(point1);

        Box box = locationBoxPixelExtent(sourceDataset.GetGeoTransform(), source);
        System.out.println(box.toString());
        return box;
    }

    /**
     * 设置颜色表
     *
     * @param table
     */
    public void setColorTable(ColorTable table) {
        this.colorTable = table;

    }

    /**
     * 每张图片只能调用一次
     *
     * @return
     */
    public synchronized byte[] getBlackBuffer(int size) {
        if (blackBuffer == null) {
            blackBuffer = new byte[size];
        }
        for (int i = 0; i < size; i++) {
            blackBuffer[i] = (byte) 0x00;
        }
        return blackBuffer;
    }

    public synchronized ByteBuffer getTargetBuffer(int w, int h) {
        return ByteBuffer.allocateDirect(w * h);
    }

    public synchronized ByteBuffer getSourceBuffer(int w, int h) {
        return ByteBuffer.allocateDirect(w * h * 8);
    }

    /**
     * 处理波段数据
     *
     * @param imageInfo
     * @param sourceDataset
     * @param targetDataset
     * @param sourceBandList
     * @param targetBandList
     * @return　如果用户要求的是单波段　就返回　true
     */
    public boolean processBands(ImageInfo imageInfo, Dataset sourceDataset, Dataset targetDataset, List<BandData> sourceBandList, List<Band> targetBandList) {
        //如果波段数 >= 4 RGB取 4 3 2 波段
        ChanelData chanelData = imageInfo.getChanelData();
        if (chanelData == null) {
            chanelData = new ChanelData();
        }
        sourceBandList.add(new BandData(sourceDataset.GetRasterBand(chanelData.getRedChanel()),
                imageInfo.findBand(chanelData.getRedChanel())));
        sourceBandList.add(new BandData(sourceDataset.GetRasterBand(chanelData.getGreenChanel()),
                imageInfo.findBand(chanelData.getGreenChanel())));
        sourceBandList.add(new BandData(sourceDataset.GetRasterBand(chanelData.getBlueChanel()),
                imageInfo.findBand(chanelData.getBlueChanel())));

        //目标PNG 我们确定有三个波段
        for (int i = 1; i <= 3; i++) {
            targetBandList.add(targetDataset.GetRasterBand(i));
        }
        return chanelData.getRedChanel() == chanelData.getBlueChanel() && chanelData.getRedChanel() == chanelData.getGreenChanel();
    }

    /**
     * 读取元数据 不进行格式转换
     * 原始影像的一个举行区域 读取数据到 目标区域
     *
     * @param transparentBand
     * @param sourceX
     * @param sourceY
     * @param sourceWidth
     * @param sourceHeight
     * @param targetX
     * @param targetY
     * @param targetWidth
     * @param targetHeight
     * @param tileWidth
     * @return
     */
    public ByteBuffer readSourceDataNoTranslate(byte[] transparentBand, BandData sourceBandData,
                                                int sourceX, int sourceY,
                                                int sourceWidth, int sourceHeight,
                                                int targetX, int targetY,
                                                int targetWidth, int targetHeight,
                                                int tileWidth
    ) {
        Band sourceBand = sourceBandData.getBand();
        ByteBuffer source = ByteBuffer.allocateDirect(targetWidth * targetHeight * 8);
        int dt = sourceBand.GetRasterDataType();
        source.position(0);
        sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, dt, source);
        return source;
    }

    /**
     * 读取元数据 转换为 RGB byte 格式
     *
     * @param transparentBand
     * @param sourceX
     * @param sourceY
     * @param sourceWidth
     * @param sourceHeight
     * @param targetX
     * @param targetY
     * @param targetWidth
     * @param targetHeight
     * @param canvasWidth
     * @param canvasHeight
     * @return
     */
    public ByteBuffer readAndTranslateToBytes256(byte[] transparentBand, BandData sourceBandData,
                                                 int sourceX, int sourceY,
                                                 int sourceWidth, int sourceHeight,
                                                 int targetX, int targetY,
                                                 int targetWidth, int targetHeight,
                                                 int canvasWidth, int canvasHeight
    ) {
        Band sourceBand = sourceBandData.getBand();
        int dt = sourceBand.GetRasterDataType();

        ByteBuffer target = getTargetBuffer(canvasWidth, canvasHeight);
        ByteBuffer source = getSourceBuffer(targetWidth, targetHeight);

        target.position(0);
        source.position(0);


        if (dt == GDT_Byte || dt==GDT_Int8) {
            // read image data
            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, GDT_Byte, source);
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            BandInfo sourceBandInfo = sourceBandData.info;
            //循环处理每一个像素
            if (sourceBandInfo.enableGamma) {
                if (sourceBandInfo.gammaMax == null || sourceBandInfo.gammaMin == null || sourceBandInfo.gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(sourceBandInfo, source, dt, new Rect().setValue(0, 0, targetWidth, targetHeight));
                }
            }
            sourceBandInfo.check();

            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        //原始影像的位置
                        int pos = (row * targetWidth + col);
                        //目标影像的位置
                        int posTarget = (targetY + row) * canvasWidth + col + targetX;
                        //读取原始影像 pos 位置的像素值
                        double pixel = ((int) source.get(pos) & 0xFF);
                        if (transparentBand != null) {
                            if (isPixelTransparent(pixel, sourceBandInfo)) {
                                transparentBand[posTarget] = (byte) 0x00;
                            } else {
                                //如果不是透明颜色 且传入了alpha通道 设为不透明 0xFF  0x00是透明颜色
                                transparentBand[posTarget] = (byte) 0xFF;
                            }
                        }
                        if (sourceBandInfo.enableGamma) {
                            // 采用Gamma矫正算法
                            //  value  [outputMin,outputMax]
                            pixel = sourceBandInfo.calValue(pixel);
                        }

                        target.put(posTarget, (byte) pixel);
                    }
                }
        } else if (dt == GDT_Int16 || dt == gdalconstConstants.GDT_UInt16) {
            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, dt, source);
            //循环处理影像 并进行拉伸压缩
            log.info("Band DataType int16");
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            BandInfo sourceBandInfo = sourceBandData.info;
            //循环处理每一个像素
            if (sourceBandInfo.enableGamma) {
                if (sourceBandInfo.gammaMax == null || sourceBandInfo.gammaMin == null || sourceBandInfo.gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(sourceBandInfo, source, dt, new Rect().setValue(0, 0, targetWidth, targetHeight));
                }
            }
            sourceBandInfo.check();
            target.position(0);
            ShortBuffer shortBuffer = source.asShortBuffer();
            for (int row = 0; row < targetHeight; ++row) {
                for (int col = 0; col < targetWidth; ++col) {
                    //原始影像的位置
                    int pos = (row * targetWidth + col);
                    //目标影像的位置
                    int posTarget = (targetY + row) * canvasWidth + col + targetX;
                    //读取原始影像 pos 位置的像素值
                    double pixel = shortBuffer.get(pos);
                    if (transparentBand != null) {
                        if (isPixelTransparent(pixel, sourceBandInfo)) {
                            transparentBand[posTarget] = (byte) 0x00;
                        } else {
                            //如果不是透明颜色 且传入了alpha通道 设为不透明 0xFF  0x00是透明颜色
                            transparentBand[posTarget] = (byte) 0xFF;
                        }
                    }
                    pixel = sourceBandInfo.calValue(pixel);
                    int v = (((int) pixel) & 0xFF);
                    target.put(posTarget, (byte) v);
                }
            }
        } else if (dt == gdalconstConstants.GDT_UInt32 || dt == gdalconstConstants.GDT_Int32) {
            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, gdalconstConstants.GDT_Int32, source);
            //循环处理影像 并进行拉伸压缩
            log.info("Band DataType uint32");
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            BandInfo sourceBandInfo = sourceBandData.info;
            //循环处理每一个像素
            if (sourceBandInfo.enableGamma) {
                if (sourceBandInfo.gammaMax == null || sourceBandInfo.gammaMin == null || sourceBandInfo.gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(sourceBandInfo, source, dt, new Rect().setValue(0, 0, targetWidth, targetHeight));
                }
            }
            sourceBandInfo.check();
            IntBuffer intBuffer = source.asIntBuffer();
            target.position(0);
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {

                        //原始影像的位置
                        int pos = (row * targetWidth + col);
                        //目标影像的位置
                        int posTarget = (targetY + row) * canvasWidth + col + targetX;
                        //读取原始影像 pos 位置的像素值
                        double pixel = intBuffer.get(pos);
                        if (transparentBand != null) {
                            if (isPixelTransparent(pixel, sourceBandInfo)) {
                                transparentBand[posTarget] = (byte) 0x00;
                            } else {
                                //如果不是透明颜色 且传入了alpha通道 设为不透明 0xFF  0x00是透明颜色
                                transparentBand[posTarget] = (byte) 0xFF;
                            }
                        }
                        pixel = sourceBandInfo.calValue(pixel);
                        int v = (((int) pixel) & 0xFF);
                        target.put(posTarget, (byte) v);
                    }
                }
        } else if (dt == gdalconstConstants.GDT_Float32) {

            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight,
                    targetWidth, targetHeight, gdalconstConstants.GDT_Float32, source);
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            BandInfo sourceBandInfo = sourceBandData.info;
            //循环处理每一个像素
            if (sourceBandInfo.enableGamma) {
                if (sourceBandInfo.gammaMax == null || sourceBandInfo.gammaMin == null || sourceBandInfo.gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(sourceBandInfo, source, dt, new Rect().setValue(0, 0, targetWidth, targetHeight));
                }
            }
            sourceBandInfo.check();
            target.position(0);
            FloatBuffer floatBuffer = source.asFloatBuffer();
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        //原始影像的位置
                        int pos = (row * targetWidth + col);
                        //目标影像的位置
                        int posTarget = (targetY + row) * canvasWidth + col + targetX;
                        //读取原始影像 pos 位置的像素值
                        double pixel = floatBuffer.get(pos);
                        if (transparentBand != null) {
                            if (isPixelTransparent(pixel, sourceBandInfo)) {
                                transparentBand[posTarget] = (byte) 0x00;
                            } else {
                                //如果不是透明颜色 且传入了alpha通道 设为不透明 0xFF  0x00是透明颜色
                                transparentBand[posTarget] = (byte) 0xFF;
                            }
                        }
                        pixel = sourceBandInfo.calValue(pixel);
                        int v = (((int) pixel) & 0xFF);
                        target.put(posTarget, (byte) v);
                    }
                }
        } else if (dt == gdalconstConstants.GDT_Float64) {

            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight,
                    targetWidth, targetHeight, gdalconstConstants.GDT_Float64, source);
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            BandInfo sourceBandInfo = sourceBandData.info;
            //循环处理每一个像素
            if (sourceBandInfo.enableGamma) {
                if (sourceBandInfo.gammaMax == null || sourceBandInfo.gammaMin == null || sourceBandInfo.gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(sourceBandInfo, source, dt, new Rect().setValue(0, 0, targetWidth, targetHeight));
                }
            }
            sourceBandInfo.check();
            DoubleBuffer doubleBuffer = source.asDoubleBuffer();
            //循环处理影像 并进行拉伸压缩
            target.position(0);
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        //原始影像的位置
                        int pos = (row * targetWidth + col);
                        //目标影像的位置
                        int posTarget = (targetY + row) * canvasWidth + col + targetX;
                        //读取原始影像 pos 位置的像素值
                        double pixel = doubleBuffer.get(pos);
                        if (transparentBand != null) {
                            if (isPixelTransparent(pixel, sourceBandInfo)) {
                                transparentBand[posTarget] = (byte) 0x00;
                            } else {
                                //如果不是透明颜色 且传入了alpha通道 设为不透明 0xFF  0x00是透明颜色
                                transparentBand[posTarget] = (byte) 0xFF;
                            }
                        }
                        pixel = sourceBandInfo.calValue(pixel);
                        int v = (((int) pixel) & 0xFF);
                        target.put(posTarget, (byte) v);
                    }
                }
        } else {
            log.error(" 数据长度 未能处理数据类型 {}", dt);
        }
        return target;
    }


    private boolean isPixelTransparent(double pixel, BandInfo bandInfo) {
        Double[] noValues = bandInfo.noValues;
        if (noValues != null && noValues.length > 0) {
            for (int t = 0; t < noValues.length; t++) {
                if (Math.abs(pixel - noValues[t]) < 0.0000001) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * change int value to byte value
     * min   max
     * DEM    -744   6578
     * 0      255
     * x=255*(a-min)/(max-min)
     *
     * @param sourceBandData
     * @param novalue
     * @param anInt
     * @return
     */
    private int intValueToByteValue(Boolean[] isTransprentOut, BandData sourceBandData, Double[] novalue, int anInt, boolean extraction) {
        int b = anInt;
        if (extraction) {
            //处理空值的问题
            double range = (anInt - sourceBandData.info.getMinValue());
            double total = (sourceBandData.info.getMaxValue() - sourceBandData.info.getMinValue());
            double scale = range / total;
            double v1 = 255 * scale;
            b = (int) v1;
        }

        if (novalue != null && novalue.length > 0) {
            for (int t = 0; t < novalue.length; t++) {
                int et = novalue[t].intValue();
                if (et == b) {
                    isTransprentOut[0] = true;
                    break;
                }
            }
        } else {
            isTransprentOut[0] = false;
        }
        return b & 0xFF;
    }

    /**
     * check pixel Value <anFloat>
     * 1.is Transparent
     * 2. normalize? -> 0,1
     *
     * @param sourceBandData
     * @param novalue
     * @param anFloat
     * @return
     */
    private float checkValue(Boolean[] isTransprentOut, BandData sourceBandData, Double[] novalue, float anFloat, boolean normalize) {
        if (novalue != null && novalue.length > 0) {
            for (int t = 0; t < novalue.length; t++) {
                float et = novalue[t].floatValue();
                if (Math.abs(et - anFloat) < 0.000001) {
                    isTransprentOut[0] = true;
                    break;
                }
            }
        } else {
            isTransprentOut[0] = false;
        }

        if (normalize) {
            //处理空值的问题
            double range = (anFloat - sourceBandData.info.getMinValue());
            double total = (sourceBandData.info.getMaxValue() - sourceBandData.info.getMinValue());
            if (Math.abs(total) < 0.0000001) {
                return 0;
            }
            return (float) (range / total);
        } else {
            return anFloat;
        }
    }

    /**
     * 根据仿射变换 将经纬度坐标转换到 影像的像素坐标空间
     * 利用下面的公式进行逆变换求解 已知(XP YP ) 求解  (P L)
     * * Xp = padfTransform[0] + P*padfTransform[1] + L*padfTransform[2];
     * * Yp = padfTransform[3] + P*padfTransform[4] + L*padfTransform[5];
     * <p>
     * <p>
     * L=(a1*a3-a4*a0+a4*Xp-a1*Yp)/(a2*a4-a1*a5)
     * P=-(a2*a3-a5*a0+a5*Xp-a2*Yp)/(a2*a4-a1*a5)
     *
     * @param adfGeoTransform
     * @param tileLngLatExtent
     * @return
     */
    protected Box locationBoxPixelExtent(double[] adfGeoTransform, Box tileLngLatExtent) {
        double a1a3 = adfGeoTransform[1] * adfGeoTransform[3];
        double a4a0 = adfGeoTransform[4] * adfGeoTransform[0];
        double a2a3 = adfGeoTransform[2] * adfGeoTransform[3];
        double a5a0 = adfGeoTransform[5] * adfGeoTransform[0];
        double a2a4 = adfGeoTransform[2] * adfGeoTransform[4];
        double a1a5 = adfGeoTransform[1] * adfGeoTransform[5];
        // TL
        double L0 = (a1a3 - a4a0 + adfGeoTransform[4] * tileLngLatExtent.getXmin() - adfGeoTransform[1] * tileLngLatExtent.getYmax()) / (a2a4 - a1a5);
        double P0 = -(a2a3 - a5a0 + adfGeoTransform[5] * tileLngLatExtent.getXmin() - adfGeoTransform[2] * tileLngLatExtent.getYmax()) / (a2a4 - a1a5);
        //RB
        double L1 = (a1a3 - a4a0 + adfGeoTransform[4] * tileLngLatExtent.getXmax() - adfGeoTransform[1] * tileLngLatExtent.getYmin()) / (a2a4 - a1a5);
        double P1 = -(a2a3 - a5a0 + adfGeoTransform[5] * tileLngLatExtent.getXmax() - adfGeoTransform[2] * tileLngLatExtent.getYmin()) / (a2a4 - a1a5);
        return new Box(P0, L1, P1, L0);
    }

    private void printDataType() {
        log.info(" GDT_Unknown {}", gdalconstJNI.GDT_Unknown_get());
        log.info(" GDT_Byte {}", gdalconstJNI.GDT_Byte_get());
        log.info(" GDT_UInt16 {}", gdalconstJNI.GDT_UInt16_get());
        log.info(" GDT_Int16 {}", gdalconstJNI.GDT_Int16_get());
        log.info(" GDT_UInt32 {}", gdalconstJNI.GDT_UInt32_get());
        log.info(" GDT_Int32 {}", gdalconstJNI.GDT_Int32_get());
        log.info(" GDT_Float32 {}", gdalconstJNI.GDT_Float32_get());
        log.info(" GDT_Float64 {}", gdalconstJNI.GDT_Float64_get());
        log.info(" GDT_CInt16 {}", gdalconstJNI.GDT_CInt16_get());
        log.info(" GDT_CInt32 {}", gdalconstJNI.GDT_CInt32_get());
        log.info(" GDT_CFloat32 {}", gdalconstJNI.GDT_CFloat32_get());
        log.info(" GDT_CFloat64 {}", gdalconstJNI.GDT_CFloat64_get());
        log.info(" GDT_TypeCount {}", gdalconstJNI.GDT_TypeCount_get());
    }

    public boolean preview(ImageInfo imageInfo, Dataset sourceDataset, Dataset targetDataset, int targetWidth, int targetHeight) {

        //目标tile的像素空间 初始化设置 下面会根据不同的情况进行调整
        Rect targetRect = new Rect(0, 0, targetWidth, targetHeight);
        Rect imageRect = new Rect(0, 0, (int) imageInfo.width, (int) imageInfo.height);


        //准备波段数据
        //如果原始波段数 大于3  只取前三个波段进行输出
        //如果原始波段数 小于3  用最后一个波段填充其余的波段
        // 构造三个波段进行处理
        List<BandData> sourceBandList = new ArrayList<>(3);
        List<Band> targetBandList = new ArrayList<>(3);

        boolean singleBand = processBands(imageInfo, sourceDataset, targetDataset, sourceBandList, targetBandList);
        byte[] transparentBand = getBlackBuffer(targetWidth * targetHeight);
        for (int bandIndex = 0; bandIndex < 3; bandIndex++) {

            BandData sourceBand = sourceBandList.get(bandIndex);
            Band targetBand = targetBandList.get(bandIndex);
            //读出原数据
            //写出目标文件
            //这里的操作会拉伸影像
            try {
                ByteBuffer byteBuffer = readAndTranslateToBytes256(
                        transparentBand, sourceBand,
                        imageRect.getXAsInt(), imageRect.getYAsInt(),
                        imageRect.getWidthAsInt(), imageRect.getHeightAsInt(),
                        targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(),
                        targetWidth, targetHeight);

                targetBand.WriteRaster_Direct(targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), byteBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (targetDataset.getRasterCount() == 4) {
            targetDataset.GetRasterBand(4).WriteRaster(0, 0, targetWidth, targetHeight, transparentBand);
        }
        //写完了一个tile 直接输出到 .S文件中去吧
        targetDataset.FlushCache();
        return true;
    }

    /**
     * @param value 0xRRGGBBAA
     * @return
     */
    public int translateColor(double value) {
        if (colorTable == null) {
            return defaultColor;
        }
        return colorTable.mapColor(value);
    }

    /**
     * 读取 三波段影像数据 并返回 Alpha波段数据
     *
     * @param canvasSize
     * @param targetRect
     * @param sourceRect
     * @param sourceBandList
     * @param targetBandList
     * @return
     */
    public byte[] getBand(boolean singleBand, Size canvasSize, Rect targetRect, Rect sourceRect, List<BandData> sourceBandList, List<Band> targetBandList) {
        int canvasLength = canvasSize.getYAsInt() * canvasSize.getXAsInt();
        byte[] transparentBand = getBlackBuffer(canvasLength);

        BandData source1 = sourceBandList.get(0);
        //这里的 sourceData 就是一个原始数据类型的值数组
        //单波段影像
        if (singleBand) {
            //灰度影像 使用颜色表进行替换操作
            BandData sourceBand = source1;

            if (sourceBand.getInfo().getNoValues() == null) {
                Double[] noValue = new Double[0];
                sourceBand.getInfo().setNoValues(noValue);
            }

            //这里的 sourceData 就是一个原始数据类型的值数组
            ByteBuffer sourceData = readSourceDataNoTranslate(
                    transparentBand, sourceBand,
                    sourceRect.getXAsInt(), sourceRect.getYAsInt(),
                    sourceRect.getWidthAsInt(), sourceRect.getHeightAsInt(),
                    targetRect.getXAsInt(), targetRect.getYAsInt(),
                    targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), canvasSize.getXAsInt());

            //RGB 通道 用于替换颜色
            ByteBuffer[] sourceBuffer = new ByteBuffer[3];

            sourceBuffer[0] = ByteBuffer.allocateDirect(canvasLength);
            sourceBuffer[1] = ByteBuffer.allocateDirect(canvasLength);
            sourceBuffer[2] = ByteBuffer.allocateDirect(canvasLength);

            //根据读出的值 用颜色表替换
            // sourceData.position(0); //原始影像
            int dt = sourceBand.getBand().GetRasterDataType();
            //循环处理每一个像素
            sourceData.order(ByteOrder.nativeOrder());

            if (source1.getInfo().enableGamma) {
                if (source1.getInfo().gammaMax == null || source1.getInfo().gammaMin == null || source1.getInfo().gamma == null) {
                    // gamma 矫正参数未设置, 根据当前小区域计算
                    calculateGamma(source1.getInfo(), sourceData, dt, targetRect);
                }
            }

            source1.getInfo().check();
            FloatBuffer floatBuffer = sourceData.asFloatBuffer();
            DoubleBuffer doubleBuffer = sourceData.asDoubleBuffer();
            IntBuffer intBuffer = sourceData.asIntBuffer();
            ShortBuffer shortBuffer = sourceData.asShortBuffer();

            //循环目标区域 [0-78][0-32]
            for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
                for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                    //目标像素位置 用于读取经过GDAL采样后的影像数组
                    int location = row * targetRect.getWidthAsInt() + col;

                    int tilePosition = (targetRect.getYAsInt() + row) * canvasSize.getXAsInt() + targetRect.getXAsInt() + col;
                    double pixelValue = 0;
                    if (dt == GDT_Byte || dt==GDT_Int8) {
                        pixelValue = ((int) sourceData.get(location)) & 0xFF;

                    } else if (dt == GDT_Int16) {
                        pixelValue = shortBuffer.get(location);
                    } else if (dt == gdalconstConstants.GDT_UInt16) {
                        int v = shortBuffer.get(location) & 0xFFFF;
                        pixelValue = v;
                    } else if (dt == gdalconstConstants.GDT_Int32) {
                        pixelValue = intBuffer.get(location);
                    } else if (dt == GDT_UInt32) {
                        long unsignedLong = Integer.toUnsignedLong(intBuffer.get(location));
                        pixelValue = unsignedLong;
                    } else if (dt == gdalconstConstants.GDT_Float32) {
                        pixelValue = floatBuffer.get(location);
                    } else if (dt == gdalconstConstants.GDT_Float64) {
                        pixelValue = doubleBuffer.get(location);
                    }

                    int rgba;


                    if (source1.getInfo().enableGamma) {
                        // 采用Gamma矫正算法
                        //  value  [outputMin,outputMax]
                        double value = source1.getInfo().calValue(pixelValue);
                        if(colorTable == null){
                            int v = (int) value;
                            rgba = Colors.fromColorInt(v, v, v, 0xFF);
                        } else  if (colorTable.getDefaultTable() != null && colorTable.getDefaultTable()) {
                            //缺省的颜色表
                            int v = (int) value;
                            rgba = Colors.fromColorInt(v, v, v, 0xFF);
                        } else {
                            if (colorTable != null && colorTable.getNormalize() != null && colorTable.getNormalize()) {
                                //用户设置了归一化调色板
                                pixelValue = normalizePixel(sourceBand, value);
                                rgba = translateColor(pixelValue);
                            } else {
                                long valueLong = Math.round(value);
                                rgba = translateColor(valueLong);
                            }
                        }
                    } else if (colorTable != null) {
                        //颜色表为缺省的　首先使用
                        if (colorTable.getDefaultTable() != null && colorTable.getDefaultTable()) {

                            if (sourceBand.getInfo().colorMaps != null) {
                                //用户设定了自己的颜色表　就用用户的颜色表渲染
                                rgba = translateImageColorTable(sourceBand.getInfo().colorMaps, pixelValue);
                            } else {
                                //如果没有设定,直接用像素值
                                int v = (int) pixelValue;
                                rgba = Colors.fromColorInt(v, v, v, 0xFF);
                            }
                        } else {
                            //不是缺省的颜色表
                            //用户自定义了颜色表
                            if (colorTable.getNormalize() != null && colorTable.getNormalize()) {
                                //颜色表是归一化颜色表 0.0-1.0  范围之外的颜色通通为透明
                                // 讲数据中的颜色进行归一化处理
                                pixelValue = normalizePixel(sourceBand, pixelValue);

                                if (pixelValue < 0.0 || pixelValue > 1.0) {
                                    rgba = 0xFFFFFF00;
                                } else {
                                    rgba = translateColor(pixelValue);
                                }

                            } else {
                                //颜色表不是归一化颜色表 使用颜色表进行转换
                                rgba=translateColor(pixelValue);
                            }
                        }
                    } else {
                        //没有条色斑　原始数据
                        int v = (int) pixelValue;
                        rgba = Colors.fromColorInt(v, v, v, 0xFF);
                    }

                    sourceBuffer[0].put(tilePosition, (byte) (Colors.r(rgba) & 0xFF));
                    sourceBuffer[1].put(tilePosition, (byte) (Colors.g(rgba) & 0xFF));
                    sourceBuffer[2].put(tilePosition, (byte) (Colors.b(rgba) & 0xFF));
                    transparentBand[tilePosition] = (byte) (Colors.a(rgba) & 0xFF);
                }
            }

            for (int i = 0; i < 3; i++) {
                Band targetBand = targetBandList.get(i);
                targetBand.WriteRaster_Direct(0, 0,
                        canvasSize.getXAsInt(), canvasSize.getYAsInt(), sourceBuffer[i]);
            }
        } else {
            for (int bandIndex = 0; bandIndex < 3; bandIndex++) {

                BandData sourceBand = sourceBandList.get(bandIndex);
                Band targetBand = targetBandList.get(bandIndex);


                //这里的操作会拉伸影像
                ByteBuffer byteBuffer = readAndTranslateToBytes256(
                        transparentBand, sourceBand,
                        sourceRect.getXAsInt(), sourceRect.getYAsInt(),
                        sourceRect.getWidthAsInt(), sourceRect.getHeightAsInt(),
                        targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(),
                        canvasSize.getXAsInt(), canvasSize.getYAsInt());
                targetBand.WriteRaster_Direct(0, 0, canvasSize.getXAsInt(), canvasSize.getYAsInt(), byteBuffer);
            }
        }
        return transparentBand;
    }

    private int translateImageColorTable(List<ColorMap> colorMaps, double pixelValue) {
        if (colorMaps == null) {
            return defaultColor;
        }
        for (ColorMap colorMap : colorMaps) {
            if (colorMap.getStart() == pixelValue) {
                return colorMap.getRgba();
            }
        }
        //透明颜色
        return 0;
    }


    /**
     * 直接读取目标影像中的数据 不进行采样
     *
     * @param left
     * @param top
     * @param width
     * @param height
     * @param bands  [1,2,3] start with 1
     * @return byteBuffer save the float value
     */
    public ByteBuffer[] readImageBandSourceData(String location, int left, int top, int width, int height, int[] bands, int[] outBandsType) {
        Rect source = new Rect();
        Rect target = new Rect();
        Dataset dataset;
        try {
            dataset = gdal.Open(location);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        int rasterWidth = dataset.getRasterXSize();
        int rasterHeight = dataset.getRasterYSize();
        if (width <= 0 || height <= 0) {
            log.error("read image width or height <=0");
            return null;
        }

        if (left <= -width || left >= rasterWidth) {
            log.error("read image left or right is out of range");
            return null;
        }
        if (top <= -height || top >= rasterHeight) {
            log.error("read image top or bottom is out of range");
            return null;
        }

        if (left <= 0) {
            source.x = 0;
            target.x = -left;
            int imageWidth = Math.min(width + left, rasterWidth);
            source.width = imageWidth;
            target.width = imageWidth;
        } else {
            source.x = left;
            target.x = 0;
            int imageWidth = Math.min(rasterWidth - left, width);
            source.width = imageWidth;
            target.width = imageWidth;
        }

        if (top <= 0) {
            source.y = 0;
            target.y = -top;
            int imageHeight = Math.min(height + top, rasterHeight);
            source.height = imageHeight;
            target.height = imageHeight;
        } else {
            source.y = top;
            target.y = 0;
            int imageHeight = Math.min(rasterHeight - top, height);
            source.height = imageHeight;
            target.height = imageHeight;
        }

        ByteBuffer[] buffers = new ByteBuffer[bands.length];


        //临时缓存
        ByteBuffer buffer = ByteBuffer.allocateDirect(source.getWidthAsInt() * source.getHeightAsInt() * 8);
        buffer.order(ByteOrder.nativeOrder());

        for (int i = 0; i < bands.length; i++) {

            buffer.clear();
            int bandIndex = bands[i];
            if (bandIndex < 1 || bandIndex > dataset.GetRasterCount()) {
                bandIndex = 1;
            }
            Band band = dataset.GetRasterBand(bandIndex);
            int dataType = band.getDataType();

            outBandsType[i] = dataType;
            band.ReadRaster_Direct(source.getXAsInt(), source.getYAsInt(),
                    source.getWidthAsInt(), source.getHeightAsInt(),
                    target.getWidthAsInt(), target.getHeightAsInt(),
                    dataType, buffer);

            ByteBuffer outputBuffer = ByteBuffer.allocateDirect(width * height * 8);
            FloatBuffer outputFloatBuffer = outputBuffer.asFloatBuffer();

            ShortBuffer sourceShort = buffer.asShortBuffer();
            FloatBuffer sourceFloat = buffer.asFloatBuffer();
            DoubleBuffer sourceDouble = buffer.asDoubleBuffer();
            IntBuffer sourceInt = buffer.asIntBuffer();
            //读出之后 讲图像移动到目标位置
            for (int y = 0; y < target.getHeightAsInt(); y++) {
                for (int x = 0; x < target.getWidthAsInt(); x++) {
                    int targePos = (target.getYAsInt() + y) * width + (target.getXAsInt() + x);
                    int sourcePos = (x + y * source.getWidthAsInt());
                    if (dataType == GDT_Byte || dataType==GDT_Int8) {
                        byte b = buffer.get(sourcePos);
                        float v = (int) b & 0xFF;
                        outputFloatBuffer.put(targePos, v);
                    } else if (dataType == GDT_UInt16) {
                        short s = sourceShort.get();
                        int v = s & 0xFFFF;
                        outputFloatBuffer.put(targePos, v);
                    } else if (dataType == GDT_Int16) {
                        short s = sourceShort.get();
                        int v = s;
                        outputFloatBuffer.put(targePos, v);
                    } else if (dataType == GDT_Int32) {
                        outputFloatBuffer.put(targePos, sourceInt.get(sourcePos));
                    } else if (dataType == GDT_UInt32) {
                        long l = sourceInt.get(sourcePos) & 0xFFFFFFFFL;
                        outputFloatBuffer.put(targePos, l);
                    } else if (dataType == GDT_Float32) {
                        outputFloatBuffer.put(targePos, sourceFloat.get(sourcePos));
                    } else if (dataType == GDT_Float64) {
                        outputFloatBuffer.put(targePos, (float) sourceDouble.get(sourcePos));
                    }
                }
            }

            buffers[i] = outputBuffer;
        }
        // dataset.Close();
        return buffers;
    }

    /**
     * Gamma correction the pixel,by the way with linenarly extraction
     *
     * @param pixelValue
     * @param gammaMin
     * @param gammaMax
     * @param gamma      0.1-6
     * @return 0-1.0
     */
    private double clip(double pixelValue, Double gammaMin, Double gammaMax, Double gamma) {
        if (pixelValue <= gammaMin) {
            pixelValue = gammaMin;
        }
        if (pixelValue >= gammaMax) {
            pixelValue = gammaMax;
        }
        double value = Math.pow((pixelValue - gammaMin) / (gammaMax - gammaMin), gamma);
        return value;
    }

    /**
     * 像素值归一化处理   less 0 || great 1 -> is transparent  set it Value to -1
     *
     * @param sourceBand
     * @param pixelValue
     * @return
     */
    private double normalizePixel(BandData sourceBand, double pixelValue) {
        double total = sourceBand.getInfo().getMaxValue() - sourceBand.getInfo().getMinValue();
        double value = pixelValue - sourceBand.getInfo().getMinValue();
        if (Math.abs(total) < 0.0000001) {
            //颜色数据差别娇小 无法显示 设为-1
            return -1.0;
        }
        return Math.abs(value / total);
    }

    private void calculateGamma(BandInfo info, ByteBuffer sourceData, int dt, Rect targetRect) {
        // 遍历 sourceData, 计算最大最小值
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
            for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                int location = row * targetRect.getWidthAsInt() + col;
                double pixelValue = 0;
                if (dt == GDT_Byte || dt==GDT_Int8) {
                    pixelValue = sourceData.get(location) & 0xFF;
                } else if (dt == GDT_Int16 || dt == gdalconstConstants.GDT_UInt16) {
                    pixelValue = sourceData.asShortBuffer().get(location) & 0xFFFF;
                } else if (dt == gdalconstConstants.GDT_Int32 || dt == gdalconstConstants.GDT_UInt32) {
                    pixelValue = sourceData.asIntBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float32) {
                    pixelValue = sourceData.asFloatBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float64) {
                    pixelValue = sourceData.asDoubleBuffer().get(location);
                }
                if (pixelValue > max) {
                    max = pixelValue;
                }
                if (pixelValue < min) {
                    min = pixelValue;
                }
            }
        }
        sourceData.position(0);
        // 进行线性拉伸, 计算直方图
        int[] histogram = new int[256];
        for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
            for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                int location = row * targetRect.getWidthAsInt() + col;
                double pixelValue = 0;
                if (dt == GDT_Byte || dt==GDT_Int8) {
                    pixelValue = sourceData.get(location) & 0xFF;
                } else if (dt == GDT_Int16 || dt == gdalconstConstants.GDT_UInt16) {
                    pixelValue = sourceData.asShortBuffer().get(location) & 0xFFFF;
                } else if (dt == gdalconstConstants.GDT_Int32 || dt == gdalconstConstants.GDT_UInt32) {
                    pixelValue = sourceData.asIntBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float32) {
                    pixelValue = sourceData.asFloatBuffer().get(location);
                } else if (dt == gdalconstConstants.GDT_Float64) {
                    pixelValue = sourceData.asDoubleBuffer().get(location);
                }
                if (max != min) {
                    double value = (pixelValue - min) / (max - min);
                    int key = (int) (value * 255);
                    histogram[key]++;
                }
            }
        }
        sourceData.position(0);

        // 计算平均亮度
        int totalBrightness = 0;
        int totalPixels = 0;
        for (int i = 0; i < histogram.length; i++) {
            totalBrightness += i * histogram[i];
            totalPixels += histogram[i];
        }
        double averageBrightness = totalBrightness / totalPixels;

        // 使用对数函数将平均亮度映射到伽马值
        double deviation = Math.log(averageBrightness) / Math.log(127);
        double gamma = 1.0;
//        if (deviation < 1) {
//            gamma = 1 - deviation;
//        } else if (deviation > 1) {
//            gamma = 1 + deviation;
//        }
        // 或者直接 gamma = 1 / deviation
        gamma = 1 / deviation;

        info.setGammaMax(max);
        info.setGammaMin(min);
        info.setGamma(gamma);
        // 需要将 sourceData 置为初始位置
        sourceData.position(0);
    }
}
