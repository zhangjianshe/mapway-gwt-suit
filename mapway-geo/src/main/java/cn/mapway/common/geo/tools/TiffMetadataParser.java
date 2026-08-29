package cn.mapway.common.geo.tools;

import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Dataset;
import org.nutz.lang.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiffMetadataParser {

    /**
     * 拍摄时间
     */
    public static final String KEY_CAPTURE_TIME = "date";

    /**
     * 县代码
     */
    public static final String KEY_COUNTY_CODE = "country";
    /**
     * 县名称
     */
    public static final String KEY_COUNTY_NAME = "qxmc";
    /**
     * 省代码
     */
    public static final String KEY_PROVINCE_CODE = "sdm";
    /**
     * 省名称
     */
    public static final String KEY_PROVINCE_NAME = "smc";
    /**
     * 分辨率
     */
    public static final String KEY_RESOLUTION = "resolution";
    /**
     * 传感器
     */
    public static final String KEY_SENSOR = "sensor";
    /**
     * 卫星名称
     */
    public static final String KEY_SATELLITE = "satellite";
    /**
     * 通道数
     */
    public static final String KEY_BAND_NUM = "bandnum";
    /**
     * 波段顺序， 没有就默认 BGRNRe
     */
    public static final String KEY_BAND_ORDER = "bandorder";

    /**
     * tif内label存放的名空间
     */
    public static final String SPACE_CUSTOM = "CUSTOM";



    private static final Pattern RESOLUTION_NUMBER_PATTERN = Pattern.compile("[-+]?\\d+(?:\\.\\d+)?(?:[eE][-+]?\\d+)?");



    public static String getCaptureTime(Dataset dataset){
        return dataset.GetMetadataItem(KEY_CAPTURE_TIME, SPACE_CUSTOM);
    }

    public static String getCountyCode(Dataset dataset){
        return dataset.GetMetadataItem(KEY_COUNTY_CODE, SPACE_CUSTOM);
    }

    public static Double getResolution(Dataset dataset){
        String value = dataset.GetMetadataItem(KEY_RESOLUTION, SPACE_CUSTOM);
        if (Strings.isBlank(value)) {
            return null;
        }
        Matcher matcher = RESOLUTION_NUMBER_PATTERN.matcher(value.trim());
        if (!matcher.find()) {
            return null;
        }
        try {
            double resolution = Double.parseDouble(matcher.group());
            return Double.isFinite(resolution) && resolution > 0D
                    ? resolution : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Integer getBandNum(Dataset dataset){
        String bandNum = dataset.GetMetadataItem(KEY_BAND_NUM, SPACE_CUSTOM);
        if(StringUtils.isNotBlank(bandNum)){
            try{
                return Integer.valueOf(bandNum);
            }catch(NumberFormatException e){
                return null;
            }
        }
        return null;
    }

    public static String getBandOrder(Dataset dataset){
        return dataset.GetMetadataItem(KEY_BAND_ORDER, SPACE_CUSTOM);
    }

    public static String getSensor(Dataset dataset){
        return dataset.GetMetadataItem(KEY_SENSOR, SPACE_CUSTOM);
    }

    public static String getSatellite(Dataset dataset){
        return dataset.GetMetadataItem(KEY_SATELLITE, SPACE_CUSTOM);
    }


    public static String getCountyName(Dataset dataset){
        return dataset.GetMetadataItem(KEY_COUNTY_NAME, SPACE_CUSTOM);
    }

    public static String getProvinceCode(Dataset dataset){
        return dataset.GetMetadataItem(KEY_PROVINCE_CODE, SPACE_CUSTOM);
    }

    public static String getProvinceName(Dataset dataset){
        return dataset.GetMetadataItem(KEY_PROVINCE_NAME, SPACE_CUSTOM);
    }


    private String trimMetadata(String value) {
        return Strings.isBlank(value) ? null : value.trim();
    }


}
