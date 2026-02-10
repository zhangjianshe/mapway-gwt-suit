package cn.mapway.ui.client.util;

/**
 * 迭代元素接口
 * @param <T>
 */
@FunctionalInterface
public interface IEachElement<T> {
    /**
     * 返回　true 继续下一个元素
     * 　　　false 终止迭代
     * @param e
     * @return
     */
    boolean each(T e);
}
