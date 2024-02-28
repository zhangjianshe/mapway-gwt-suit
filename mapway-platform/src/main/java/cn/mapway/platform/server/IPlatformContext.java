package cn.mapway.platform.server;

/**
 * 平台服务的上下文
 */
public interface IPlatformContext {
    /**
     */
    void initialize();

    /**
     */
    void unInitialize();


    /**
     */
    void registerServerModule();
}
