package cn.mapway.rbac.shared.servlet;

import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IRbacServerAsync {
    ///CODE_GEN_INSERT_POINT///
	void deleteOrgUser(DeleteOrgUserRequest request, AsyncCallback<RpcResult<DeleteOrgUserResponse>> async);

	void updateOrgUser(UpdateOrgUserRequest request, AsyncCallback<RpcResult<UpdateOrgUserResponse>> async);

	void queryOrgUser(QueryOrgUserRequest request, AsyncCallback<RpcResult<QueryOrgUserResponse>> async);

	void queryOrg(QueryOrgRequest request, AsyncCallback<RpcResult<QueryOrgResponse>> async);

	void deleteOrg(DeleteOrgRequest request, AsyncCallback<RpcResult<DeleteOrgResponse>> async);

	void updateOrg(UpdateOrgRequest request, AsyncCallback<RpcResult<UpdateOrgResponse>> async);

	void queryCurrentUser(QueryCurrentUserRequest request, AsyncCallback<RpcResult<QueryCurrentUserResponse>> async);

	void login(LoginRequest request, AsyncCallback<RpcResult<LoginResponse>> async);

    void queryUserResource(QueryUserResourceRequest request, AsyncCallback<RpcResult<QueryUserResourceResponse>> async);

    void queryRoleResource(QueryRoleResourceRequest request, AsyncCallback<RpcResult<QueryRoleResourceResponse>> async);

    void queryResource(QueryResourceRequest request, AsyncCallback<RpcResult<QueryResourceResponse>> async);

    void deleteResource(DeleteResourceRequest request, AsyncCallback<RpcResult<DeleteResourceResponse>> async);

    void createResource(CreateResourceRequest request, AsyncCallback<RpcResult<CreateResourceResponse>> async);

    void updateRoleResource(UpdateRoleResourceRequest request, AsyncCallback<RpcResult<UpdateRoleResourceResponse>> async);

    void deleteRoleResource(DeleteRoleResourceRequest request, AsyncCallback<RpcResult<DeleteRoleResourceResponse>> async);

    void queryUserRole(QueryUserRoleRequest request, AsyncCallback<RpcResult<QueryUserRoleResponse>> async);

    void deleteUserRole(DeleteUserRoleRequest request, AsyncCallback<RpcResult<DeleteUserRoleResponse>> async);

    void createUserRole(CreateUserRoleRequest request, AsyncCallback<RpcResult<CreateUserRoleResponse>> async);

    void queryRole(QueryRoleRequest request, AsyncCallback<RpcResult<QueryRoleResponse>> async);

    void deleteRole(DeleteRoleRequest request, AsyncCallback<RpcResult<DeleteRoleResponse>> async);

    void updateRole(UpdateRoleRequest request, AsyncCallback<RpcResult<UpdateRoleResponse>> async);

    void queryUser(QueryUserRequest request, AsyncCallback<RpcResult<QueryUserResponse>> async);

    void createUser(CreateUserRequest request, AsyncCallback<RpcResult<CreateUserResponse>> async);

    void deleteUser(DeleteUserRequest request, AsyncCallback<RpcResult<DeleteUserResponse>> async);

    void updateRbacUser(UpdateRbacUserRequest request, AsyncCallback<RpcResult<UpdateRbacUserResponse>> async);

}

