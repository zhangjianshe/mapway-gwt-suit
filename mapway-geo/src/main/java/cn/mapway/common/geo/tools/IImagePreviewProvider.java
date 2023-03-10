package cn.mapway.common.geo.tools;

/**
 * IImagePreviewProvider
 * 影像预览接口标准
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public interface IImagePreviewProvider {
    byte[] read(String sha256);

    boolean write(String sha256, byte[] image);
}
