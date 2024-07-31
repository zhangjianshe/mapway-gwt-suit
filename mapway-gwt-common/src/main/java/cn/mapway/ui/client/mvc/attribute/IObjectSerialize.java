package cn.mapway.ui.client.mvc.attribute;

/**
 * 对象序列化接口
 */
public interface  IObjectSerialize<T> {
    T fromStr(String data);
    String toStr();
}
