package cn.mapway.ui.client.mvc.window;

import cn.mapway.ui.client.mvc.Size;

/**
 * IProvideSize
 * 实现这个接口的窗口 可以提供一个缺省的大小供父窗口进行大小调整
 *
 * @author zhang
 */
public interface IProvideSize {
    /**
     * @return
     */
    Size requireDefaultSize();
}
