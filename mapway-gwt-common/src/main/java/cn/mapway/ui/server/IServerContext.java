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

    void putToSession(String key, Object value);

    Object getFromSession(String key);

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
}
