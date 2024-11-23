package cn.mapway.ui.client.frame;


import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.mvc.ModuleParameter;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * 子系统窗口管理器
 * 子系统管理器 能够管理本子系统下的所有子模块
 * 如果子模块所属的group为 全屏,就会将子模块进行全屏展示
 */
public abstract class SubsystemModule extends BaseAbstractModule implements RequiresResize {


    private static final SubsystemModuleUiBinder ourUiBinder = GWT.create(SubsystemModuleUiBinder.class);
    /**
     * 用户选择了子模块
     */
    private final CommonEventHandler moduleSelectorHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isSwitch()) {
                fireModuleEvent(SubsystemModule.this, CommonEvent.switchEvent(event.getValue()));
            }
        }
    };
    SubsystemUiHolder uiHolder;
    List<String> moduleCodes;
    boolean showModuleSelector = true;


    public SubsystemModule() {

    }

    @Override
    public void onResize(){
        uiHolder.root.onResize();
    }

    @Override
    protected void initWidget(Widget widget) {
        uiHolder = new SubsystemUiHolder();
        moduleCodes = new ArrayList<>();
        super.initWidget(ourUiBinder.createAndBindUi(uiHolder));
        initializeSubsystem();
        if (widget != null) {
            uiHolder.moduleSelector.removeFromParent();
            uiHolder.root.add(widget);
            showModuleSelector = false;
        }
        uiHolder.moduleSelector.addCommonHandler(moduleSelectorHandler);
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        boolean b = super.initialize(parentModule, parameter);
        if (showModuleSelector) {
            //从子模块中选择
            moduleCodes.clear();
            ModuleInfo moduleInfo = getModuleFactory().findModuleInfo(getModuleCode());
            if (moduleInfo != null && moduleInfo.children != null) {
                for (ModuleInfo child : moduleInfo.children) {
                    if (!child.isVisible) {
                        continue;
                    }
                    moduleCodes.add(child.code);
                }
            }
            uiHolder.moduleSelector.setData(moduleCodes);
        }
        return b;
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




    interface SubsystemModuleUiBinder extends UiBinder<DockLayoutPanel, SubsystemUiHolder> {
    }

    static class SubsystemUiHolder {

        @UiField
        ModuleSelector moduleSelector;
        @UiField
        DockLayoutPanel root;

    }

}
