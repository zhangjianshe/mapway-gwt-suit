package cn.mapway.rbac.client.role;

import cn.mapway.rbac.shared.RbacRole;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class RoleSelector extends CommonEventComposite {
    private static final RoleSelectorUiBinder ourUiBinder = GWT.create(RoleSelectorUiBinder.class);
    private static Dialog<RoleSelector> dialog;
    @UiField
    RoleTree roleTree;
    @UiField
    SaveBar saveBar;
    RbacRole selectedRole;

    public RoleSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public static Dialog<RoleSelector> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }

    private static Dialog<RoleSelector> createOne() {
        RoleSelector roleSelector = new RoleSelector();
        return new Dialog<RoleSelector>(roleSelector, "角色选择");
    }

    /**
     * load role tree from server
     */
    public void load() {
        roleTree.load();
    }

    @UiHandler("roleTree")
    public void roleTreeCommon(CommonEvent event) {
        if (event.isSelect()) {
            selectedRole = event.getValue();
            updateUI();
        }
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            fireEvent(CommonEvent.okEvent(selectedRole));
        } else {
            fireEvent(event);
        }
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(500, 700);
    }

    private void updateUI() {
        saveBar.setEnableSave(selectedRole != null);
    }

    public void setEnableEditor(boolean enableEditor) {
        roleTree.setEnableEditor(enableEditor);
    }

    interface RoleSelectorUiBinder extends UiBinder<DockLayoutPanel, RoleSelector> {
    }
}