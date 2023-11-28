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
    public static final String urlSeperator = "[;!]";

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

    /**
     * * 解析HAsh参数
     * * 324525;238;32432
     * * 模块ID  模块参数
     * * 本函数的功能 提取第一个参数到hash中
     *
     * @param excludeFirst 第一个参数过滤
     * @param hash
     * @return
     */
    public static HashParameter parseHashParameter(String excludeFirst, String hash) {
        if (hash == null || hash.length() == 0) {
            return new HashParameter();
        }
        if (excludeFirst == null) {
            excludeFirst = "";

        }

        HashParameter hashParameter = new HashParameter();
        List<String> hashes = StringUtil.splitIgnoreBlank(hash, urlSeperator);
        boolean findHash = false;
        StringBuilder paras = new StringBuilder();
        for (int index = 0; index < hashes.size(); index++) {
            String v = hashes.get(index);
            if (!findHash) {
                //找到第一个参数
                if (!excludeFirst.equals(v)){
                    findHash = true;
                    hashParameter.code = v;
                }
            } else {
                if (paras.length() > 0) {
                    paras.append(";").append(v);
                } else {
                    paras.append(v);
                }
            }
        }
        hashParameter.subHashes = paras.toString();
        return hashParameter;
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
            data.getParameters().put(IModuleDispatcher.KEY_MODULE_HASHES, data.getHash());
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
        if (current instanceof IShowMessage) {
            IShowMessage showMessage = (IShowMessage) current;
            showMessage.showMessage(level, code, message);
        } else {
            Logs.info(message);
        }
    }
}
