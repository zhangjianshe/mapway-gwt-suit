package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.rpc.DeleteOrgUserRequest;
import cn.mapway.rbac.shared.rpc.DeleteOrgUserResponse;
import cn.mapway.rbac.shared.rpc.UpdateOrgUserRequest;
import cn.mapway.rbac.shared.rpc.UpdateOrgUserResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
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
import com.google.gwt.user.client.ui.HorizontalPanel;

import java.util.Date;

public class OrgUserListPanel extends CommonEventComposite  {
    private static final OrgUserListPanelUiBinder ourUiBinder = GWT.create(OrgUserListPanelUiBinder.class);
    @UiField
    HorizontalPanel tools;
    @UiField
    Button btnAdd;
    @UiField
    Button btnDelete;
    @UiField
    OrgUserList list;
    String orgCode;
    RbacOrgUserEntity selectedUser;

    public OrgUserListPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        updateUI();
    }

    public void doAddUser(String orgCode, IUserInfo userInfo) {
        RbacOrgUserEntity entity = new RbacOrgUserEntity();
        entity.setCreateTime(new Date());
        entity.setUserCode("");
        entity.setAvatar(userInfo.getAvatar());
        entity.setAliasName(userInfo.getUserName());
        entity.setUserId(userInfo.getId());
        entity.setOrgCode(orgCode);
        entity.setSystemCode(userInfo.getSystemCode());
        UpdateOrgUserRequest request = new UpdateOrgUserRequest();
        request.setOrgUser(entity);
        RbacServerProxy.get().updateOrgUser(request, new AsyncCallback<RpcResult<UpdateOrgUserResponse>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(RpcResult<UpdateOrgUserResponse> result) {
                list.load(orgCode);
            }
        });

    }


    public void load(String orgCode) {
        this.orgCode = orgCode;
        list.load(orgCode);
        updateUI();
    }

    @UiHandler("btnAdd")
    public void btnAddClick(ClickEvent event) {
        RbacClient.get().getClientContext().chooseUser().then((accept)->{
            if (accept !=null && accept.length > 0) {
                IUserInfo userInfo = accept.getAt(0);
                doAddUser(orgCode, userInfo);
            }
            return null;
        });
    }

    @UiHandler("btnDelete")
    public void btnDeleteClick(ClickEvent event) {
        if (selectedUser == null) {
            return;
        }
        confirmDelete(selectedUser);

    }

    private void confirmDelete(RbacOrgUserEntity selectedUser) {
        RbacClient.get().getClientContext().confirm("确定要删除用户" + selectedUser.getAliasName() + "吗?").then((accept)->{
            doDelete(selectedUser);
            return null;
        });
    }

    private void doDelete(RbacOrgUserEntity user) {
        DeleteOrgUserRequest request = new DeleteOrgUserRequest();
        request.setUserCode(user.getUserCode());
        RbacServerProxy.get().deleteOrgUser(request, new AsyncCallback<RpcResult<DeleteOrgUserResponse>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(RpcResult<DeleteOrgUserResponse> result) {
                load(orgCode);
            }
        });
    }

    @UiHandler("list")
    public void listCommon(CommonEvent event) {
        if (event.isSelect()) {
            selectedUser = event.getValue();
            updateUI();
            fireEvent(CommonEvent.selectEvent(selectedUser));
        }
    }

    private void updateUI() {
        btnDelete.setEnabled(selectedUser != null);
        btnAdd.setEnabled(StringUtil.isNotBlank(orgCode));
    }

    interface OrgUserListPanelUiBinder extends UiBinder<DockLayoutPanel, OrgUserListPanel> {
    }
}