package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.org.RbacOrgFrame;
import cn.mapway.rbac.client.role.RoleResourceFrame;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.ui.client.IClientContext;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.frame.ModuleBar;
import cn.mapway.ui.client.mvc.*;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * AiStudioSystem
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@ModuleMarker(value = RbacFrame.MODULE_CODE,
        name = "角色授权",
        unicode = Fonts.RBAC_ROLE,
        summary = "RbacStudio",
        group = RbacConstant.MODULE_GROUP_RBAC,
        order = 1
)
public class RbacFrame extends BaseAbstractModule {
    public static final String MODULE_CODE = "rbac_frame";
    private static final RbacFrameUiBinder ourUiBinder = GWT.create(RbacFrameUiBinder.class);

    @UiField
    DockLayoutPanel root;
    @UiField
    ModuleBar moduleBar;
    @UiField
    HTMLPanel tools;
    IModule selectedModule = null;

    public RbacFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        List<String> codes = new ArrayList<>();
        codes.add(RbacOrgFrame.MODULE_CODE);
        codes.add(RoleResourceFrame.MODULE_CODE);
        moduleBar.setData(codes);
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        boolean b= super.initialize(parentModule, parameter);
        IClientContext clientContext = RbacClient.get().getClientContext();
        if(!clientContext.isAssignRole(RbacConstant.ROLE_SYS_MAINTAINER))
        {
            //没有授权操作
            SwitchModuleData switchModuleData = new SwitchModuleData(SimpleFrame.MODULE_CODE, "");
            switchModuleData.getParameters().put("没有授权");
            switchModule(switchModuleData);
            root.setWidgetHidden(tools, true);
        }
        else{
            root.setWidgetHidden(tools, false);
            SwitchModuleData switchModuleData = new SwitchModuleData(RbacOrgFrame.MODULE_CODE, "");
            switchModuleData.getParameters().put("");
            switchModule(switchModuleData);
        }
        return b;
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }

    private void removeCurrent() {
        if (selectedModule != null) {
            root.remove(selectedModule.getRootWidget());
        }
        selectedModule = null;
    }


    @UiHandler("moduleBar")
    public void moduleBarCommon(CommonEvent event) {
        if (event.isSwitch()) {
            SwitchModuleData data = event.getValue();
            switchModule(data);
        }
    }

    private void switchModule(SwitchModuleData data) {
        removeCurrent();
        IModule module = BaseAbstractModule.getModuleFactory().createModule(data.getModuleCode(), true);
        root.add(module.getRootWidget());
        selectedModule = module;
        module.initialize(this, data.getParameters());
    }

    interface RbacFrameUiBinder extends UiBinder<DockLayoutPanel, RbacFrame> {
    }
}