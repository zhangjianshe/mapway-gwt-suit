package cn.mapway.ui.client.mvc.decorator;

/**
 * ISelectable
 * 设置或者取消选择的接口
 *
 * @author zhang
 */
public interface ISelectable {
    String SELECT_ATTRIBUTE = "select";

    void setSelect(boolean select);
}
