package cn.mapway.common.geo.tools;


import cn.mapway.geo.client.raster.ChanelData;
import cn.mapway.geo.client.raster.ImageInfo;
import cn.mapway.geo.shared.color.ColorTable;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import cn.mapway.ui.client.util.Colors;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.gdalconst.gdalconstJNI;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.gdal.gdalconst.gdalconstConstants.*;

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

    /**
     * 设置颜色表
     *
     * @param table
     */
    public void setColorTable(ColorTable table) {
        if (table != null) {
            this.colorTable = table;
        }
    }

    /**
     * 每张图片只能调用一次
     *
     * @return
     */
    public synchronized byte[] getBlackBuffer(int w, int h) {
        if (blackBuffer == null) {
            blackBuffer = new byte[w * h];
        }
        for (int i = 0; i < w * h; i++) {
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

    public void processBands(ImageInfo imageInfo, Dataset sourceDataset, Dataset targetDataset, List<BandData> sourceBandList, List<Band> targetBandList) {
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
    }

    /**
     * 读取元数据 不进行格式转换
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
     * @param tileWidth
     * @return
     */
    public ByteBuffer readAndTranslateToBytes256(byte[] transparentBand, BandData sourceBandData,
                                                 int sourceX, int sourceY,
                                                 int sourceWidth, int sourceHeight,
                                                 int targetX, int targetY,
                                                 int targetWidth, int targetHeight,
                                                 int tileWidth
    ) {
        Band sourceBand = sourceBandData.getBand();
        int dt = sourceBand.GetRasterDataType();

        ByteBuffer target = getTargetBuffer(targetWidth, targetHeight);
        ByteBuffer source = getSourceBuffer(targetWidth, targetHeight);

        target.position(0);
        source.position(0);
        Boolean[] isTransparent = new Boolean[1];
        if (dt == GDT_Byte) {

            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, GDT_Byte, source);
            //循环处理影像 并进行拉伸压缩
            Double[] noValues = sourceBandData.info.getNoValues();
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        //原始影像的位置
                        int pos = (row * targetWidth + col);
                        //目标影像的位置
                        int posTarget = (targetY + row) * tileWidth + col + targetX;
                        //读取原始影像 pos 位置的像素值
                        int anInt = (source.get(pos) & 0xFF);
                        isTransparent[0] = false;

                        //判断是否是 无效值 如果无效值 isTransparent[0]==true
                        byte b = (byte) (0xFF & intValueToByteValue(isTransparent, sourceBandData, noValues, anInt, false));
                        //转换后的像素值
                        target.put(b);
                        if (!isTransparent[0] && transparentBand != null) {
                            //如果不是透明颜色 且传入了alpha通道 设为不透明 0xFF  0x00是透明颜色
                            transparentBand[posTarget] = (byte) 0xff;
                        }
                    }
                }
        } else if (dt == gdalconstConstants.GDT_Int16) {
            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, gdalconstConstants.GDT_Int16, source);
            //循环处理影像 并进行拉伸压缩
            Double[] novalue = sourceBandData.info.getNoValues();
            log.info("Band DataType uint16");
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        int pos = (row * targetWidth + col) * 2;
                        int posTarget = (targetY + row) * tileWidth + col + targetX;

                        int anInt = (source.get(pos + 1) << 8) & 0xFF00 + (source.get(pos) & 0xFF);
                        if (anInt == 0xFF00) {
                            anInt = 0;
                        }
                        isTransparent[0] = false;
                        //16位影像 不处理 noValue值
                        byte b = (byte) (0xFF & intValueToByteValue(isTransparent, sourceBandData, null, anInt, true));
                        target.put(pos / 2, b);
                        if (anInt > sourceBandData.info.getMaxValue()) {
                            log.info("{}-{} {} {} {}", row, col, anInt, b, 0);
                        }
                        if (isTransparent[0] && transparentBand != null) {
                            transparentBand[posTarget] = (byte) 0x00;
                        } else {
                            transparentBand[posTarget] = (byte) 0xff;
                        }
                    }
                }
        } else if (dt == gdalconstConstants.GDT_UInt16) {
            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight, targetWidth, targetHeight, gdalconstConstants.GDT_UInt16, source);
            //循环处理影像 并进行拉伸压缩
            Double[] novalue = sourceBandData.info.getNoValues();
            log.info("Band DataType uint16");
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        int pos = (row * targetWidth + col) * 2;
                        int posTarget = (targetY + row) * tileWidth + col + targetX;
                        int anInt = (source.get(pos + 1) << 8) & 0xFF00 + (source.get(pos) & 0xFF);
                        isTransparent[0] = false;
                        byte b = (byte) (0xFF & intValueToByteValue(isTransparent, sourceBandData, novalue, anInt, true));
                        target.put(pos / 2, b);
                        if (!isTransparent[0] && transparentBand != null) {
                            transparentBand[posTarget] = (byte) 0xff;
                        }
                    }
                }
        } else if (dt == gdalconstConstants.GDT_Float32) {

            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight,
                    targetWidth, targetHeight, gdalconstConstants.GDT_Float32, source);
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            Double[] novalue = sourceBandData.info.getNoValues();
            target.position(0);
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        int pos = (row * targetWidth + col) * 4;
                        int posTarget = (targetY + row) * tileWidth + col + targetX;
                        // source  postion 读取一个 64位的浮点数 怎么转换

                        int anInt = (int) source.getDouble(pos);
                        isTransparent[0] = false;
                        byte b = (byte) (0xFF & intValueToByteValue(isTransparent, sourceBandData, novalue, anInt, true));
                        target.put(b);
                        if (!isTransparent[0] && transparentBand != null) {
                            transparentBand[posTarget] = (byte) 0xff;
                        }
                    }
                }
        } else if (dt == gdalconstConstants.GDT_Float64) {

            sourceBand.ReadRaster_Direct(sourceX, sourceY, sourceWidth, sourceHeight,
                    targetWidth, targetHeight, gdalconstConstants.GDT_Float64, source);
            source.order(ByteOrder.nativeOrder());

            //循环处理影像 并进行拉伸压缩
            Double[] novalue = sourceBandData.info.getNoValues();
            target.position(0);
            for (int row = 0; row < targetHeight; row++)
                for (int col = 0; col < targetWidth; col++) {
                    {
                        int pos = (row * targetWidth + col) * 8;
                        int posTarget = (targetY + row) * tileWidth + col + targetX;
                        // source  postion 读取一个 64位的浮点数 怎么转换

                        int anInt = (int) source.getDouble(pos);
                        isTransparent[0] = false;
                        byte b = (byte) (0xFF & intValueToByteValue(isTransparent, sourceBandData, novalue, anInt, true));
                        target.put(b);
                        if (!isTransparent[0] && transparentBand != null) {
                            transparentBand[posTarget] = (byte) 0xff;
                        }
                    }
                }
        } else {
            log.error(" 数据长度 未能处理数据类型 {}", dt);
        }
        return target;
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

//        if (imageRect.getWidth() > imageRect.getHeight()) {
//            // 长条状
//            int height = (int) ((1.0 * imageRect.getHeight() * targetWidth) / imageRect.getWidth());
//            targetRect.getYAsInt() = (targetHeight - height) / 2;
//            targetRect.getHeightAsInt() = (targetHeight - 2 * targetRect.getYAsInt());
//        } else {
//            //竖条装
//            int width = (int) ((1.0 * imageInfo.getWidth() * targetHeight) / imageRect.getHeight());
//            targetRect.getXAsInt() = (targetWidth - width) / 2;
//            targetRect.getWidthAsInt() = (targetWidth - 2 * targetRect.getXAsInt());
//        }

        //准备波段数据
        //如果原始波段数 大于3  只取前三个波段进行输出
        //如果原始波段数 小于3  用最后一个波段填充其余的波段
        // 构造三个波段进行处理
        List<BandData> sourceBandList = new ArrayList<>(3);
        List<Band> targetBandList = new ArrayList<>(3);

        processBands(imageInfo, sourceDataset, targetDataset, sourceBandList, targetBandList);
        byte[] transparentBand = getBlackBuffer(targetWidth, targetHeight);
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
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), targetWidth);
                targetBand.WriteRaster_Direct(targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getHeightAsInt(), targetRect.getHeightAsInt(), byteBuffer);
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
     * @param tileSize
     * @param targetRect
     * @param sourceRect
     * @param sourceBandList
     * @param targetBandList
     * @return
     */
    public byte[] getBand(boolean singleBand, int tileSize, Rect targetRect, Rect sourceRect, List<BandData> sourceBandList, List<Band> targetBandList, byte[] replaceColor) {
        byte[] transparentBand = getBlackBuffer(tileSize, tileSize);

        BandData source1 = sourceBandList.get(0);
        if (singleBand) {
            //灰度影像 使用颜色表进行替换操作
            BandData sourceBand = source1;

            if (sourceBand.getInfo().getNoValues() == null || sourceBand.getInfo().getNoValues().length == 0) {
                Double[] noValue = new Double[1];
                noValue[0] = 0.0;
                sourceBand.getInfo().setNoValues(noValue);
            }

            //这里的 sourceData 就是一个原始数据类型的值数组
            ByteBuffer sourceData = readSourceDataNoTranslate(
                    transparentBand, sourceBand,
                    sourceRect.getXAsInt(), sourceRect.getYAsInt(),
                    sourceRect.getWidthAsInt(), sourceRect.getHeightAsInt(),
                    targetRect.getXAsInt(), targetRect.getYAsInt(),
                    targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), tileSize);

            log.info(sourceData.toString());
            //RGB 通道 用于替换颜色
            ByteBuffer[] sourceBuffer = new ByteBuffer[3];
            sourceBuffer[0] = ByteBuffer.allocateDirect(tileSize * tileSize);
            sourceBuffer[1] = ByteBuffer.allocateDirect(tileSize * tileSize);
            sourceBuffer[2] = ByteBuffer.allocateDirect(tileSize * tileSize);

            //根据读出的值 用颜色表替换
            // sourceData.position(0); //原始影像
            int dt = sourceBand.getBand().GetRasterDataType();
            //循环处理每一个像素
            sourceData.order(ByteOrder.nativeOrder());
            for (int row = 0; row < targetRect.getHeightAsInt(); row++) {
                for (int col = 0; col < targetRect.getWidthAsInt(); col++) {
                    int location = row * targetRect.getWidthAsInt() + col;
                    int tilePosition = (targetRect.getYAsInt() + row) * tileSize + targetRect.getXAsInt() + col;
                    double pixelValue = 0;
                    if (dt == GDT_Byte) {
                        pixelValue = sourceData.get(location) & 0xFF;
                    } else if (dt == GDT_Int16 || dt == GDT_UInt16) {
                        pixelValue = sourceData.asShortBuffer().get(location) & 0xFFFF;
                    } else if (dt == GDT_Int32 || dt == GDT_UInt32) {
                        pixelValue = sourceData.asIntBuffer().get(location);
                    } else if (dt == GDT_Float32) {
                        pixelValue = sourceData.asFloatBuffer().get(location);
                    } else if (dt == GDT_Float64) {
                        pixelValue = sourceData.asDoubleBuffer().get(location);
                    }
                    int rgba;
                    if (replaceColor == null || replaceColor.length < 3) {
                        //没有设置替换颜色 使用颜色表
                        rgba = translateColor(pixelValue);
                        sourceBuffer[0].put((byte) (Colors.r(rgba) & 0xFF));
                        sourceBuffer[1].put((byte) (Colors.g(rgba) & 0xFF));
                        sourceBuffer[2].put((byte) (Colors.b(rgba) & 0xFF));
                        //使用颜色表中的透明色
                        transparentBand[tilePosition] = (byte) (Colors.a(rgba) & 0xFF);

                    } else {
                        sourceBuffer[0].put(replaceColor[0]);
                        sourceBuffer[1].put(replaceColor[1]);
                        sourceBuffer[2].put(replaceColor[2]);
                        if ((transparentBand[tilePosition] & 0xFF) == 0x00) {
                            // 对于文件中设置的无效值 仍然采用透明色处理
                        } else {
                            //使用颜色表中的透明色
                            if (replaceColor.length >= 4) {
                                transparentBand[tilePosition] = replaceColor[3];
                            } else {
                                transparentBand[tilePosition] = (byte) 0xFF;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
                Band targetBand = targetBandList.get(i);
                //读出原数据
                //写出目标文件
                if (targetRect.getXAsInt() != 0
                        || targetRect.getYAsInt() != 0
                        || targetRect.getWidthAsInt() != tileSize
                        || targetRect.getHeightAsInt() != tileSize) {
                    //用白色清空目标区域
                    targetBand.WriteRaster(0, 0, tileSize, tileSize, GlobalMercator.get().getWhiteBand());
                }
                targetBand.WriteRaster_Direct(targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), sourceBuffer[i]);
            }
        } else {
            for (int bandIndex = 0; bandIndex < 3; bandIndex++) {

                BandData sourceBand = sourceBandList.get(bandIndex);
                Band targetBand = targetBandList.get(bandIndex);
                //读出原数据
                //写出目标文件
                if (targetRect.getXAsInt() != 0
                        || targetRect.getYAsInt() != 0
                        || targetRect.getWidthAsInt() != tileSize
                        || targetRect.getHeightAsInt() != tileSize) {
                    //用白色清空目标区域
                    targetBand.WriteRaster(0, 0, tileSize, tileSize, GlobalMercator.get().getWhiteBand());
                }

                //这里的操作会拉伸影像
                //这里的操作会拉伸影像
                ByteBuffer byteBuffer = readAndTranslateToBytes256(
                        transparentBand, sourceBand,
                        sourceRect.getXAsInt(), sourceRect.getYAsInt(),
                        sourceRect.getWidthAsInt(), sourceRect.getHeightAsInt(),
                        targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), tileSize);
                //  log.info("target {}", targetRect);
                targetBand.WriteRaster_Direct(targetRect.getXAsInt(), targetRect.getYAsInt(),
                        targetRect.getWidthAsInt(), targetRect.getHeightAsInt(), byteBuffer);
            }
        }
        return transparentBand;
    }
}
