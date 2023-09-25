package cn.mapway.ui.client.frame;

import cn.mapway.ui.client.event.EventBus;
import cn.mapway.ui.client.event.IEventHandler;
import cn.mapway.ui.client.mvc.*;
import cn.mapway.ui.client.tools.IShowMessage;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import java.util.List;
import java.util.Stack;

/**
 * ModuleDispatcher
 * 有两种事件的发生 会调度到这个模块调度器
 * 1. 直接调用本类提供的接口 switchModule
 * 2.接受来自 eventBus 上的模块调度消息
 * -调度模块
 * -返回之前的模块
 * <p>
 * 关于 URL中Hash
 * 1.之前版本中的hash只支持一级模块 #2A3F12
 * 2.变更之后支持多级模块的调度 #2A3F12;34490;443443
 *
 * @author zhangjianshe@gmail.com
 */
public class ModuleDispatcher implements IModuleDispatcher, IEventHandler, IShowMessage {

    /**
     * 全局调度主题
     */

    EventBus eventBus;
    ModuleFactory moduleFactory = BaseAbstractModule.getModuleFactory();
    IModule current = null;
    Stack<IModule> widgets = new Stack<IModule>();


    /**
     * 全局模块调度器 需要一个全局的事件总线
     *
     * @param eventBus
     */
    public ModuleDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(MODULE_DISPATCH_EVENT, this);
        this.eventBus.register(MODULE_RETURN_EVENT, this);
    }

    @Override
    public IModuleDispatcher switchModule(String code, ModuleParameter parameter, boolean saveToHistory) {
        SwitchModuleData data = new SwitchModuleData(code, "");
        data.setParameters(parameter);
        changeLayout(data);
        return this;
    }

    /**
     * 根据模块数据 切换页面布局
     *
     * @param data
     */
    private void changeLayout(SwitchModuleData data) {
        if (data == null) {
            Logs.info("在模块切换中 开发人员不小心，传入了空的参数");
            return;
        }
        ModuleInfo moduleInfo = moduleFactory.findModuleInfo(data.getModuleCode());
        if (moduleInfo == null) {
            Logs.info("没有模块信息" + data.getModuleCode());
            return;
        }

        IModule module = moduleFactory.createModule(data.getModuleCode(), true);
        if (module == null) {
            Logs.info("生成模块" + data.getModuleCode() + "错误");
            return;
        }

        if (current != null && current.getModuleInfo().code.equals(data.getModuleCode())) {
            //模块没有变化 可能是参数变化了
            data.getParameters().put(IModuleDispatcher.KEY_MODULE_HASHES,data.getHash());
            current.initialize(null, data.getParameters());
            return;
        }


        if (current != null) {
            //保存浏览器导航数据
            History.newItem(current.getModuleInfo().hash, false);
        }

        RootLayoutPanel root = RootLayoutPanel.get();
        root.clear();
        current = module;
        root.add(current.getRootWidget());

        data.getParameters().put(IModuleDispatcher.KEY_MODULE_HASHES, data.getHash());
        current.initialize(null, data.getParameters());
    }

    public static HashParameter parseHash(String rootHash, String hash) {
        HashParameter hashParameter = new HashParameter();
        //这里需要处理一下 mainwindow的hash值 如果 data.gethash == mainwindow的hash 则不需要放入参数中
        // Hash may be like this #2A3F12;34490;443443
        // 提取出第一级模块 然后将其余的模块放入参数中
        List<String> hashes = StringUtil.splitIgnoreBlank(hash, ";");
        if (hashes.size() > 0) {
            hashParameter.hash = hashes.get(0);
        }
        StringBuilder hashParameterString = new StringBuilder();
        for (int i = 1; i < hashes.size(); i++) {
            String hash0 = hashes.get(i);
            if (hash0.equals(rootHash)) {
                //模块哈希值与要调度的模块一致 移除
                continue;
            }
            if (hashParameterString.length() > 0) {
                hashParameterString.append(";");
            }
            hashParameterString.append(hash);
        }
        hashParameter.subHashes = hashParameterString.toString();
        return hashParameter;
    }

    @Override
    public void onEvent(String topic, int type, Object event) {
        if (MODULE_DISPATCH_EVENT.equals(topic)) {
            SwitchModuleData data = (SwitchModuleData) event;
            changeLayout(data);
        } else if (MODULE_RETURN_EVENT.equals(topic)) {
            IModule module = widgets.pop();
            if (module != null) {
                RootLayoutPanel root = RootLayoutPanel.get();
                root.clear();
                current = module;
                root.add(current.getRootWidget());
            }
        }
    }

    @Override
    public void showMessage(int level, Integer code, String message) {
        if (current != null) {
            IShowMessage showMessage = (IShowMessage) current;
            if (showMessage != null) {
                showMessage.showMessage(level, code, message);
            } else {
                Logs.info(message);
            }
        } else {
            Logs.info(message);
        }
    }
}
