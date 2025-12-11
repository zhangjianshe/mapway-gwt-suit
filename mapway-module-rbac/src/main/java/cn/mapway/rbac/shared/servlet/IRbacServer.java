package cn.mapway.rbac.shared.servlet;

import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath(RbacConstant.DEFAULT_SERVER_PATH)
public interface IRbacServer extends RemoteService {
    ///CODE_GEN_INSERT_POINT///
    RpcResult<QueryUserRoleResourceResponse> queryUserRoleResource(QueryUserRoleResourceRequest request);

    RpcResult<QueryUserOrgResponse> queryUserOrg(QueryUserOrgRequest request);

    RpcResult<DeleteOrgUserResponse> deleteOrgUser(DeleteOrgUserRequest request);

    RpcResult<UpdateOrgUserResponse> updateOrgUser(UpdateOrgUserRequest request);

    RpcResult<QueryOrgUserResponse> queryOrgUser(QueryOrgUserRequest request);

    RpcResult<QueryOrgResponse> queryOrg(QueryOrgRequest request);

    RpcResult<DeleteOrgResponse> deleteOrg(DeleteOrgRequest request);

    RpcResult<UpdateOrgResponse> updateOrg(UpdateOrgRequest request);

    RpcResult<QueryCurrentUserResponse> queryCurrentUser(QueryCurrentUserRequest request);

    RpcResult<LoginResponse> login(LoginRequest request);

    RpcResult<LogoutResponse> logout(LogoutRequest request);

    RpcResult<QueryUserResourceResponse> queryUserResource(QueryUserResourceRequest request);

    RpcResult<QueryRoleResourceResponse> queryRoleResource(QueryRoleResourceRequest request);

    RpcResult<QueryResourceResponse> queryResource(QueryResourceRequest request);

    RpcResult<DeleteResourceResponse> deleteResource(DeleteResourceRequest request);

    RpcResult<CreateResourceResponse> createResource(CreateResourceRequest request);

    RpcResult<UpdateRoleResourceResponse> updateRoleResource(UpdateRoleResourceRequest request);

    RpcResult<DeleteRoleResourceResponse> deleteRoleResource(DeleteRoleResourceRequest request);

    RpcResult<QueryUserRoleResponse> queryUserRole(QueryUserRoleRequest request);

    RpcResult<DeleteUserRoleResponse> deleteUserRole(DeleteUserRoleRequest request);

    RpcResult<CreateUserRoleResponse> createUserRole(CreateUserRoleRequest request);

    RpcResult<QueryRoleResponse> queryRole(QueryRoleRequest request);

    RpcResult<DeleteRoleResponse> deleteRole(DeleteRoleRequest request);

    RpcResult<UpdateRoleResponse> updateRole(UpdateRoleRequest request);

    RpcResult<QueryUserResponse> queryUser(QueryUserRequest request);

    RpcResult<CreateUserResponse> createUser(CreateUserRequest request);

    RpcResult<DeleteUserResponse> deleteUser(DeleteUserRequest request);

    RpcResult<UpdateRbacUserResponse> updateRbacUser(UpdateRbacUserRequest request);

}
