package cn.mapway.geo.client.raster;

/**
 * ImageTools
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class ImageTools {
    /**
     * 18:14:27.269 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Unknown 0
     * 18:14:27.275 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Int8 14
     * 18:14:27.275 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Byte 1
     * 18:14:27.275 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_UInt16 2
     * 18:14:27.275 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Int16 3
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_UInt32 4
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Int32 5
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Float32 6
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_Float64 7
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_CInt16 8
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_CInt32 9
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_CFloat32 10
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_CFloat64 11
     * 18:14:27.276 [main] INFO cn.imagebot.geo.tools.TiffTools -  GDT_TypeCount 12
     *
     * @param dataType
     * @return
     */
    public static String getDataType(int dataType) {
        switch (dataType) {
            case 0:
                return "不知道，猜不出";
            case 1:
                return "8Bit";
            case 2:
                return "16bit 无符号";
            case 3:
                return "16bit 有符号";
            case 4:
                return "32bit 无符号";
            case 5:
                return "32bit 有符号";
            case 6:
                return "32bit 浮点数";
            case 7:
                return "64bit 浮点数";
            case 8:
                return "16bit 复数";
            case 9:
                return "32bit 整型复数";
            case 10:
                return "32bit 浮点复数";
            case 11:
                return "64bit 浮点复数";
            case 14:
                return "8bit 有符号";
            default:
                return "不知道";
        }
    }
}
