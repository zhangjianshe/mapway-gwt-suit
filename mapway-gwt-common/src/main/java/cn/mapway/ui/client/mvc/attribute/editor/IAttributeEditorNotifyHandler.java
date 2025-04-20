package cn.mapway.ui.client.mvc.attribute.editor;

/**
 * 属性编辑器的通知事件
 * 当属性编辑器　需要通知属性编辑器本身的状态变化时 会调用这个接口
 */
public interface IAttributeEditorNotifyHandler {
    void handlerEditorNotify(NotifyKind kind,Object data);
}
