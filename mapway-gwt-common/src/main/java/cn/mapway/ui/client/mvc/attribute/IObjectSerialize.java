package cn.mapway.ui.client.mvc.attribute;

/**
 * 对象序列化接口
 */
public interface IObjectSerialize {
    Object fromStr(String data);

    String toStr();
}
