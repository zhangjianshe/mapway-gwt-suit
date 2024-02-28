package cn.mapway.common.geo.tools.parser;

import cn.mapway.geo.client.raster.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.Xmls;
import org.nutz.lang.util.NutMap;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Date;

/**
 * GF1Parser
 * 对高分1卫星影像进行数据采集
 *
 * @author zhang
 */
@Slf4j
public class GF1Parser implements ISatelliteExtractor {
    @Override
    public boolean extract(String fileName, ImageInfo imageInfo) {
        if (imageInfo == null) return false;

        String majorName = Files.getMajorName(fileName);
        if (!majorName.matches("^[Gg][Ff]1[BCD]?_")) {
            log.warn("{} is not a GF1 file", fileName);
            return false;
        }
        String metaFileName = Files.getParent(fileName) + Files.getMajorName(fileName) + ".meta.xml";
        File file = new File(metaFileName);
        if (file.exists()) {
            Document xml = Xmls.xml(file);
            NutMap nutMap = Xmls.asMap(xml.getDocumentElement());
            String satellite = nutMap.getString("ProductMeta.SatelliteInfo.SatelliteID", "");
            String sensor = nutMap.getString("ProductMeta.SatelliteInfo.SensorID", "");
            String level = nutMap.getString("ProductMeta.ProductInfo.ProductLevel", "");
            // 2021-06-05T03:30:20.006655
            Date captureTime = null;
            String temp = nutMap.getString("ProductMeta.SatelliteInfo.CentreTime", "");
            if (temp != null && temp.length() > 0) {
                temp = temp.replaceAll("T", " ");
                captureTime = Times.D(temp);
            }
            imageInfo.setSatellite(satellite);
            imageInfo.setSensor(sensor);
            imageInfo.setCaptureTime(captureTime);
            imageInfo.setLevel(level);
            return true;
        }


        //最后根据名字进行猜测
        //GF1C_MSS1_017153_20210605_MY8L1_01_016_L1A.tiff
        String[] segments = Strings.split(Files.getName(fileName), false, false, '_', '-');
        if (segments.length > 7) {
            imageInfo.setSatellite(segments[0]);
            imageInfo.setSensor(segments[1]);
            imageInfo.setCaptureTime(Times.D(segments[3]));
            imageInfo.setLevel(segments[7]);
            return true;
        }
        return false;
    }
}
