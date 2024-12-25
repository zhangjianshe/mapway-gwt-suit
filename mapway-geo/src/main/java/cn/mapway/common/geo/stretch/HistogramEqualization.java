package cn.mapway.common.geo.stretch;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HistogramEqualization {
    public static void main(String[] args) {
        try {
            // 读取图像
            BufferedImage image = ImageIO.read(new File("F:\\data\\cis\\labels\\sample_145\\image\\15623.tif"));

            // 获取图像的宽度和高度
            int width = image.getWidth();
            int height = image.getHeight();

            // 计算图像的直方图
            int[] histogram = new int[256];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y) & 0xFF;
                    histogram[pixel]++;
                }
            }

            // 计算累积直方图
            int[] cumulativeHistogram = new int[256];
            cumulativeHistogram[0] = histogram[0];
            for (int i = 1; i < 256; i++) {
                cumulativeHistogram[i] = cumulativeHistogram[i - 1] + histogram[i];
            }

            // 计算直方图均衡化后的像素值
            int totalPixels = width * height;
            int[] equalizedPixels = new int[256];
            for (int i = 0; i < 256; i++) {
                equalizedPixels[i] = (cumulativeHistogram[i] * 255) / totalPixels;
            }

            // 创建新的均衡化图像
            BufferedImage equalizedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y) & 0xFF;
                    int newPixel = equalizedPixels[pixel];
                    int rgb = (newPixel << 16) | (newPixel << 8) | newPixel;
                    equalizedImage.setRGB(x, y, rgb);
                }
            }

            // 保存均衡化后的图像
            ImageIO.write(equalizedImage, "jpg", new File("F:\\data\\temp\\1.tif"));

            System.out.println("直方图均衡化处理完成！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
