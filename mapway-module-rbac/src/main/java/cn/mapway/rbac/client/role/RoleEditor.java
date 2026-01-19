package cn.mapway.rbac.client.role;

import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 角色编辑
 */
public class RoleEditor extends CommonEventComposite implements IData<RbacRole> {

    private static final RoleEditorUiBinder ourUiBinder = GWT.create(RoleEditorUiBinder.class);
    private static Dialog<RoleEditor> dialog;
    @UiField
    TextBox txtName;
    @UiField
    TextBox txtCode;
    @UiField
    TextArea txtSummary;
    @UiField
    SaveBar saveBar;
    @UiField
    CheckBox checkSystemCode;
    private RbacRole role;

    public RoleEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public static Dialog<RoleEditor> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }

    private static Dialog<RoleEditor> createOne() {
        RoleEditor editor = new RoleEditor();
        return new Dialog(editor, "角色编辑");
    }

    @Override
    public RbacRole getData() {
        return role;
    }

    @Override
    public void setData(RbacRole rbacRole) {
        role = rbacRole;
        toUI();
    }

    private void toUI() {
        txtCode.setText(role.code);
        txtName.setText(role.name);
        txtSummary.setText(role.summary);
        txtCode.setEnabled(StringUtil.isBlank(role.code));
        checkSystemCode.setValue(role.systemRole);
    }

    public void msg(String message) {
        saveBar.msg(message);
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(800, 550);
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            fromUI();
            fireEvent(CommonEvent.okEvent(role));
        } else {
            fireEvent(event);
        }
    }

    private void fromUI() {
        role.code = txtCode.getValue();
        role.name = txtName.getValue();
        role.summary = txtSummary.getValue();
        role.systemRole = checkSystemCode.getValue();
    }

    interface RoleEditorUiBinder extends UiBinder<DockLayoutPanel, RoleEditor> {
    }
}