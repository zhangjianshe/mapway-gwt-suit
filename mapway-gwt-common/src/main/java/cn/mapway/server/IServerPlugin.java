package cn.mapway.server;

import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.server.IServerContext;

/**
 * 服务器端模块
 */
public interface IServerPlugin {
    String getName();
    String version();
    String author();

    /**
     *　　初始化模块
     * @param context
     */
    void init(IServerContext context);

    /**
     * 注销模块
     * @param context
     */
    void destroy(IServerContext context);

    /**
     * 服务插件获取当前请求的用户信息
     * @return
     */
    IUserInfo requestUser();
}
