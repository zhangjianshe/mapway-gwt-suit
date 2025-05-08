package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.RbacUserOrg;
import cn.mapway.rbac.shared.rpc.QueryUserOrgRequest;
import cn.mapway.rbac.shared.rpc.QueryUserOrgResponse;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import java.util.List;

/**
 * 用户的所有所属机构
 */
public class UserOrgList extends ZTree {
    public void load(String userId,String systemCode)
    {
        QueryUserOrgRequest request=new QueryUserOrgRequest();
        request.setUserId(userId);
        request.setSystemCode(systemCode);
        RbacServerProxy.get().queryUserOrg(request, new AsyncCallback<RpcResult<QueryUserOrgResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                RbacClient.get().getClientContext().alert(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<QueryUserOrgResponse> result) {
                if(result.isSuccess())
                {
                    renderItems(result.getData().getUserOrgs());
                }
                else {
                    RbacClient.get().getClientContext().alert(result.getMessage());
                }
            }
        });
    }

    private void renderItems(List<RbacUserOrg> userOrgs) {
        clear();
        for(RbacUserOrg org:userOrgs)
        {
            ImageTextItem item = addFontIconItem(null, org.orgName, org.orgIcon);
            item.setData(org);
            item.appendWidget(new Label(org.charger),null);
        }
    }
}
