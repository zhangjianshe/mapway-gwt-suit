package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.RbacRoleResource;
import cn.mapway.rbac.shared.RbacUserOrg;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.QueryUserOrgRequest;
import cn.mapway.rbac.shared.rpc.QueryUserOrgResponse;
import cn.mapway.rbac.shared.rpc.QueryUserRoleResourceRequest;
import cn.mapway.rbac.shared.rpc.QueryUserRoleResourceResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户的授权信息
 * //这个组件见仅仅展示当前登录用户的授权信息
 *  用户->角色->权限
 */
public class UserAuthorityInfoPanel extends CommonEventComposite {
    interface UserAuthorityInfoPanelUiBinder extends UiBinder<DockLayoutPanel, UserAuthorityInfoPanel> {
    }

    private static UserAuthorityInfoPanelUiBinder ourUiBinder = GWT.create(UserAuthorityInfoPanelUiBinder.class);
    @UiField
    ZTree orgList;
    @UiField
    ZTree roleList;

    public UserAuthorityInfoPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void load()
    {
        QueryUserOrgRequest request=new QueryUserOrgRequest();
        IUserInfo userInfo = RbacClient.get().getClientContext().getUserInfo();
        request.setSystemCode(userInfo.getSystemCode());
        request.setUserId(userInfo.getId());
                    orgList.clear();
        RbacServerProxy.get().queryUserOrg(request, new AsyncCallback<RpcResult<QueryUserOrgResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                orgList.setMessage(new Label(caught.getMessage()),60);
            }

            @Override
            public void onSuccess(RpcResult<QueryUserOrgResponse> result) {
                if(result.isSuccess())
                {
                    for (RbacUserOrg org : result.getData().getUserOrgs()) {
                        ImageTextItem item = orgList.addFontIconItem(null, org.orgName, Fonts.LAYER_VIEW);
                        item.setData(org);
                    }
                }
                else {
                    orgList.setMessage(new Label(result.getMessage()),60);
                }
            }
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        load();
    }

    @UiHandler("orgList")
    public void orgListCommon(CommonEvent event) {
        if(event.isSelect())
        {
            ImageTextItem item = event.getValue();
            RbacUserOrg org = (RbacUserOrg) item.getData();
            loadRoleResource(org);
        }
    }

    /**
     * 加载用户身份对应的角色和资源
     * @param org
     */
    private void loadRoleResource(RbacUserOrg org) {
        QueryUserRoleResourceRequest request=new QueryUserRoleResourceRequest();
        request.setUserCode(org.userCode);
        roleList.clear();
        RbacServerProxy.get().queryUserRoleResource(request, new AsyncCallback<RpcResult<QueryUserRoleResourceResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                roleList.setMessage(new Label(caught.getMessage()),60);
            }

            @Override
            public void onSuccess(RpcResult<QueryUserRoleResourceResponse> result) {
                if(result.isSuccess())
                {
                    for (RbacRoleEntity role : result.getData().getRoles()) {
                        ImageTextItem item = roleList.addFontIconItem(null, role.getName(), Fonts.RBAC_ROLE);
                        item.setData(role);
                        List<RbacRoleResource> resources=findResource(role.getCode(),result.getData().getResource());
                        for (RbacRoleResource resource : resources) {
                            ImageTextItem child = roleList.addFontIconItem(item, resource.name,  Fonts.RBAC_ROLE_RESOURCE);
                            child.setData(resource);
                        }
                    }
                }
                else {
                    roleList.setMessage(new Label(result.getMessage()),60);
                }
            }
        });
    }

    private List<RbacRoleResource> findResource(String roleCode, List<RbacRoleResource> resource) {
        List<RbacRoleResource> resources=new ArrayList<>();
        for (RbacRoleResource r : resource) {
            if(roleCode.equals(r.roleCode))
            {
                    resources.add(r);
            }
        }
        return resources;
    }
}