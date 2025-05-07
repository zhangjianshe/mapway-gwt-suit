package cn.mapway.rbac.client.org;

import cn.mapway.rbac.shared.db.postgis.RbacOrgUserEntity;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class OrgUserItem extends CommonEventComposite implements IData<RbacOrgUserEntity> {
    private final static OrgUserItemUiBinder ourUiBinder = GWT.create(OrgUserItemUiBinder.class);
    @UiField
    Label lbName;
    @UiField
    Image avatar;
    @UiField
    Label lbMajor;
    Long errorNo = 0L;
    private RbacOrgUserEntity user;
    public OrgUserItem() {
        initWidget(ourUiBinder.createAndBindUi(this));
        addDomHandler(event -> fireEvent(CommonEvent.selectEvent(user)), ClickEvent.getType());

        avatar.addErrorHandler(event -> {
            if (errorNo > 0) {
                return;
            }
            avatar.setUrl(GWT.getHostPageBaseURL() + "img/avatar.png");
            errorNo++;
        });
    }

    @Override
    public RbacOrgUserEntity getData() {
        return user;
    }

    @Override
    public void setData(RbacOrgUserEntity rbacOrgUserEntity) {
        user = rbacOrgUserEntity;
        toUI();
    }

    private void toUI() {
        lbName.setText(user.getAliasName());
        if (StringUtil.isNotBlank(user.getAvatar())) {
            avatar.setUrl(user.getAvatar());
        } else {
            avatar.setUrl(GWT.getModuleBaseURL() + "img/avatar.png");
        }
        if (user.getMajor() != null && user.getMajor()) {
            lbMajor.getElement().setInnerHTML(Fonts.toHtmlEntity(Fonts.FAVORITE_ON));
        } else {
            lbMajor.getElement().setInnerHTML(Fonts.toHtmlEntity(Fonts.FAVORITE_OFF));
        }
    }

    @UiHandler("lbMajor")
    public void lbMajorClick(ClickEvent event) {
        if (user != null) {
            event.stopPropagation();
            event.preventDefault();
            boolean newValue = !(user.getMajor() != null && user.getMajor());
            user.setMajor(newValue);
            fireEvent(CommonEvent.updateEvent(user));
        }
    }

    interface OrgUserItemUiBinder extends UiBinder<HTMLPanel, OrgUserItem> {
    }
}