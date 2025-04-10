package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.rpc.QueryOrgUserRequest;
import cn.mapway.rbac.shared.rpc.QueryOrgUserResponse;
import cn.mapway.rbac.shared.rpc.UpdateOrgUserRequest;
import cn.mapway.rbac.shared.rpc.UpdateOrgUserResponse;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

/**
 * 组织用户列表
 */
public class OrgUserList extends VerticalPanel implements HasCommonHandlers {
    OrgUserItem selectedItem = null;
    String orgCode = "";
    private final CommonEventHandler itemHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isSelect()) {
                OrgUserItem item = (OrgUserItem) event.getSource();
                selectItem(item, true);
            } else if (event.isUpdate()) {
                RbacOrgUserEntity user = event.getValue();
                updateAndReload(user);
            }
        }

    };

    private void updateAndReload(RbacOrgUserEntity user) {
        UpdateOrgUserRequest request = new UpdateOrgUserRequest();
        request.setOrgUser(user);
        RbacServerProxy.get().updateOrgUser(request, new AsyncCallbackLambda<RpcResult<UpdateOrgUserResponse>>() {
            @Override
            public void onResult(RpcResult<UpdateOrgUserResponse> data) {
                reload();
            }

        });
    }

    private void reload() {
        if (StringUtil.isNotBlank(orgCode)) {
            load(orgCode);
        }
    }

    private void selectItem(OrgUserItem item, boolean b) {
        if (selectedItem != null) {
            selectedItem.setSelect(false);
        }
        selectedItem = item;
        if (selectedItem != null) {
            selectedItem.setSelect(b);
            if (b) {
                fireEvent(CommonEvent.selectEvent(selectedItem.getData()));
            }
        }

    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler commonEventHandler) {
        return addHandler(commonEventHandler, CommonEvent.TYPE);
    }

    public void load(String orgCode) {
        this.orgCode = orgCode;
        QueryOrgUserRequest request = new QueryOrgUserRequest();
        request.setOrgCode(orgCode);
        RbacServerProxy.get().queryOrgUser(request, new AsyncCallback<RpcResult<QueryOrgUserResponse>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(RpcResult<QueryOrgUserResponse> result) {
                if (result.isSuccess()) {
                    renderResponse(result.getData().getUsers());
                }
            }
        });
    }

    private void renderResponse(List<RbacOrgUserEntity> users) {
        clear();
        for (RbacOrgUserEntity user : users) {
            OrgUserItem item = new OrgUserItem();
            item.setData(user);
            add(item);
            item.addCommonHandler(itemHandler);
        }
    }
}
