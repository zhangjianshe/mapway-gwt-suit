package cn.mapway.ui.client.tools;

/**
 * IShowMessage
 * 定义了显示消息的接口
 * @author zhangjianshe@gmail.com
 */
public interface IShowMessage {
    /**
     * 显示一个消息
     * @param level 显示级别 0,1,2,3,4
     * @param message 显示的消息
     */
    void showMessage(int level, Integer code, String message);
}
