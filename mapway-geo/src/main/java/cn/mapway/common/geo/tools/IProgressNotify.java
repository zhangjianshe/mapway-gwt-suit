package cn.mapway.common.geo.tools;

/**
 * IProgressNotify
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public interface IProgressNotify {
    /**
     * 向用户 userId 发送通用 task_message 消息
     *
     * @param userId   用户ID
     * @param subTopic 主题
     * @param phase    阶段名称
     * @param type     消息类型
     * @param message  消息内容
     * @param progress 消息进度 0-100
     */
    void notify(Long userId,String subTopic, int phase, Integer type, String message, int progress);
}
