package cn.mapway.rbac.client.org;

import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class OrgUserItem extends CommonEventComposite implements IData<RbacOrgUserEntity> {
    private RbacOrgUserEntity user;

    @Override
    public RbacOrgUserEntity getData() {
        return user;
    }

    @Override
    public void setData(RbacOrgUserEntity rbacOrgUserEntity) {
        user=rbacOrgUserEntity;
        toUI();
    }

    private void toUI() {
        lbName.setText(user.getAliasName());
        if(StringUtil.isNotBlank(user.getAvatar())) {
            avatar.setUrl(user.getAvatar());
        }
        else {
            avatar.setUrl(GWT.getModuleBaseURL() + "img/avatar.png");
        }
    }


    interface OrgUserItemUiBinder extends UiBinder<HTMLPanel, OrgUserItem> {
    }

    private final static OrgUserItemUiBinder ourUiBinder = GWT.create(OrgUserItemUiBinder.class);
    @UiField
    Label lbName;
    @UiField
    Image avatar;

    public OrgUserItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}