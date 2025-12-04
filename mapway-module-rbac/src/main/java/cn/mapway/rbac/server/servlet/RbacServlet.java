package cn.mapway.rbac.server.servlet;

import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.client.user.RbacUser;
import cn.mapway.rbac.server.RbacServerPlugin;
import cn.mapway.rbac.server.service.*;
import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.rbac.shared.servlet.IRbacServer;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.server.CheckUserServlet;
import cn.mapway.ui.shared.CommonConstant;
import cn.mapway.ui.shared.rpc.RpcResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Iot Server for GWT RPC
 */
@Component
@Slf4j
public class RbacServlet extends CheckUserServlet<IUserInfo> implements IRbacServer {

    @Resource
    RbacServerPlugin rbacServerPlugin;

    @Resource
    LoginExecutor loginExecutor;
    @Resource
    QueryUserResourceExecutor queryUserResourceExecutor;
    @Resource
    QueryRoleResourceExecutor queryRoleResourceExecutor;
    @Resource
    QueryResourceExecutor queryResourceExecutor;
    @Resource
    DeleteOrgUserExecutor deleteOrgUserExecutor;
    @Resource
    UpdateOrgUserExecutor updateOrgUserExecutor;
    @Resource
    QueryOrgUserExecutor queryOrgUserExecutor;
    @Resource
    QueryOrgExecutor queryOrgExecutor;
    @Resource
    DeleteOrgExecutor deleteOrgExecutor;
    @Resource
    UpdateOrgExecutor updateOrgExecutor;
    @Resource
    QueryCurrentUserExecutor queryCurrentUserExecutor;
    @Resource
    DeleteResourceExecutor deleteResourceExecutor;
    @Resource
    CreateResourceExecutor createResourceExecutor;
    @Resource
    UpdateRoleResourceExecutor updateRoleResourceExecutor;
    @Resource
    DeleteRoleResourceExecutor deleteRoleResourceExecutor;
    @Resource
    QueryUserRoleExecutor queryUserRoleExecutor;
    @Resource
    DeleteUserRoleExecutor deleteUserRoleExecutor;
    @Resource
    CreateUserRoleExecutor createUserRoleExecutor;
    @Resource
    QueryRoleExecutor queryRoleExecutor;
    @Resource
    DeleteRoleExecutor deleteRoleExecutor;
    @Resource
    UpdateRoleExecutor updateRoleExecutor;
    @Resource
    QueryUserExecutor queryUserExecutor;
    @Resource
    CreateUserExecutor createUserExecutor;
    @Resource
    DeleteUserExecutor deleteUserExecutor;
    @Resource
    UpdateRbacUserExecutor updateRbacUserExecutor;
    @Resource
    LogoutExecutor logoutExecutor;
    @Resource
    RbacUserService rbacUserService;

    ///CODE_GEN_INSERT_POINT///

    @Resource
    QueryUserRoleResourceExecutor queryUserRoleResourceExecutor;
    @Resource
    QueryUserOrgExecutor queryUserOrgExecutor;

    public void extendCheckToken(List<String> methodList) {
        methodList.add("queryCurrentUser");
        methodList.add("login");
        methodList.add("logout");
    }

    /**
     * when this method is implemented in subclass, findUserByToken getHeadTokenTag will be not called.
     *
     * @return
     */
    @Override
    public IUserInfo requestUser() {
        return rbacServerPlugin.requestUser();
    }

    @Override
    public IUserInfo findUserByToken(String token) {

        RbacUserEntity userByToken = rbacUserService.findUserByToken(token);
        if (userByToken != null) {
            return new RbacUser(userByToken);
        } else {
            return null;
        }
    }

    @Override
    public String getHeadTokenTag() {
        return CommonConstant.API_TOKEN;
    }

    @Override
    public RpcResult<QueryUserRoleResourceResponse> queryUserRoleResource(QueryUserRoleResourceRequest request) {
        BizResult<QueryUserRoleResourceResponse> bizResult = queryUserRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }
    @Override
    public RpcResult<LogoutResponse> logout(LogoutRequest request) {
        BizResult<LogoutResponse> bizResult = logoutExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }
    @Override
    public RpcResult<QueryUserOrgResponse> queryUserOrg(QueryUserOrgRequest request) {
        BizResult<QueryUserOrgResponse> bizResult = queryUserOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }


    @Override
    public RpcResult<DeleteOrgUserResponse> deleteOrgUser(DeleteOrgUserRequest request) {
        BizResult<DeleteOrgUserResponse> bizResult = deleteOrgUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }


    @Override
    public RpcResult<UpdateOrgUserResponse> updateOrgUser(UpdateOrgUserRequest request) {
        BizResult<UpdateOrgUserResponse> bizResult = updateOrgUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryOrgUserResponse> queryOrgUser(QueryOrgUserRequest request) {
        BizResult<QueryOrgUserResponse> bizResult = queryOrgUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryOrgResponse> queryOrg(QueryOrgRequest request) {
        BizResult<QueryOrgResponse> bizResult = queryOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<DeleteOrgResponse> deleteOrg(DeleteOrgRequest request) {
        BizResult<DeleteOrgResponse> bizResult = deleteOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<UpdateOrgResponse> updateOrg(UpdateOrgRequest request) {
        BizResult<UpdateOrgResponse> bizResult = updateOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryCurrentUserResponse> queryCurrentUser(QueryCurrentUserRequest request) {
        BizResult<QueryCurrentUserResponse> bizResult = queryCurrentUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }


    @Override
    public RpcResult<LoginResponse> login(LoginRequest request) {
        BizResult<LoginResponse> bizResult = loginExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryUserResourceResponse> queryUserResource(QueryUserResourceRequest request) {
        BizResult<QueryUserResourceResponse> bizResult = queryUserResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryRoleResourceResponse> queryRoleResource(QueryRoleResourceRequest request) {
        BizResult<QueryRoleResourceResponse> bizResult = queryRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryResourceResponse> queryResource(QueryResourceRequest request) {
        BizResult<QueryResourceResponse> bizResult = queryResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<DeleteResourceResponse> deleteResource(DeleteResourceRequest request) {
        BizResult<DeleteResourceResponse> bizResult = deleteResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<CreateResourceResponse> createResource(CreateResourceRequest request) {
        BizResult<CreateResourceResponse> bizResult = createResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<UpdateRoleResourceResponse> updateRoleResource(UpdateRoleResourceRequest request) {
        BizResult<UpdateRoleResourceResponse> bizResult = updateRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<DeleteRoleResourceResponse> deleteRoleResource(DeleteRoleResourceRequest request) {
        BizResult<DeleteRoleResourceResponse> bizResult = deleteRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryUserRoleResponse> queryUserRole(QueryUserRoleRequest request) {
        BizResult<QueryUserRoleResponse> bizResult = queryUserRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<DeleteUserRoleResponse> deleteUserRole(DeleteUserRoleRequest request) {
        BizResult<DeleteUserRoleResponse> bizResult = deleteUserRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<CreateUserRoleResponse> createUserRole(CreateUserRoleRequest request) {
        BizResult<CreateUserRoleResponse> bizResult = createUserRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryRoleResponse> queryRole(QueryRoleRequest request) {
        BizResult<QueryRoleResponse> bizResult = queryRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<DeleteRoleResponse> deleteRole(DeleteRoleRequest request) {
        BizResult<DeleteRoleResponse> bizResult = deleteRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<UpdateRoleResponse> updateRole(UpdateRoleRequest request) {
        BizResult<UpdateRoleResponse> bizResult = updateRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<QueryUserResponse> queryUser(QueryUserRequest request) {
        BizResult<QueryUserResponse> bizResult = queryUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<CreateUserResponse> createUser(CreateUserRequest request) {
        BizResult<CreateUserResponse> bizResult = createUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<DeleteUserResponse> deleteUser(DeleteUserRequest request) {
        BizResult<DeleteUserResponse> bizResult = deleteUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }

    @Override
    public RpcResult<UpdateRbacUserResponse> updateRbacUser(UpdateRbacUserRequest request) {
        BizResult<UpdateRbacUserResponse> bizResult = updateRbacUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return toRpcResult(bizResult);
    }


    /**
     * 将BizResult 转换为RpcResult
     *
     * @param result
     * @param <T>
     * @return
     */
    private <T> RpcResult<T> toRpcResult(BizResult<T> result) {
        if (result.isFailed()) {
            return RpcResult.create(result.getCode(), result.getMessage(), result.getData());
        } else {
            return RpcResult.create(result.getCode(), result.getMessage(), result.getData());
        }
    }

    /**
     * 构造一个执行环境，上下文中包含了当前用户信息
     *
     * @return
     */
    BizContext getBizContext() {
        BizContext context = new BizContext();
        context.put(CommonConstant.KEY_LOGIN_USER, requestUser());
        return context;
    }

}
