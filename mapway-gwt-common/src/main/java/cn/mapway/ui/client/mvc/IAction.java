package cn.mapway.ui.client.mvc;

/**
 * 执行动作的统一接口
 */
public interface IAction {
    /**
     * 保存的动作
     */
    Integer ACTION_SAVE=0;
    /**
     *
     * @param action action动作类型
     * @param param  action动作参数
     */
    void doAction(Integer action,Object param);
}
