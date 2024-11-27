package cn.mapway.rbac.client.login;

import cn.mapway.rbac.client.RbacClient;
import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.rpc.LoginRequest;
import cn.mapway.rbac.shared.rpc.LoginResponse;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.ModuleMarker;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

@ModuleMarker(value = LoginFrame.MODULE_CODE,
        name = "用户登录",
        unicode = Fonts.IMAGESTUDIO,
        summary = "Login Frame",
        group = RbacConstant.MODULE_GROUP_RBAC,
        order = 1
)
public class LoginFrame extends BaseAbstractModule {
    public static final String MODULE_CODE = "rbac_login";
    private static final LoginFrameUiBinder ourUiBinder = GWT.create(LoginFrameUiBinder.class);
    @UiField
    Button btnLogin;
    @UiField
    PasswordTextBox txtPassword;
    @UiField
    TextBox txtName;
    @UiField
    Label lbMessage;
    @UiField
    FontIcon logo;

    public LoginFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
        logo.setIconUnicode(Fonts.IOT_PLATFORM);
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }

    @UiHandler("btnLogin")
    public void btnLoginClick(ClickEvent event) {
        LoginRequest request = new LoginRequest();
        request.setUserName(txtName.getValue());
        request.setPassword(txtPassword.getValue());
        RbacServerProxy.get().login(request, new AsyncCallback<RpcResult<LoginResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                lbMessage.setText(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<LoginResponse> result) {
                if (result.isSuccess()) {
                    RbacClient.get().setUser(result.getData().getCurrentUser());
                    RbacClient.get().getClientContext().fireEvent(CommonEvent.loginEvent(result.getData().getCurrentUser()));
                } else {
                    lbMessage.setText(result.getMessage());
                }
            }
        });
    }

    interface LoginFrameUiBinder extends UiBinder<DockLayoutPanel, LoginFrame> {
    }
}