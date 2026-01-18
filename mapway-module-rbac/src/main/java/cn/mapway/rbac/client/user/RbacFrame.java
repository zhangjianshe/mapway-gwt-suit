package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.org.RbacOrgFrame;
import cn.mapway.rbac.client.role.RoleResourceFrame;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.frame.ToolbarModules;
import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleMarker;
import cn.mapway.ui.client.mvc.ModuleParameter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * RbacFrame
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@ModuleMarker(value = RbacFrame.MODULE_CODE,
        name = "角色授权",
        unicode = Fonts.AUTHORIZE,
        summary = "RbacStudio",
        group = RbacConstant.MODULE_GROUP_RBAC,
        order = 1
)
public class RbacFrame extends ToolbarModules {
    public static final String MODULE_CODE = "rbac_frame";
    private static final RbacFrameUiBinder ourUiBinder = GWT.create(RbacFrameUiBinder.class);

    @UiField
    DockLayoutPanel root;
    boolean first = true;

    public RbacFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        Boolean b = super.initialize(parentModule, parameter);
        if (first) {
            first = false;
            switchTo(RbacOrgFrame.MODULE_CODE);
        }
        return b;
    }

    @Override
    protected void initializeSubsystem() {
        registerModule(RbacOrgFrame.MODULE_CODE);
        registerModule(RoleResourceFrame.MODULE_CODE);
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }

    interface RbacFrameUiBinder extends UiBinder<DockLayoutPanel, RbacFrame> {
    }
}