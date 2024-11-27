package cn.mapway.rbac.client.role;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.*;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

@ModuleMarker(value = RoleResourceFrame.MODULE_CODE,
        name = "角色资源",
        unicode = Fonts.RBAC_ROLE_RESOURCE,
        summary = "RbacStudio",
        group = RbacConstant.MODULE_GROUP_RBAC,
        order = 1
)
public class RoleResourceFrame extends BaseAbstractModule {
    public static final String MODULE_CODE = "role_resource_frame";
    private static final RoleResourceFrameUiBinder ourUiBinder = GWT.create(RoleResourceFrameUiBinder.class);
    @UiField
    HorizontalPanel tools;
    @UiField
    RoleTree roleTree;
    @UiField
    Header lbLabel;
    @UiField
    Button btnAdd;
    @UiField
    ResourceTable resourceTable;
    RbacRole selectedRole;

    public RoleResourceFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        super.initialize(parentModule, parameter);
        updateTools(tools);
        roleTree.load();
        return true;
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }

    @UiHandler("roleTree")
    public void roleTreeCommon(CommonEvent event) {
        if (event.isSelect()) {
            selectedRole = event.getValue();
            lbLabel.setText(selectedRole.name);
            updateUI();
            loadRoleResource(selectedRole);
        }
    }

    /**
     * 加载角色的关联资源
     *
     * @param selectedRole
     */
    private void loadRoleResource(RbacRole selectedRole) {
        QueryRoleResourceRequest request = new QueryRoleResourceRequest();
        request.setRoleCode(selectedRole.code);
        request.setWidthChildren(false);
        RbacServerProxy.get().queryRoleResource(request, new AsyncCallbackLambda<RpcResult<QueryRoleResourceResponse>>() {
            @Override
            public void onResult(RpcResult<QueryRoleResourceResponse> result) {
                resourceTable.setData(result.getData().getResources());
            }
        });
    }

    private void updateUI() {
        btnAdd.setEnabled(selectedRole != null);
    }

    @UiHandler("btnAdd")
    public void btnAddClick(ClickEvent event) {
        Dialog<ResourceTree> dialog = ResourceTree.getDialog(true);
        dialog.addCommonHandler(new CommonEventHandler() {
            @Override
            public void onCommonEvent(CommonEvent commonEvent) {
                if (commonEvent.isOk()) {
                    RbacResourceEntity resource = commonEvent.getValue();
                    doAddResource(resource);
                }
                dialog.hide();
            }
        });
        dialog.getContent().load();
        dialog.center();
    }

    @UiHandler("resourceTable")
    public void resourceTableCommon(CommonEvent event) {
        if (event.isDelete()) {
            RbacResourceEntity resource = event.getValue();
            confirmDelete(resource);
        }
    }

    private void confirmDelete(RbacResourceEntity resource) {
        String message = "删除角色 "+selectedRole.name+" 关联的资源 " + resource.getName()+"?";
        RbacClient.get().getClientContext().confirm(message).then((accept) -> {
            doDelete(resource);
            return null;
        });
    }

    private void doDelete(RbacResourceEntity resource) {
        DeleteRoleResourceRequest request = new DeleteRoleResourceRequest();
        request.setResourceCode(resource.getResourceCode());
        request.setRoleCode(selectedRole.code);
        RbacServerProxy.get().deleteRoleResource(request, new AsyncCallbackLambda<RpcResult<DeleteRoleResourceResponse>>() {
            @Override
            public void onResult(RpcResult<DeleteRoleResourceResponse> result) {
               if(result.isSuccess()){
                   loadRoleResource(selectedRole);
               }
               else {
                   DataBus.get().message(result.getMessage());
               }
            }
        });
    }


    /**
     * 角色添加资源
     *
     * @param resource
     */
    private void doAddResource(RbacResourceEntity resource) {
        UpdateRoleResourceRequest request = new UpdateRoleResourceRequest();
        request.setResourceCode(resource.getResourceCode());
        request.setRoleCode(selectedRole.code);
        RbacServerProxy.get().updateRoleResource(request, new AsyncCallbackLambda<RpcResult<UpdateRoleResourceResponse>>() {
            @Override
            public void onResult(RpcResult<UpdateRoleResourceResponse> result) {
                if(result.isSuccess()){
                    loadRoleResource(selectedRole);
                }
                else {
                    DataBus.get().message(result.getMessage());
                }
            }
        });
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(600, 700);
    }

    interface RoleResourceFrameUiBinder extends UiBinder<DockLayoutPanel, RoleResourceFrame> {
    }
}