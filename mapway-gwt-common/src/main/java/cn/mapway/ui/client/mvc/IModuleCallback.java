package cn.mapway.ui.client.mvc;

import cn.mapway.ui.shared.CommonEvent;

/**
 * 用于模块向外expose事件
 */
public interface IModuleCallback {
    Integer callback(IModule module, CommonEvent event);
}
