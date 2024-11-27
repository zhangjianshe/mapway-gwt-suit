package cn.mapway.ui.client;

import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;
import elemental2.core.JsArray;
import elemental2.promise.Promise;


/**
 * 浏览器中的运行环境
 */
public interface IClientContext  {
    /**
     * 获取当前登录的用户信息
     * @return
     */
    IUserInfo getUserInfo();
    void setUserInfo(IUserInfo userInfo);
    Promise<Void> confirmDelete(String message);

    Promise<Void> confirm3(ImageResource icon, String title, String message);

    Promise<Void> confirm2(String title, String message);

    Promise<Void> confirm(String message);

    /**
     * 输入一个值
     *
     * @param dialogTitle
     * @param tip
     * @param placeHolder
     * @param initValue
     * @param callback
     */
    void input(String dialogTitle, String tip, String placeHolder, String initValue, Callback callback);

    /**
     * 输入一个值
     *
     * @param dialogTitle
     * @param tip
     * @param placeHolder
     * @param initValue
     * @param callback
     */
    void inputPassword(String dialogTitle, String tip, String placeHolder, String initValue, Callback callback);

    void waiting(String info);

    void hideWaiting();

    void logout();

    /**
     * 弹框提示用户信息
     *
     * @param message
     */
    void info(String message);

    /**
     * 全局弹出消息框，本消息框自动2S后消失
     *
     * @param level
     * @param code
     * @param message
     */
    void toast(int level, Integer code, String message);

    /**
     * 弹出提示框
     *
     * @param message
     */
    void alert(String message);

    void fireEvent(GwtEvent <?> event);


    /**
     * 全局提供一个选择用户对话框
     * @return
     */
     Promise<JsArray<IUserInfo>> chooseUser();
}
