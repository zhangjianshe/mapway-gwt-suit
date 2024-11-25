package cn.mapway.ui.server;

import org.nutz.dao.Dao;

/**
 * 服务器的运行环境
 */
public interface IServerContext {
    /**
     * 获取容器中的Bean
     * @param clazz
     * @return
     * @param <T>
     */
     <T> T getBean(Class<T> clazz);
     <T> T getBeanByName(String name);
     Dao getManagerDao();

     void putToSession(String key, Object value);
     Object getFromSession(String key);
}
