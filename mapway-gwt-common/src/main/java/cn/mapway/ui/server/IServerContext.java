package cn.mapway.ui.server;

import cn.mapway.ui.client.IUserInfo;
import org.nutz.dao.Dao;

import java.util.Collection;

/**
 * 服务器的运行环境
 */
public interface IServerContext {
    /**
     * 获取容器中的Bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getBean(Class<T> clazz);

    <T> T getBeanByName(String name);

    Dao getManagerDao();

    /**
     * 加入缓存
     * @param key
     * @param value
     */
    void putToSession(String group,String key, Object value);

    /**
     * 清楚group 下所有的东西
     * @param group
     */
    void clearToSession(String group);

    /**
     * 从缓存中获取
     * @param key
     * @return
     */
    Object getFromSession(String group,String key);

    /**
     * 获取当前的登录用户
     *
     * @return
     */
    IUserInfo requestUser();

    /**
     * 需要扫描的package
     * @return
     */
    Collection<Class<?>> getScanPackages();

    /**
     * 超级管理员
     * @return
     */
    IUserInfo getSuperUser();

    /**
     * clear session cache by group
     * @param sessionCacheGroup
     */
    void clearSessionGroup(String sessionCacheGroup);

}
