package cn.mapway.ui.client.tools;

/**
 * IData
 * 一个对象如果能够获取数据和接受数据，就可以实现此接口
 * @author zhangjianshe@gmail.com
 */
public interface  IData <T> {
     T getData();
     void setData(T obj);
}
