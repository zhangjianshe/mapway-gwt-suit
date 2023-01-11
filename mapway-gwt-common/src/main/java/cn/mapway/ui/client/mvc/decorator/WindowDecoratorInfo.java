package cn.mapway.ui.client.mvc.decorator;

/**
 * WindowDecoratorInfo
 * 窗口装饰器信息
 * @author zhang
 */
public class WindowDecoratorInfo {
    public String name;
    public String unicodeIcon;
    public String summary;
    public WindowDecoratorInfo()
    {

    }
    public WindowDecoratorInfo(String name,String unicodeIcon,String summary)
    {
        this.name=name;
        this.summary=summary;
        this.unicodeIcon=unicodeIcon;
    }
}
