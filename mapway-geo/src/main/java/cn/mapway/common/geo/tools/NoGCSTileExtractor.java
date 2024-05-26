package cn.mapway.common.geo.tools;

import cn.mapway.geo.client.raster.ImageInfo;
import cn.mapway.geo.shared.vector.Box;
import cn.mapway.geo.shared.vector.Point;
import cn.mapway.geo.shared.vector.Rect;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * NoGCSTileExtractor
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
public class NoGCSTileExtractor extends BaseTileExtractor implements ITileExtractor {

    GlobalMercator globalMercator = GlobalMercator.get();


    @Override
    public boolean extractTileToTarget(ImageInfo imageInfo, long tileX, long tileY, int zoom, Dataset sourceDataset, Dataset targetDataset) {

        int tileSize = globalMercator.tileSize;
        //目标tile的像素空间 初始化设置 下面会根据不同的情况进行调整
        int targetX = 0;
        int targetWidth = tileSize;
        int targetY = 0;
        int targetHeight = tileSize;
        //读取source数据 写入raster(左上角坐标为0,0)
        //  根据tile的经纬度坐标范围 找到 影像中的像素范围 如果像素范围超过了影像大小 说明
        //  这个tile 需要从原始影像拷贝一部分数据，并进行采样处理，他的目标像素范围也要变更

        // 获取瓦片对应的地理坐标范围（GPS）
        Box tileLngLatExtent = globalMercator.tileBounds(tileX, tileY, zoom);
        // 获取瓦片对应的原始影像的像素空间坐标 坐标原点在左上角，向右和向下递增
        Box tileImagePixelExtent = locationBoxPixelExtent(imageInfo.getGeoTransform(), tileLngLatExtent);

        // tile 对应的像素空间 初始化设置 下面会根据不同的情况进行调整
        int sourceX = (int) tileImagePixelExtent.getXmin();
        int sourceWidth = (int) tileImagePixelExtent.width();
        int sourceY = (int) tileImagePixelExtent.getYmin();
        int sourceHeight = (int) tileImagePixelExtent.height();
        if (sourceX <= -256 || sourceX >= imageInfo.getWidth() || sourceY <= -256 || sourceY >= imageInfo.getHeight()) {
            log.error("超出影像范围");
            return false;
        }
        //判断是否超出影像空间
        if (sourceX < 0) {
            //左边超出了影像范围 需要减少拷贝的数据
            sourceWidth = sourceWidth + sourceX;//原始影像的拷贝宽度
            sourceX = 0;//从左边0开始拷贝

            //目标的X方向开始位置，需要计算 找出原始影像经度所在瓦片的瓦片像素X坐标
            Point latToTile = globalMercator.geoToTile(imageInfo.getBox().xmin, imageInfo.getBox().ymax, tileX, tileY, zoom);
            targetX = (int) latToTile.getX();
            targetWidth = tileSize - targetX;//目标宽度
        }
        if (sourceY < 0) {
            //tile上边超出了影像范围
            //目标的Y方向开始位置，需要计算 找出原始影像经度所在瓦片的瓦片像素Y坐标 需要计算瓦片 右下角的经纬度对应在原始影像中的限速位置
            sourceY = 0; //从影像最上边拷贝
            //原始影像的拷贝高度 这个需要计算
            sourceHeight = (int) (tileImagePixelExtent.getYmax());

            Point latToTile = globalMercator.geoToTile(imageInfo.getBox().xmin, imageInfo.getBox().ymax, tileX, tileY, zoom);
            targetY = tileSize - (int) latToTile.getY();
            targetHeight = (int) latToTile.getY();
        }

        if (sourceY + sourceHeight > imageInfo.getHeight()) {
            //tile下面超出了影像范围 只需要拷贝原始影像最下面的部分影像
            sourceHeight = (int) imageInfo.getHeight() - sourceY;

            //目标的输出范围就应该是部分高度 不能铺满真个高度，具体目标tile内的高度是什么 需要计算 计算结果为右下角为坐标原点
            Point latToTile = globalMercator.geoToTile(imageInfo.getBox().xmin, imageInfo.getBox().ymin, tileX, tileY, zoom);
            targetHeight = tileSize - (int) latToTile.getY();
            targetY = 0;
        }
        //tile 右边超出了影像范围
        if (sourceX + sourceWidth > imageInfo.getWidth()) {
            sourceWidth = (int) (imageInfo.getWidth() - sourceX);
            // 坐标是以右下角为坐标原点
            Point latToTile = globalMercator.geoToTile(imageInfo.getBox().xmax, imageInfo.getBox().ymax, tileX, tileY, zoom);
            targetWidth = (int) latToTile.getX();
            targetX = 0;
        }

        //准备波段数据
        //如果原始波段数 大于3  只取前三个波段进行输出
        //如果原始波段数 小于3  用最后一个波段填充其余的波段
        // 构造三个波段进行处理
        List<BandData> sourceBandList = new ArrayList<>(3);
        List<Band> targetBandList = new ArrayList<>(3);

        processBands(imageInfo, sourceDataset, targetDataset, sourceBandList, targetBandList);
        Rect sourceRect = new Rect(sourceX, sourceY, sourceWidth, sourceHeight);
        Rect targetRect = new Rect(targetX, targetY, targetWidth, targetHeight);
        byte[] transparentBand = getBand(imageInfo.bandInfos.size()==1, tileSize, targetRect, sourceRect, sourceBandList, targetBandList,imageInfo.getSingleColor());
        if (targetDataset.getRasterCount() == 4) {
            targetDataset.GetRasterBand(4).WriteRaster(0, 0, 256, 256, transparentBand);
        }
        //写完了一个tile 直接输出到 .S文件中去吧
        targetDataset.FlushCache();
        return true;
    }


}
