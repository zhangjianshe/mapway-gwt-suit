package cn.mapway.ui.client.mvc.window;

/**
 * ISelectable
 * 设置或者取消选择的接口
 *
 * @author zhang
 */
public interface IEnabled {
    String ENABLED_ATTRIBUTE = "enabled";
    void setEnabled(boolean enabled);
    boolean getEnabled();
}
