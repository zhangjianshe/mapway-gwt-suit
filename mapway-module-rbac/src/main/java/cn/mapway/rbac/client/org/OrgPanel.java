package cn.mapway.rbac.client.org;

import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.tree.TreeItem;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class OrgPanel extends CommonEventComposite {
    private static final OrgPanelUiBinder ourUiBinder = GWT.create(OrgPanelUiBinder.class);
    @UiField
    OrgTree orgTree;
    @UiField
    Button btnAdd;

    @UiField
    Button btnDelete;
    RbacOrgEntity selected;

    public OrgPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        updateUI();
    }

    public void init() {
        orgTree.load();
        selected = null;
        updateUI();
    }

    @UiHandler("orgTree")
    public void orgTreeCommon(CommonEvent event) {
        if (event.isSelect()) {
            TreeItem item = event.getValue();
            selected = (RbacOrgEntity) item.getData();
            updateUI();
            fireEvent(CommonEvent.selectEvent(selected));
        } else if (event.isEdit()) {
            RbacOrgEntity org = event.getValue();
            fireEvent(CommonEvent.editEvent(org));
        }
    }

    @UiHandler("btnAdd")
    public void btnAddClick(ClickEvent event) {
        //添加一个组织结构
        RbacOrgEntity org = new RbacOrgEntity();
        org.setParentId(selected == null ? null : selected.getId());
        org.setName("新组织");
        fireEvent(CommonEvent.editEvent(org));
    }


    @UiHandler("btnDelete")
    public void btnDeleteClick(ClickEvent event) {
        fireEvent(CommonEvent.deleteEvent(selected));
    }

    private void updateUI() {
        btnDelete.setEnabled(selected != null);
    }

    interface OrgPanelUiBinder extends UiBinder<DockLayoutPanel, OrgPanel> {
    }
}