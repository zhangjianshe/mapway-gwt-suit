package cn.mapway.ui.client.frame;


import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.IToolsProvider;
import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.tools.IShowMessage;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.AiTab;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ToobarModules
 * 此类的子类可以拥有一个工具栏，该工具栏上展示一些子模块
 *
 * @author zhangjianshe@gmail.com
 */
public abstract class ToolbarModules extends BaseAbstractModule implements IShowMessage, RequiresResize, ProvidesResize {
    private static final ToolbarModulesUiBinder ourUiBinder = GWT.create(ToolbarModulesUiBinder.class);
    Widget current = null;
    ToolbarsUiHolder uiHolder;
    private final CommonEventHandler subModuleHandler = event -> {
        //子系统模块被激活
        if (event.isSelect()) {
            ModuleInfo info = event.getValue();
            switchTo(info);
        }
    };
    List<String> moduleCodes;

    public ToolbarModules() {

    }

    public void switchTo(String code) {
        IModule module = getModuleFactory().createModule(code, true);
        switchToModule(module);
    }

    public void switchTo(ModuleInfo info) {
        IModule module = getModuleFactory().createModule(info.code, true);
        switchToModule(module);
    }

    private void switchToModule(IModule module) {
        if (current != null) {
            current.removeFromParent();
        }
        current = module.getRootWidget();
        uiHolder.root.add(current);
        module.initialize(ToolbarModules.this, null);
        uiHolder.tools.clear();
        IToolsProvider provider = (IToolsProvider) current;
        if (provider != null) {
            Widget widgets = provider.getTools();
            if (widgets != null) {
                uiHolder.tools.add(widgets);
            }
        }
    }

    @Override
    protected void initWidget(Widget widget) {
        moduleCodes = new ArrayList<>();
        uiHolder = new ToolbarsUiHolder();
        super.initWidget(ourUiBinder.createAndBindUi(uiHolder));
        initializeSubsystem();
        ModuleInfo moduleInfo = getModuleInfo();
        uiHolder.lbTitle.setText(moduleInfo.name);
        setIcon(moduleInfo.unicode, moduleInfo.icon);
        setStyleName("ai-top-bar");
        uiHolder.btnSubmodules.addCommonHandler(subModuleHandler);
        updateUI();
        current = widget;
        uiHolder.root.add(widget);
    }

    private void setIcon(String unicode, ImageResource icon) {
        if (StringUtil.isBlank(unicode)) {
            uiHolder.fontIcon.setVisible(false);
            uiHolder.icon.setVisible(true);
            uiHolder.icon.setResource(icon);
        } else {
            uiHolder.icon.setVisible(false);
            uiHolder.fontIcon.setVisible(true);
            uiHolder.fontIcon.setIconUnicode(unicode);
        }
    }

    protected void updateUI() {

        for (String moduleCode : moduleCodes) {
            ModuleInfo moduleInfo = getModuleFactory().findModuleInfo(moduleCode);
            if (moduleInfo == null) {
                Logs.info("模块配置错误" + moduleCode);
                continue;
            }
            uiHolder.btnSubmodules.addItem(moduleInfo.name, moduleInfo.icon, moduleInfo);
        }
    }

    @Override
    public void setStyleName(String style) {
        uiHolder.panel.setStyleName(style);
    }

    public void setCaption(String caption) {
        uiHolder.lbTitle.setText(caption);
    }

    /**
     * 清空操作面板区
     */
    public void clearOperations() {

    }

    /**
     * 添加操作面板
     *
     * @param widgets
     */
    public void appendOperation(Widget... widgets) {

    }

    @Override
    public void onResize() {
        uiHolder.root.onResize();
    }

    @Override
    public void showMessage(int level, Integer code, String message) {
        fireMessage(new MessageObject(level, code, message));
    }


    @Override
    public boolean updateTools(Widget... tools) {

        uiHolder.tools.clear();
        for (Widget w : tools) {
            uiHolder.tools.add(w);
        }
        return true;
    }

    /**
     * 注册子系统管理的模块
     *
     * @param moduleCode
     */
    public void registerModule(String moduleCode) {
        if (!moduleCodes.contains(moduleCode)) {
            moduleCodes.add(moduleCode);
        }
    }

    /**
     * 初始化子系统
     */
    protected abstract void initializeSubsystem();

    @Override
    public boolean appendTools(Widget tools) {
        uiHolder.tools.add(tools);
        return true;
    }

    public void selectSubmodule(int index) {
        if (index >= 0 && index < moduleCodes.size()) {
            uiHolder.btnSubmodules.setSelectIndex(index, true);
        }
    }

    public void msg(String message){
        fireModuleEvent(this,CommonEvent.messageEvent(MessageObject.info(0,message)));
    }

    public void enableLoading(Boolean show) {

        Logs.info("not implemented enabled loading");
    }

    interface ToolbarModulesUiBinder extends UiBinder<DockLayoutPanel, ToolbarsUiHolder> {
    }

    static class ToolbarsUiHolder {

        @UiField
        HorizontalPanel tools;
        @UiField
        HorizontalPanel bar;
        @UiField
        DockLayoutPanel root;
        @UiField
        Image icon;
        @UiField
        Header lbTitle;
        @UiField
        HTMLPanel panel;
        @UiField
        AiTab btnSubmodules;
        @UiField
        FontIcon fontIcon;
    }
}
