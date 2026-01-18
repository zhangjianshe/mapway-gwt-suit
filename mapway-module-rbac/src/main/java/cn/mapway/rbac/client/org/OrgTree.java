package cn.mapway.rbac.client.org;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.rbac.shared.rpc.QueryOrgRequest;
import cn.mapway.rbac.shared.rpc.QueryOrgResponse;
import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.buttons.EditButton;
import cn.mapway.ui.client.widget.tree.Tree;
import cn.mapway.ui.client.widget.tree.TreeItem;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import java.util.*;

/**
 * 组织树
 */
public class OrgTree extends Tree {
    private final ClickHandler editHandler = event -> {
        event.stopPropagation();
        event.preventDefault();
        EditButton source = (EditButton) event.getSource();
        RbacOrgEntity org = (RbacOrgEntity) source.getData();
        fireEvent(CommonEvent.editEvent(org));
    };
    Map<String, List<RbacOrgEntity>> orgTemp = new HashMap<>();

    public void load() {
        QueryOrgRequest request = new QueryOrgRequest();
        RbacServerProxy.get().queryOrg(request, new AsyncCallback<RpcResult<QueryOrgResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                fireEvent(CommonEvent.messageEvent(MessageObject.info(0, caught.getMessage())));
            }

            @Override
            public void onSuccess(RpcResult<QueryOrgResponse> result) {
                if (result.isSuccess()) {
                    renderResponse(result.getData().getOrgs());
                } else {
                    fireEvent(CommonEvent.messageEvent(MessageObject.info(0, result.getMessage())));
                }
            }
        });
    }

    private void renderResponse(List<RbacOrgEntity> orgs) {
        clear();

        // the org is a tree, first we need build the tree
        orgTemp.clear();
        List<RbacOrgEntity> roots = new ArrayList<>();
        for (RbacOrgEntity org : orgs) {
            if (StringUtil.isBlank(org.getParentId())) {
                //根节点
                roots.add(org);
            } else {
                List<RbacOrgEntity> list = orgTemp.get(org.getParentId());
                if (list == null) {
                    list = new ArrayList<>();
                    orgTemp.put(org.getParentId(), list);
                }
                list.add(org);
            }
        }
        Collections.sort(roots, Comparator.comparing(RbacOrgEntity::getRank));
        recursiveRenderTree(null, roots);

    }

    private void recursiveRenderTree(TreeItem parent, List<RbacOrgEntity> roots) {

        if (roots == null || roots.size() == 0) {
            return;
        }
        for (RbacOrgEntity org : roots) {
            TreeItem item = addItem(parent, org.getName(), org.getIcon());
            item.setData(org);
            Label label = new Label();
            label.setText(org.getCode());
            label.setStyleName("ai-summary");
            if (org.getDefaultOrg() != null && org.getDefaultOrg()) {
                label.addStyleName("ai-bold");
            }
            item.appendRightWidget(label, null);

            EditButton editButton = new EditButton();
            editButton.setData(org);
            editButton.addClickHandler(editHandler);
            item.appendRightWidget(editButton, 26);

            List<RbacOrgEntity> children = orgTemp.get(org.getId());
            recursiveRenderTree(item, children);
        }
    }
}
