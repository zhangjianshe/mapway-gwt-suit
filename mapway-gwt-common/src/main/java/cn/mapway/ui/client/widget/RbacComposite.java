package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RbacComposite extends Widget implements ICheckRole, HasCommonHandlers {

    protected Map<Integer,String> roleRules = new HashMap<>();

    protected Map<Integer,String> resourceRules = new HashMap<>();

    private static IUserRoleProvider userRoleProvider;

    private boolean isAdminExempt = true;

    @Override
    public void setRole(String role) {
        List<String> list = StringUtil.splitIgnoreBlank(role, ":");
        if (list.size() >= 2) {
            try {
                int type = Integer.parseInt(list.get(0));
                role = list.get(1);
                for (int i = 2; i < list.size(); i++) {
                    role += ":" + list.get(i);
                }
                roleRules.put(type, role);
            }
            catch (Exception e) {
                DomGlobal.console.warn("rule syntax error",role,": <id>:<role>");
            }
        }
        else if(list.size() == 1) {
            if(roleRules.isEmpty()){
                setAllRole(role);
            } else {
                DomGlobal.console.warn("rule syntax error",role,": <id>:<role>");
            }
        } else {
            DomGlobal.console.warn("rule syntax error",role,": <id>:<role>");
        }
        fireEvent(CommonEvent.rbacPermissionChangeEvent(null));
    }

    public void setAllRole(String role) {
        roleRules.put(ALL_TYPE,role);
        fireEvent(CommonEvent.rbacPermissionChangeEvent(null));
    }

    public void setResource(String resource) {
        List<String> list = StringUtil.splitIgnoreBlank(resource, ":");
        if (list.size() >= 2) {
            try {
                int type = Integer.parseInt(list.get(0));
                resource = list.get(1);
                for (int i = 2; i < list.size(); i++) {
                    resource += ":" + list.get(i);
                }
                resourceRules.put(type, resource);
            }
            catch (Exception e) {
                DomGlobal.console.warn("rule syntax error", resource, ": <id>:<role>");
            }
        }
        else if(list.size() == 1) {
            if(resourceRules.isEmpty()){
                setAllRole(resource);
            } else {
                DomGlobal.console.warn("rule syntax error",resource,": <id>:<resource>");
            }
        } else {
            DomGlobal.console.warn("rule syntax error",resource,": <id>:<resource>");
        }
        fireEvent(CommonEvent.rbacPermissionChangeEvent(null));
    }

    public void setAllResource(String resource) {
        resourceRules.put(ALL_TYPE,resource);
        fireEvent(CommonEvent.rbacPermissionChangeEvent(null));
    }


    public static void setUserRoleProvider(IUserRoleProvider provider) {
        userRoleProvider = provider;
    }


    public boolean isAssign(int type){
        // 尚未有用户登陆
        if(userRoleProvider == null){
            return true;
        }
        // 无权限设置
        if(roleRules.isEmpty() && resourceRules.isEmpty()){
            return true;
        }
        // 先尝试角色权限， 若权限通过则直接返回
        String role = StringUtil.isBlank(roleRules.get(ALL_TYPE)) ? roleRules.get(type): roleRules.get(ALL_TYPE);
        if(!StringUtil.isBlank(role)){
            // 设置了角色要求
            if(userRoleProvider.isAssignRoleWithAdminExempt(role, isAdminExempt)){
                return true;
            }
        }
        // 再尝试资源权限
        String resource = StringUtil.isBlank(resourceRules.get(ALL_TYPE)) ? resourceRules.get(type): resourceRules.get(ALL_TYPE);
        if(!StringUtil.isBlank(resource)){
            // 设置了资源要求
            if(userRoleProvider.isAssignResourceWithAdminExempt(resource, isAdminExempt)){
                return true;
            }
        }
        return false;
    }

    public void setAdminExempt(boolean adminExempt) {
        isAdminExempt = adminExempt;
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return addHandler(handler, CommonEvent.TYPE);
    }

}
