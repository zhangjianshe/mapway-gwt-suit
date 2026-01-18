package cn.mapway.rbac.client.role;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.*;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.client.widget.SearchBox;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @UiField
    ResourceKindDropdown kindDropdown;
    @UiField
    SearchBox searchBox;
    RbacRole selectedRole;

    Integer kind = ResourceKind.RESOURCE_KIND_SYS_MENU.code;
    List<RbacResourceEntity> resourceEntityList = null;

    public RoleResourceFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        kindDropdown.addValueChangeHandler(event -> {
            kind = (Integer) event.getValue();
            filterKind(kind);
        });
        searchBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                filterName(event.getValue());
            }
        });
    }

    private void filterName(String value) {
        if (value == null || value.trim().isEmpty()) {
            if (resourceEntityList == null) {
                resourceTable.setData(new ArrayList<>());
            } else {
                resourceTable.setData(resourceEntityList);
            }
        } else {
            List<RbacResourceEntity> collect = resourceEntityList.stream().filter(r -> {
                return r.getName().contains(value);
            }).collect(Collectors.toList());
            resourceTable.setData(collect);
        }
    }

    private void filterKind(Integer kind) {
        if (resourceEntityList == null || resourceEntityList.isEmpty()) {
            resourceTable.setData(new ArrayList<>());
            return;
        }
        List<RbacResourceEntity> collect = resourceEntityList.stream().filter(r -> {
            return r.getKind().equals(kind);
        }).collect(Collectors.toList());
        resourceTable.setData(collect);
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        super.initialize(parentModule, parameter);
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
                resourceEntityList = result.getData().getResources();
                filterName("");
            }
        });
    }

    private void updateUI() {
        btnAdd.setEnabled(selectedRole != null);
    }

    @UiHandler("btnAdd")
    public void btnAddClick(ClickEvent event) {
        Dialog<ResourceTree> dialog = ResourceTree.getDialog(true);
        dialog.addCommonHandler(commonEvent -> {
            if (commonEvent.isOk()) {
                List<RbacResourceEntity> resources = commonEvent.getValue();
                doAddResource(resources);
            } else if (commonEvent.isMessage()) {
                dialog.getContent().saveBar.msg(commonEvent.getValue());
            } else if (commonEvent.isClose()) {
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
        String message = "删除角色 " + selectedRole.name + " 关联的资源 " + resource.getName() + "?";
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
                if (result.isSuccess()) {
                    loadRoleResource(selectedRole);
                } else {
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
    private void doAddResource(List<RbacResourceEntity> resource) {
        UpdateRoleResourceRequest request = new UpdateRoleResourceRequest();
        List<String> codes = new ArrayList<String>();
        for (RbacResourceEntity item : resource) {
            codes.add(item.getResourceCode());
        }
        request.setResourceCodes(codes);
        request.setRoleCode(selectedRole.code);
        RbacServerProxy.get().updateRoleResource(request, new AsyncCallbackLambda<RpcResult<UpdateRoleResourceResponse>>() {
            @Override
            public void onResult(RpcResult<UpdateRoleResourceResponse> result) {
                if (result.isSuccess()) {
                    loadRoleResource(selectedRole);
                } else {
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