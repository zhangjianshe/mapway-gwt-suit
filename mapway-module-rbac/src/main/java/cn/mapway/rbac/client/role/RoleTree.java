package cn.mapway.rbac.client.role;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;

import java.util.List;

/**
 * 角色树
 */
public class RoleTree extends CommonEventComposite {
    private static final RoleTreeUiBinder ourUiBinder = GWT.create(RoleTreeUiBinder.class);
    @UiField
    FontIcon btnCreate;
    @UiField
    ZTree roleTree;
    RbacRole selectedRole;

    public RoleTree() {
        initWidget(ourUiBinder.createAndBindUi(this));
        btnCreate.setIconUnicode(Fonts.PLUS);
    }

    public void load() {
        QueryRoleRequest request = new QueryRoleRequest();
        RbacServerProxy.get().queryRole(request, new AsyncCallbackLambda<RpcResult<QueryRoleResponse>>() {
            @Override
            public void onResult(RpcResult<QueryRoleResponse> result) {
                if (result.isSuccess()) {
                    renderTree(result.getData());
                } else {
                    DataBus.get().message(result.getMessage());
                }
            }
        });
    }


    Boolean enableEditor=true;
    public void setEnableEditor(Boolean enabled)
    {
        this.enableEditor=enabled;
        btnCreate.setEnabled(enabled);
    }

    private void renderTree(QueryRoleResponse data) {
        roleTree.clear();
        recursiveRenderTree(null, data.getRoles());
    }

    private void recursiveRenderTree(ImageTextItem parent, List<RbacRole> roles) {
        if (roles == null || roles.size() == 0) {
            return;
        }
        for (RbacRole role : roles) {
            ImageTextItem item = roleTree.addFontIconItem(parent, role.name+"  ["+role.code+"]", role.icon);
            item.setData(role);
            if(enableEditor) {
                FontIcon editButton = new FontIcon();
                editButton.setIconUnicode(Fonts.PEN);
                editButton.addClickHandler(event -> {
                    event.stopPropagation();
                    event.preventDefault();
                    doEdit(role);
                });
                item.appendWidget(editButton);
            }
            if(enableEditor) {
                if (role.children == null || role.children.size() == 0) {
                    // last leaf node
                    FontIcon deleteButton = new FontIcon();
                    deleteButton.setIconUnicode(Fonts.DELETE);
                    deleteButton.addClickHandler(event -> {
                        event.stopPropagation();
                        event.preventDefault();
                        confirmDelete(role);
                    });
                    item.appendWidget(deleteButton);
                }
            }

            recursiveRenderTree(item, role.children);
        }
    }

    private void confirmDelete(RbacRole role) {
        String message = "确认要删除角色" + role.name + "吗?";
        RbacClient.get().getClientContext().confirm(message).then(result -> {
            doDelete(role);
            return null;
        });
    }

    private void doDelete(RbacRole role) {
        DeleteRoleRequest request = new DeleteRoleRequest();
        RbacServerProxy.get().deleteRole(request, new AsyncCallback<RpcResult<DeleteRoleResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<DeleteRoleResponse> result) {
                if (result.isSuccess()) {
                    load();
                } else {
                    msg(result.getMessage());
                }
            }
        });
    }

    private void msg(String msg) {
        RbacClient.get().getClientContext().alert(msg);
    }

    private void doEdit(RbacRole role) {

        Dialog<RoleEditor> dialog = RoleEditor.getDialog(true);
        dialog.addCommonHandler(commonEvent -> {
            if (commonEvent.isOk()) {
                RbacRole role1 = commonEvent.getValue();
                doSaveRole(dialog, role1);
            }
            dialog.hide();
        });
        dialog.getContent().setData(role);
        dialog.center();
    }

    @UiHandler("btnCreate")
    public void btnCreateClick(ClickEvent event) {
        Dialog<RoleEditor> dialog = RoleEditor.getDialog(true);
        dialog.addCommonHandler(commonEvent -> {
            if (commonEvent.isOk()) {
                RbacRole role = commonEvent.getValue();
                doSaveRole(dialog, role);
            }
            dialog.hide();
        });
        RbacRole role = new RbacRole();
        if (selectedRole != null) {
            role.parentCode = selectedRole.code;
        }
        role.name = "新建角色";
        role.code = "";
        role.summary = role.name;
        dialog.getContent().setData(role);
        dialog.center();
    }

    private void doSaveRole(Dialog<RoleEditor> dialog, RbacRole role) {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setRole(role);
        RbacServerProxy.get().updateRole(request, new AsyncCallbackLambda<RpcResult<UpdateRoleResponse>>() {
            @Override
            public void onResult(RpcResult<UpdateRoleResponse> updateRoleResponseRpcResult) {
                if (updateRoleResponseRpcResult.isSuccess()) {
                    dialog.hide();
                    load();
                } else {
                    dialog.getContent().msg(updateRoleResponseRpcResult.getMessage());
                }
            }
        });
    }

    @UiHandler("roleTree")
    public void roleTreeCommon(CommonEvent event) {
        if (event.isSelect()) {
            ImageTextItem item = event.getValue();
            selectedRole = (RbacRole) item.getData();
            fireEvent(CommonEvent.selectEvent(selectedRole));
        }
    }

    interface RoleTreeUiBinder extends UiBinder<DockLayoutPanel, RoleTree> {
    }
}