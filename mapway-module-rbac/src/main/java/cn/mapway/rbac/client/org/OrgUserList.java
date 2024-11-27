package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.rbac.shared.rpc.QueryOrgUserRequest;
import cn.mapway.rbac.shared.rpc.QueryOrgUserResponse;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

/**
 * 组织用户列表
 */
public class OrgUserList extends VerticalPanel implements HasCommonHandlers {
    OrgUserItem selectedItem = null;
    private final ClickHandler clickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            OrgUserItem item = (OrgUserItem) event.getSource();
            selectItem(item, true);
        }
    };


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
            item.addDomHandler(clickHandler, ClickEvent.getType());
        }
    }
}
