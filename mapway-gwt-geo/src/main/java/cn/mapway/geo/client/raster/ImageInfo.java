package cn.mapway.geo.client.raster;


import cn.mapway.geo.shared.vector.Box;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ImageInfo
 * 此对象描述一个影像的信息  这个影像可能为 TIFF GEOIMage 或者一个目录
 * 如果描述一个镶嵌影像 （目录）  (name location minZoom maxZoom copyright lng lat type)
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class ImageInfo implements Serializable, IsSerializable {
    public String sha256;

    public String name;

    public String location;
    public double lng;
    public double lat;
    public int minZoom;
    public int maxZoom;
    public String copyright;
    // 影像类型   0  目录 1 文件
    public int type =0;
    /**
     * 分辨率 以cm为单位
     */
    public Integer resolution;
    /**
     * 波段数量
     */
    public int bands = 0;
    public long width = 0;
    public long height = 0;
    /**
     * 投影信息
     */
    public String projection;
    /**
     * 影像包围盒 WGS84
     */
    public Box box;
    /**
     * 原始坐标参考下的包围盒
     */
    public Box sourceBox;
    /**
     * 文件长度
     */
    public Long fileSize;

    /**
     * epsg定义的坐标参考系标识
     */
    public int srid;

    /**
     * 数据生成的日期 格式 yyyy-MM-dd HH:mm:ss 2013-09-07 13:24:12
     */
    public String dataTime;

    public double[] geoTransform;

    public Integer autoCreate;

    public List<BandInfo> bandInfos;
    //RGB  通道数据
    public ChanelData chanelData;

    /**
     * 影像对应的URI location值可以从这个字段推导出来
     */
    public String uri;

    /**
     * 卫星名称
     */
    String satellite;
    /**
     * 传感器平台
     */
    String sensor;

    /**
     * 成像时间
     */
    Date captureTime;

    /**
     * 数据产品的Level
     */
    String level;

    /**
     * 单波段 二值影像 可以设定一个rgb 颜色值 用于渲染输出
     */
    byte[] singleColor;

    public ImageInfo() {
        bandInfos = new ArrayList<>();
        singleColor = null;
    }


    /**
     *
     * @param chanelIndex  1,2,3
     * @return
     */
    public BandInfo findBand(int chanelIndex) {
        for(BandInfo bandInfo:bandInfos)
        {
            if(bandInfo.index==chanelIndex-1)
            {
                return bandInfo;
            }
        }
        return null;
    }
}
