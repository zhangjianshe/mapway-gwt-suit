package cn.mapway.ui.client.mvc;

import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.frame.HashParameter;
import cn.mapway.ui.client.frame.ModuleDispatcher;
import cn.mapway.ui.client.mvc.help.HelpInfo;
import cn.mapway.ui.client.mvc.help.IHelpProvider;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.panel.MessagePanel;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块基类.
 *
 * @author zhangjianshe
 */
public abstract class BaseAbstractModule extends CommonEventComposite implements IModule, IToolsProvider, IHelpProvider {


    private static ModuleFactory FACTORY;
    IModuleCallback callback;
    boolean initialized = false;
    boolean hasAuthority = true;
    private IModule mParentModule;
    private ModuleParameter mParameter;
    private MessagePanel unauthorizedPanel;

    /**
     * Instantiates a new Base abstract module.
     */
    public BaseAbstractModule() {
    }

    /**
     * 模块工厂
     *
     * @return module factory
     */
    public static ModuleFactory getModuleFactory() {
        if (FACTORY == null) {
            FACTORY = GWT.create(ModuleFactory.class);
        }
        return FACTORY;
    }

    /**
     * 是否已经初始化了
     */
    public boolean hasInitialized() {
        return initialized;
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        initialized = true;
        if (parameter == null) {
            mParameter = new ModuleParameter();
        } else {
            mParameter = parameter;
        }
        mParentModule = parentModule;

        //判断用户是否拥有此模块的 授权
        Object object = mParameter.get(IAuthorize.IGNORE_CHECK_KEY);
        if (object == null || !String.valueOf(object).trim().equalsIgnoreCase("true")) {
            //用户没有特别设定　或略检查
            Object authority = mParameter.get(IAuthorize.AUTHORITY_KEY);
            if (authority instanceof IAuthorize) {
                hasAuthority = ((IAuthorize) authority).isAuthorized(this.getModuleCode(), mParameter);
            } else {
                DomGlobal.console.log("模块权限检查配置错误");
                //为了兼容之前的代码　所有判断错误的都允许授权
                hasAuthority = true;
            }
        } else {
            hasAuthority = true;
        }

        return true;
    }

    /**
     * 解析HAsh参数
     * 324525;238;32432
     * 模块ID  模块参数
     * 本函数的功能 提取第一个参数到hash中
     *
     * @param hash
     * @return
     */
    public HashParameter parseHashParameter(String hash) {
        return ModuleDispatcher.parseHashParameter(getModuleInfo().hash, hash);
    }

    /**
     * 缺省不提供帮助信息
     *
     * @param helpId 暂时没有使用
     * @return
     */
    @Override
    public HelpInfo getHelpInfo(String helpId) {
        return null;
    }

    @Override
    public void unInitialize() {
        initialized = false;
    }

    @Override
    public Widget getTools() {
        return null;
    }

    @Override
    public boolean updateTools(Widget... tools) {
        if (mParentModule != null) {
            return mParentModule.updateTools(tools);
        }
        return false;
    }

    @Override
    public boolean appendTools(Widget tools) {
        if (mParentModule != null) {
            return mParentModule.appendTools(tools);

        }
        return false;
    }

    @Override
    public boolean appendTools(Widget[] tools) {
        if (mParentModule != null) {
            return mParentModule.appendTools(tools);

        }
        return false;
    }

    @Override
    public IModule getParentModule() {
        return mParentModule;
    }

    @Override
    public ModuleInfo getModuleInfo() {
        return getModuleFactory().findModuleInfo(getModuleCode());
    }

    /**
     * 获取模块代码
     *
     * @return module code
     */
    public abstract String getModuleCode();

    @Override
    public Widget getRootWidget() {
        if (hasAuthority) {
            return this;
        } else {
            if (unauthorizedPanel == null) {
                unauthorizedPanel = new MessagePanel();
                unauthorizedPanel.setHtml("<div style='display:flex;align-items:center;justify-content:center;font-size:1.2rem;'>该模块(" + getModuleCode() + ")没有授权</div>");
            }
            return unauthorizedPanel;
        }
    }

    private List<IModule> getModuleStack(IModule module) {

        List<IModule> modules = new ArrayList<IModule>();
        IModule p = module;
        while (p != null) {
            modules.add(p);
            p = p.getParentModule();
        }
        return modules;
    }

    /**
     * Gets module path.
     *
     * @param module the module
     * @return the module path
     */
    public List<SwitchModuleData> getModulePath(IModule module) {

        List<SwitchModuleData> r = new ArrayList<SwitchModuleData>();

        List<IModule> modules = getModuleStack(module);

        for (IModule m : modules) {
            ModuleInfo info = m.getModuleInfo();
            SwitchModuleData d = new SwitchModuleData(info.code, info.hash);
            d.setParameters(m.getParameters());
            r.add(0, d);
        }
        return r;
    }

    @Override
    public ModuleParameter getParameters() {
        return mParameter;
    }

    @Override
    public void addModuleCallback(IModuleCallback callback) {
        this.callback = callback;
    }

    /**
     * 向模块外发送事件
     *
     * @param module
     * @param event
     */
    public void fireModuleEvent(IModule module, CommonEvent event) {
        if (this.callback != null) {
            this.callback.callback(module, event);
        }
    }

    @Override
    public void fireMessage(MessageObject message) {
        if (this.callback != null) {
            this.callback.callback(this, CommonEvent.messageEvent(message));
        } else {
            super.fireMessage(message);
        }
    }

    public void processServiceCode(RpcResult result) {
        if (result != null && result.getCode().equals(300001)) {
            throw new RuntimeException("300001");
        }
    }
}
