package cn.mapway.ui.client.widget;

public interface IUserRoleProvider {
    boolean isAssignRole(String role);
    boolean isAssignRoleWithAdminExempt(String role, boolean adminExempt);

    boolean isAssignResource(String role);
    boolean isAssignResourceWithAdminExempt(String role, boolean adminExempt);
}
