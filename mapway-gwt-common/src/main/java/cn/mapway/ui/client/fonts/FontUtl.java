package cn.mapway.ui.client.fonts;

import elemental2.dom.DomGlobal;
import elemental2.dom.FontFace;
import elemental2.promise.Promise;

/**
 * 字体工具类
 * 本系统需要 一个 mapway-font的字体库
 * 有两种方式加载
 * 1. 在html中设置以下代码
 * <code>
 *      @font-face {
 *             font-family: 'mapway-font';
 *             font-style: normal;
 *             font-weight: 400;
 *             src: url(./font/iconfont.woff2) format('woff2');
 *         }
 * </code>
 * 2. 使用 FontUtil.loadFont("mapway-font",${url});
 */
public class FontUtl {
    /**
     * 文档加载一个WEB Font字体
     * @param faceName 字体名称
     * @param url 字体Url
     * @return
     */
    public static Promise<FontFace> loadFont(String faceName,String url)
    {
        FontFace face=new FontFace(faceName,url);
        DomGlobal.document.fonts.add(face);
        return face.load();
    }
}
