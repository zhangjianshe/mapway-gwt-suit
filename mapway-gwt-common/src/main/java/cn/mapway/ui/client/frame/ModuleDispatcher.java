package cn.mapway.ui.client.frame;

import cn.mapway.ui.client.event.EventBus;
import cn.mapway.ui.client.event.IEventHandler;
import cn.mapway.ui.client.mvc.*;
import cn.mapway.ui.client.tools.IShowMessage;
import cn.mapway.ui.client.util.Logs;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import java.util.Stack;

/**
 * ModuleDispatcher
 * 有两种事件的发生 会调度到这个模块调度器
 * 1. 直接调用本类提供的接口 switchModule
 * 2.接受来自 eventBus 上的模块调度消息
 * -调度模块
 * -返回之前的模块
 *
 * @author zhangjianshe@gmail.com
 */
public class ModuleDispatcher implements IModuleDispatcher, IEventHandler, IShowMessage {

    /**
     * 全局调度主题
     */
    public final static String MODULE_DISPATCH_EVENT = "MODULE_DISPATCH_EVENT";
    public final static String MODULE_RETURN_EVENT = "MODULE_DISPATCH_RETURN";

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
        IModule module = moduleFactory.createModule(data.getModuleCode(), true);
        if (module == null) {
            Logs.info("生成模块" + data.getModuleCode() + "错误");
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
        //这里需要处理一下 mainwindow的hash值 如果 data.gethash == mainwindow的hash 则不需要放入参数中
        if (!module.getModuleInfo().hash.equals(data.getHash())) {
            data.getParameters().put(data.getHash());
        }
        current.initialize(null, data.getParameters());
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
