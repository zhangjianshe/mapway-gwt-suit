package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.rbac.shared.rpc.QueryOrgRequest;
import cn.mapway.rbac.shared.rpc.QueryOrgResponse;
import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织树
 */
public class OrgTree extends ZTree {
    public void load()
    {
        QueryOrgRequest request=new QueryOrgRequest();
        RbacServerProxy.get().queryOrg(request, new AsyncCallback<RpcResult<QueryOrgResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                fireEvent(CommonEvent.messageEvent(MessageObject.info(0, caught.getMessage())));
            }

            @Override
            public void onSuccess(RpcResult<QueryOrgResponse> result) {
                if(result.isSuccess()){
                    renderResponse(result.getData().getOrgs());
                }
                else {
                    fireEvent(CommonEvent.messageEvent(MessageObject.info(0, result.getMessage())));
                }
            }
        });
    }
    Map<String,List<RbacOrgEntity>> orgTemp=new HashMap<>();
    private void renderResponse(List<RbacOrgEntity> orgs) {
        clear();

        // the org is a tree, first we need build the tree
        orgTemp.clear();
        List<RbacOrgEntity> roots=new ArrayList<>();
        for(RbacOrgEntity org:orgs){
            if(StringUtil.isBlank(org.getParentId()))
            {
                //根节点
                roots.add(org);
            }
            else {
                List<RbacOrgEntity> list = orgTemp.get(org.getParentId());
                if(list==null)
                {
                    list=new ArrayList<>();
                    orgTemp.put(org.getParentId(),list);
                }
                list.add(org);
            }
        }
        recursiveRenderTree(null,roots);

    }

    private void recursiveRenderTree(ImageTextItem parent, List<RbacOrgEntity> roots) {

        if (roots == null || roots.size() == 0) {
            return;
        }
        for (RbacOrgEntity org : roots) {
            ImageTextItem item = addItem(parent, org.getName(), null);
            item.setData(org);

            List<RbacOrgEntity> children = orgTemp.get(org.getId());
            recursiveRenderTree(item,children);
        }
    }
}
