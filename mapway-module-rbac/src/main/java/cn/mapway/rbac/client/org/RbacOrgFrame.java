package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.client.user.UserOrgListPanel;
import cn.mapway.rbac.client.user.UserRoleResourcePanel;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.rpc.DeleteOrgRequest;
import cn.mapway.rbac.shared.rpc.DeleteOrgResponse;
import cn.mapway.rbac.shared.rpc.UpdateOrgRequest;
import cn.mapway.rbac.shared.rpc.UpdateOrgResponse;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleMarker;
import cn.mapway.ui.client.mvc.ModuleParameter;
import cn.mapway.ui.client.mvc.attribute.editor.inspector.ObjectEditor;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 *
 */
@ModuleMarker(value = RbacOrgFrame.MODULE_CODE,
        name = "组织机构",
        unicode = Fonts.RBAC_ROLE,
        summary = "RbacStudio",
        group = RbacConstant.MODULE_GROUP_RBAC,
        order = 2
)
public class RbacOrgFrame extends BaseAbstractModule {
    public static final String MODULE_CODE = "rbac_org";
    private static final RbacOrgFrameUiBinder ourUiBinder = GWT.create(RbacOrgFrameUiBinder.class);
    @UiField
    OrgPanel orgPanel;
    @UiField
    OrgUserListPanel userList;
    @UiField
    UserRoleResourcePanel userRoleResourcePanel;
    @UiField
    UserOrgListPanel userOrgList;
    @UiField
    TabLayoutPanel tab;
    RbacOrgAttrProvider rbacOrgAttrProvider;

    public RbacOrgFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        rbacOrgAttrProvider = new RbacOrgAttrProvider();
    }


    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        boolean b = super.initialize(parentModule, parameter);
        orgPanel.init();
        return b;
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }

    public void objectInspectorCommon(CommonEvent event) {
        if (event.isSave()) {

        }
    }

    private void msg(String message) {
        DataBus.get().message(message);
    }

    @UiHandler("orgPanel")
    public void orgPanelCommon(CommonEvent event) {
        if (event.isEdit()) {
            //编辑组织
            RbacOrgEntity org = event.getValue();
            editOrg(org);
        } else if (event.isSelect()) {
            RbacOrgEntity org = event.getValue();
            if (StringUtil.isNotBlank(org.getId())) {
                userList.load(org.getCode());
            }
        } else if (event.isDelete()) {
            RbacOrgEntity org = event.getValue();
            confirmDelete(org);
        }
    }

    private void editOrg(RbacOrgEntity org) {
        rbacOrgAttrProvider.rebuild(org);
        Dialog<ObjectEditor> dialog = ObjectEditor.getDialog(true);
        dialog.getContent().setColumns(2);
        dialog.addCommonHandler(new CommonEventHandler() {
            @Override
            public void onCommonEvent(CommonEvent event) {
                if (event.isSave()) {
                    doSave(rbacOrgAttrProvider);
                    dialog.hide();
                } else {
                    dialog.hide();
                }
            }
        });
        SaveBar saveBar = dialog.getContent().getSaveBar();
        saveBar.setEnableSave(true);
        saveBar.setSaveText("保存");
        dialog.setPixelSize(900, 550);
        dialog.center();
        dialog.getContent().setData(rbacOrgAttrProvider);

    }

    private void doSave(RbacOrgAttrProvider rbacOrgAttrProvider) {

        RbacOrgEntity org = rbacOrgAttrProvider.getOrg();
        UpdateOrgRequest request = new UpdateOrgRequest();
        request.setOrg(org);
        RbacServerProxy.get().updateOrg(request, new AsyncCallback<RpcResult<UpdateOrgResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<UpdateOrgResponse> result) {
                if (result.isSuccess()) {
                    orgPanel.init();
                } else {
                    RbacClient.get().getClientContext().alert(result.getMessage());
                }
            }
        });
    }

    @UiHandler("userList")
    public void uerListCommon(CommonEvent event) {
        if (event.isSelect()) {
            //选中了一个用户
            //展示两部分内容
            // 1.用户的所属角色列表
            RbacOrgUserEntity user = event.getValue();
            userRoleResourcePanel.load(user.getUserCode());
            // 2.用户所属的机构列表
            userOrgList.load(user.getUserId(), user.getSystemCode());
        }
    }

    private void confirmDelete(RbacOrgEntity org) {
        String message = "确定要删除组织机构" + org.getName() + "吗,包括他的子机构?";
        RbacClient.get().getClientContext().confirm(message).then((accept) -> {
            doDelete(org);
            return null;
        });

    }

    private void doDelete(RbacOrgEntity org) {
        DeleteOrgRequest request = new DeleteOrgRequest();
        request.setOrgId(org.getId());
        RbacServerProxy.get().deleteOrg(request, new AsyncCallback<RpcResult<DeleteOrgResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<DeleteOrgResponse> result) {
                if (result.isSuccess()) {
                    rbacOrgAttrProvider.rebuild(null);
                    orgPanel.init();
                } else {
                    msg(result.getMessage());
                }
            }
        });
    }

    interface RbacOrgFrameUiBinder extends UiBinder<DockLayoutPanel, RbacOrgFrame> {
    }
}