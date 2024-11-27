package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.org.RbacOrgFrame;
import cn.mapway.rbac.client.role.RoleResourceFrame;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.frame.ModuleBar;
import cn.mapway.ui.client.mvc.*;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * AiStudioSystem
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@ModuleMarker(value = RbacFrame.MODULE_CODE,
        name = "RBAC",
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
    IModule selectedModule = null;

    public RbacFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        List<String> codes = new ArrayList<>();
        codes.add(RbacOrgFrame.MODULE_CODE);
        codes.add(RoleResourceFrame.MODULE_CODE);
        moduleBar.setData(codes);
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