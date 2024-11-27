package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.client.user.SearchUserPanel;
import cn.mapway.rbac.client.user.UserRoleResourcePanel;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
import cn.mapway.rbac.shared.rpc.DeleteOrgRequest;
import cn.mapway.rbac.shared.rpc.DeleteOrgResponse;
import cn.mapway.rbac.shared.rpc.UpdateOrgRequest;
import cn.mapway.rbac.shared.rpc.UpdateOrgResponse;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleMarker;
import cn.mapway.ui.client.mvc.ModuleParameter;
import cn.mapway.ui.client.mvc.attribute.editor.inspector.ObjectInspector;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;

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
    ObjectInspector objectInspector;
    @UiField
    OrgUserListPanel userList;
    @UiField
    UserRoleResourcePanel userRoleResourcePanel;
    RbacOrgAttrProvider rbacOrgAttrProvider;
    public RbacOrgFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        rbacOrgAttrProvider = new RbacOrgAttrProvider();
        objectInspector.setData(rbacOrgAttrProvider);
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

    @UiHandler("objectInspector")
    public void objectInspectorCommon(CommonEvent event) {
        if (event.isSave()) {
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
                        msg(result.getMessage());
                    }
                }
            });
        }
    }

    private void msg(String message) {
        DataBus.get().message(message);
    }

    @UiHandler("orgPanel")
    public void orgPanelCommon(CommonEvent event) {
        if (event.isEdit()) {
            RbacOrgEntity org = event.getValue();
            rbacOrgAttrProvider.rebuild(org);
            if (StringUtil.isNotBlank(org.getId())) {
                userList.load(org.getCode());
            }
        } else if (event.isDelete()) {
            RbacOrgEntity org = event.getValue();
            confirmDelete(org);
        }
    }

    @UiHandler("userList")
    public void uerListCommon(CommonEvent event) {
        if (event.isSelect()) {
            RbacOrgUserEntity user = event.getValue();
            userRoleResourcePanel.load(user.getUserCode());
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