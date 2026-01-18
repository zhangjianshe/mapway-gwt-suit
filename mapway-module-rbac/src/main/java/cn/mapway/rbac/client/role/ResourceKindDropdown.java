package cn.mapway.rbac.client.role;

import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.ui.client.widget.Dropdown;

public class ResourceKindDropdown extends Dropdown {
    public ResourceKindDropdown()
    {
        for (ResourceKind kind : ResourceKind.values()) {
            addItem("",kind.getName(),kind.getCode(),true);
        }
    }
}
