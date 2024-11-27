package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.client.role.ResourceTable;
import cn.mapway.rbac.client.role.RoleSelector;
import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import java.util.List;

/**
 * 用户角色权限
 */
public class UserRoleResourcePanel extends CommonEventComposite {
    private static final UserRoleResourcePanelUiBinder ourUiBinder = GWT.create(UserRoleResourcePanelUiBinder.class);
    @UiField
    ZTree roleList;
    @UiField
    ResourceTable resourceTable;
    @UiField
    Button btnAdd;
    @UiField
    Button btnDelete;
    String userCode;
    RbacRoleEntity selectedRole = null;

    public UserRoleResourcePanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void load(String userCode) {
        this.userCode = userCode;
        QueryUserRoleRequest request = new QueryUserRoleRequest();
        request.setUserCode(userCode);
        RbacServerProxy.get().queryUserRole(request, new AsyncCallback<RpcResult<QueryUserRoleResponse>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(RpcResult<QueryUserRoleResponse> result) {
                if (result.isSuccess()) {
                    renderRoleList(result.getData().getRoles());
                }
            }
        });
        updateUI();
    }

    private void updateUI() {
        btnAdd.setEnabled(StringUtil.isNotBlank(userCode));
        btnDelete.setEnabled(selectedRole != null);
    }

    private void renderRoleList(List<RbacRoleEntity> roles) {
        roleList.clear();
        for (RbacRoleEntity role : roles) {
            ImageTextItem item = roleList.addItem(null, role.getName(), null);
            item.setData(role);
        }
    }

    @UiHandler("roleList")
    public void roleListCommon(CommonEvent event) {
        if (event.isSelect()) {
            ImageTextItem item = event.getValue();
            selectedRole = (RbacRoleEntity) item.getData();
            loadRoleResource(selectedRole.getCode());
            updateUI();
        }
    }

    @UiHandler("btnAdd")
    public void btnAddClick(ClickEvent event) {

        Dialog<RoleSelector> dialog = RoleSelector.getDialog(true);
        dialog.addCommonHandler(commonEvent -> {
            if (commonEvent.isOk()) {
                RbacRole role = commonEvent.getValue();
                // do add user code 's role
                doAddUserRole(userCode, role);
            }
            dialog.hide();
        });
        dialog.getContent().setEnableEditor(false);
        dialog.getContent().load();
        dialog.center();
    }

    private void doAddUserRole(String userCode, RbacRole role) {
        CreateUserRoleRequest request = new CreateUserRoleRequest();
        request.setUserCode(userCode);
        request.setRoleCode(role.code);
        RbacServerProxy.get().createUserRole(request, new AsyncCallback<RpcResult<CreateUserRoleResponse>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(RpcResult<CreateUserRoleResponse> result) {
                if (result.isSuccess()) {
                    load(userCode);
                }
            }
        });
    }

    @UiHandler("btnDelete")
    public void btnDeleteClick(ClickEvent event) {
        if (selectedRole == null) {
            return;
        }
        confirmDelete(selectedRole);
    }

    private void confirmDelete(RbacRoleEntity selectedRole) {
        String message = "删除用户 " + userCode + " 关联的角色 " + selectedRole.getName() + "?";
        RbacClient.get().getClientContext().confirm(message).then(accept -> {
            doDelete(selectedRole);
            return null;
        });
    }

    private void doDelete(RbacRoleEntity selectedRole) {
        DeleteUserRoleRequest request = new DeleteUserRoleRequest();
        request.setUserCode(userCode);
        request.setRoleCode(selectedRole.getCode());
        RbacServerProxy.get().deleteUserRole(request, new AsyncCallback<RpcResult<DeleteUserRoleResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<DeleteUserRoleResponse> result) {
                if (result.isSuccess()) {
                    load(userCode);
                } else {
                    msg(result.getMessage());
                }
            }
        });
    }

    private void loadRoleResource(String roleCode) {

        QueryRoleResourceRequest request = new QueryRoleResourceRequest();
        request.setRoleCode(roleCode);
        request.setWidthChildren(false);
        RbacServerProxy.get().queryRoleResource(request, new AsyncCallback<RpcResult<QueryRoleResourceResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<QueryRoleResourceResponse> result) {
                if (result.isSuccess()) {
                    resourceTable.setData(result.getData().getResources());
                } else {
                    msg(result.getMessage());
                }
            }
        });
    }

    private void msg(String message) {
        RbacClient.get().getClientContext().alert(message);
    }

    interface UserRoleResourcePanelUiBinder extends UiBinder<DockLayoutPanel, UserRoleResourcePanel> {
    }
}