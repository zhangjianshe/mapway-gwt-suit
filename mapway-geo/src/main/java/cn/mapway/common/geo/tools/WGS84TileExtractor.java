package cn.mapway.common.geo.tools;


import cn.mapway.geo.client.raster.ImageInfo;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import cn.mapway.ui.client.mvc.Size;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import java.util.ArrayList;
import java.util.List;

/**
 * WGS84TileExtractor
 * 将原始影像 坐标参考系为地理坐标系 WGS84的影像中 提取出按照WebMecator规定的瓦片数据
 * <p>
 * // 1.找到左上角瓦片编号  某个级别 的像素数以及图像的像素数 找瓦片
 * // 2.顺序进行瓦片输出 将原始影像拷贝到目标瓦片 写入.S文件
 * <p>
 * //先创建一个目标文件 512*512  这个文件在内存中 (每个波段都以byte存储的数据) 这个文件的波段数和原始影像保持一枝
 * // GDAL中的影像坐标为 左上角为 (0,0)
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Slf4j
public class WGS84TileExtractor extends BaseTileExtractor implements ITileExtractor {


    GlobalMercator globalMercator = GlobalMercator.get();

    @Override
    public boolean extractTileToTarget(ImageInfo imageInfo, long tileX, long tileY, int zoom, Dataset sourceDataset, Dataset targetDataset) {
        //瓦片宽度和高度 一般为256个像素
        int tileSize = globalMercator.tileSize;

        //目标tile的像素空间 初始化设置 下面会根据不同的情况进行调整
        Rect targetRect = new Rect(0, 0, tileSize, tileSize);
        System.out.println("[INFO] Tile " + tileX + " " + tileY + " " + zoom);
        // 获取瓦片对应的地理坐标范围（GPS）
        Box tileLngLatExtent = globalMercator.tileBoundWgs84(tileX, tileY, zoom);
        System.out.println("[INFO] Tile WGS84  " + tileLngLatExtent.toString());
        System.out.println("----->");
        Box tileImagePixelExtent = locationBoxPixelExtentFromWgs84(sourceDataset, tileLngLatExtent);
        System.out.println("<-----");
        System.out.println("[INFO] Tile IMAGE  " + tileImagePixelExtent.toString());


        Rect imageRect = new Rect(0, 0, (int) imageInfo.width, (int) imageInfo.height);
        // tile 对应的像素空间 初始化设置 下面会根据不同的情况进行调整
        Rect sourceRect = new Rect((int) tileImagePixelExtent.getXmin(), (int) tileImagePixelExtent.getYmax(),
                (int) tileImagePixelExtent.width(), (int) -tileImagePixelExtent.height());
        // log.info("tile image extent {}", sourceRect);
        // 处理时 主要分为两种情况 1.tile小于图片范围 2.tile大于图片范围
        // [sourceRect targetRect]   sourceRect   并不是影像坐标，而是tile对应的影像坐标 可能超出影像范围
        //  先判断是否超出了影像范围
        if (!sourceRect.isCross(imageRect)) {
            //  log.error("超出影像范围 {}", sourceRect);
            return false;
        }

        //目标的X方向开始位置，需要计算 找出原始影像经度所在瓦片的瓦片像素X坐标
        Point leftTop = globalMercator.geoToTile(imageInfo.getBox().xmin, imageInfo.getBox().ymax, tileX, tileY, zoom);
        Point rightBottom = globalMercator.geoToTile(imageInfo.getBox().xmax, imageInfo.getBox().ymin, tileX, tileY, zoom);

        //先处理 X方向  sourceRect imageRect  都是以影像坐上角为坐标原点的直角坐标系统的坐标
        if (sourceRect.getWidth() > imageRect.getWidth()) {
            //瓦片包含了整个影像 包含一下这三种情况
            //              T<----------------------->T                  sourceRect
            //      I<---------->I                                       imageRect

            //      T<----------------------->T                          sourceRect
            //                           I<---------->I                  imageRect

            //      T<----------------------->T                          sourceRect
            //               I<---------->I                              imageRect
            //目标区域 也就是瓦片区域需要计算

            if (sourceRect.x > imageRect.x
                    && sourceRect.x < imageRect.x + imageRect.getWidth()
                    && imageRect.x + imageRect.getWidth() < sourceRect.x + sourceRect.getWidth()
            ) {
                //情况1
                sourceRect.x = sourceRect.x;
                sourceRect.width = imageRect.getWidth() - sourceRect.x;
                targetRect.x = 0;
                targetRect.width = (int) rightBottom.getX();
            } else if ((sourceRect.x + sourceRect.getWidth()) > imageRect.x
                    && (sourceRect.x + sourceRect.getWidth()) < imageRect.x + imageRect.getWidth()
                    && sourceRect.x < imageRect.x
            ) {//情况2
                sourceRect.width = sourceRect.getWidth() + sourceRect.x;
                sourceRect.x = 0;
                targetRect.x = (int) leftTop.getX();
                targetRect.width = tileSize - targetRect.x;
            } else {
                //情况3
                sourceRect.x = 0;
                sourceRect.width = imageRect.getWidth();
                targetRect.x = (int) leftTop.getX();
                targetRect.width = (int) (rightBottom.getX() - leftTop.getX());
            }
        } else {
            // 瓦片小于影像宽度
            //      T<---------->T                                      sourceRect
            //             I<-------------------------->I               imageRect

            //                                   T<--------->T          sourceRect
            //             I<-------------------------->I               imageRect

            //                 T<----------->T                          sourceRect
            //             I<------------------------->I                imageRect
            //目标区域 也就是瓦片区域需要计算
            if (sourceRect.x < imageRect.x
                    && sourceRect.x + sourceRect.getWidth() > imageRect.x
                    && sourceRect.x + sourceRect.getWidth() < imageRect.x + imageRect.getWidth()
            ) {
                //情况1
                sourceRect.width = sourceRect.x + sourceRect.getWidth();
                sourceRect.x = 0;
                targetRect.x = (int) leftTop.getX();
                targetRect.width = tileSize - targetRect.x;
            } else if (sourceRect.x < (imageRect.x + imageRect.getWidth())
                    && sourceRect.x + sourceRect.getWidth() > (imageRect.x) + imageRect.getWidth()
                    && imageRect.x < sourceRect.x
            ) {//情况2
                sourceRect.x = sourceRect.getX();
                sourceRect.width = imageRect.getWidth() - sourceRect.x;
                targetRect.x = 0;
                targetRect.width = (int) rightBottom.getX();
            } else {
                //情况3
                sourceRect.x = sourceRect.getX();
                sourceRect.width = sourceRect.getWidth();
                targetRect.x = 0;
                targetRect.width = tileSize;
            }
        }
        //
        if (sourceRect.getHeight() > imageRect.getHeight()) {
            //瓦片包含了整个影像 包含一下这三种情况
            //     sourceRect imageRect      sourceRect imageRect
            //   (3) T  I               (1)T I         (2) T I
            //       ┯                       ┯             ┯
            //       │                       │             │
            //       │  ┯                  ┯ │             │
            //       │  │                  │ │             │
            //       │  │                  │ ┷             │ ┯
            //       │  │                  │               │ │
            //       │  ┷                  │               │ │
            //       ┷                     │               ┷ │
            //                             ┷                 ┷
            if ((imageRect.y + imageRect.getHeight()) > sourceRect.getY()
                    && (imageRect.y + imageRect.getHeight()) < (sourceRect.getY() + sourceRect.getHeight())
                    && (sourceRect.y > imageRect.y)
            ) {
                //情况1
                sourceRect.y = sourceRect.getY();
                sourceRect.height = imageRect.getHeight() - sourceRect.y;
                targetRect.y = 0;
                targetRect.height = (int) rightBottom.getY();
            } else if (sourceRect.y + sourceRect.height > imageRect.y &&
                    sourceRect.y + sourceRect.height < imageRect.y + imageRect.height
                    && (sourceRect.y < imageRect.y)
            ) {
                //情况2
                sourceRect.height = sourceRect.y + sourceRect.height;
                sourceRect.y = 0;
                targetRect.y = (int) leftTop.getY();
                targetRect.height = tileSize - targetRect.getY();
            } else {
                //情况3
                sourceRect.y = 0;
                sourceRect.height = imageRect.getHeight();
                targetRect.y = (int) leftTop.getY();
                targetRect.height = (int) (rightBottom.getY() - leftTop.getY());
            }
        } else {
            //瓦片包含了整个影像 包含一下这三种情况
            //     sourceRect imageRect      sourceRect imageRect
            //   (3) T  I               (1)T I         (2) T I
            //          ┯                    ┯             ┯
            //          │                    │             │
            //          │                    │             │ ┯
            //       ┯  │                    │             ┷ │
            //       │  │                  ┯ │               │
            //       │  │                  │ │               │
            //       ┷  │                  │ ┷               │
            //          ┷                  ┷                 ┷
            //目标区域 也就是瓦片区域需要计算
            if ((imageRect.y + imageRect.getHeight() > sourceRect.y)
                    && (imageRect.y + imageRect.getHeight() < sourceRect.y + sourceRect.getHeight())
                    && (sourceRect.y > imageRect.y)
            ) {
                //情况1
                sourceRect.y = sourceRect.y;
                sourceRect.height = imageRect.getHeight() - sourceRect.y;
                targetRect.y = 0;
                targetRect.height = (int) rightBottom.getY();
            } else if (imageRect.y > sourceRect.y
                    && imageRect.y < sourceRect.y + sourceRect.getHeight()
                    && (sourceRect.y + sourceRect.getHeight() < imageRect.y + imageRect.getHeight())
            ) {
                //情况2
                sourceRect.height = sourceRect.y + sourceRect.height;
                sourceRect.y = 0;
                targetRect.y = (int) leftTop.getY();
                targetRect.height = tileSize - targetRect.y;
            } else {
                //情况3
                sourceRect.y = sourceRect.y;
                sourceRect.height = sourceRect.height;
                targetRect.y = 0;
                targetRect.height = tileSize;
            }
        }

        //准备波段数据
        //如果原始波段数 大于3  只取前三个波段进行输出
        //如果原始波段数 小于3  用最后一个波段填充其余的波段
        // 构造三个波段进行处理
        List<BandData> sourceBandList = new ArrayList<>(3);
        List<Band> targetBandList = new ArrayList<>(3);

        boolean singleBand=processBands(imageInfo, sourceDataset, targetDataset, sourceBandList, targetBandList);
        byte[] transparentBand = getBand(singleBand, new Size(256, 256), targetRect, sourceRect, sourceBandList, targetBandList);
        if (targetDataset.getRasterCount() == 4) {
            targetDataset.GetRasterBand(4).WriteRaster(0, 0, 256, 256, transparentBand);
        }
        //写完了一个tile 直接输出到 .S文件中去吧
        targetDataset.FlushCache();
        return true;
    }
}
