package cn.mapway.platform.server;

/**
 * 平台服务的上下文
 */
public interface IPlatformContext {
    /**
     * 平台初始化
     */
    void initialize();

    /**
     * 平台结束
     */
    void unInitialize();


    /**
     * 注册一个服务端模块
     */
    void registerServerModule();
}
