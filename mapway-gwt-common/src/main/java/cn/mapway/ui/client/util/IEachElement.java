package cn.mapway.ui.client.util;

/**
 * 迭代元素接口
 * @param <T>
 */
@FunctionalInterface
public interface IEachElement<T> {
    boolean each(T e);
}
