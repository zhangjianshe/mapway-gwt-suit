package cn.mapway.rbac.server.api;

import cn.mapway.biz.api.ApiResult;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.document.annotation.Doc;
import cn.mapway.rbac.server.service.*;
import cn.mapway.rbac.shared.rpc.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/rbac")
@Doc(value = "用户角色权限", group = "/服务", desc = "")
public class RbacController extends ApiBaseController {


    @Resource
    QueryUserOrgExecutor queryUserOrgExecutor;
    /**
     * QueryUserOrg
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryUserOrg", retClazz = {QueryUserOrgResponse.class})
    @RequestMapping(value = "/queryUserOrg", method = RequestMethod.POST)
    public ApiResult<QueryUserOrgResponse> queryUserOrg(@RequestBody QueryUserOrgRequest request) {
        BizResult<QueryUserOrgResponse> bizResult = queryUserOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryUserRoleResourceExecutor queryUserRoleResourceExecutor;
    /**
     * QueryUserRoleResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryUserRoleResource", retClazz = {QueryUserRoleResourceResponse.class})
    @RequestMapping(value = "/queryUserRoleResource", method = RequestMethod.POST)
    public ApiResult<QueryUserRoleResourceResponse> queryUserRoleResource(@RequestBody QueryUserRoleResourceRequest request) {
        BizResult<QueryUserRoleResourceResponse> bizResult = queryUserRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    UpdateOrgExecutor updateOrgExecutor;
    /**
     * UpdateOrg
     *
     * @param request request
     * @return data
     */
    @Doc(value = "UpdateOrg", retClazz = {UpdateOrgResponse.class})
    @RequestMapping(value = "/updateOrg", method = RequestMethod.POST)
    public ApiResult<UpdateOrgResponse> updateOrg(@RequestBody UpdateOrgRequest request) {
        BizResult<UpdateOrgResponse> bizResult = updateOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteOrgExecutor deleteOrgExecutor;
    /**
     * DeleteOrg
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteOrg", retClazz = {DeleteOrgResponse.class})
    @RequestMapping(value = "/deleteOrg", method = RequestMethod.POST)
    public ApiResult<DeleteOrgResponse> deleteOrg(@RequestBody DeleteOrgRequest request) {
        BizResult<DeleteOrgResponse> bizResult = deleteOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryOrgExecutor queryOrgExecutor;
    /**
     * QueryOrg
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryOrg", retClazz = {QueryOrgResponse.class})
    @RequestMapping(value = "/queryOrg", method = RequestMethod.POST)
    public ApiResult<QueryOrgResponse> queryOrg(@RequestBody QueryOrgRequest request) {
        BizResult<QueryOrgResponse> bizResult = queryOrgExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryOrgUserExecutor queryOrgUserExecutor;
    /**
     * QueryOrgUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryOrgUser", retClazz = {QueryOrgUserResponse.class})
    @RequestMapping(value = "/queryOrgUser", method = RequestMethod.POST)
    public ApiResult<QueryOrgUserResponse> queryOrgUser(@RequestBody QueryOrgUserRequest request) {
        BizResult<QueryOrgUserResponse> bizResult = queryOrgUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    UpdateOrgUserExecutor updateOrgUserExecutor;
    /**
     * UpdateOrgUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "UpdateOrgUser", retClazz = {UpdateOrgUserResponse.class})
    @RequestMapping(value = "/updateOrgUser", method = RequestMethod.POST)
    public ApiResult<UpdateOrgUserResponse> updateOrgUser(@RequestBody UpdateOrgUserRequest request) {
        BizResult<UpdateOrgUserResponse> bizResult = updateOrgUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteOrgUserExecutor deleteOrgUserExecutor;
    /**
     * DeleteOrgUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteOrgUser", retClazz = {DeleteOrgUserResponse.class})
    @RequestMapping(value = "/deleteOrgUser", method = RequestMethod.POST)
    public ApiResult<DeleteOrgUserResponse> deleteOrgUser(@RequestBody DeleteOrgUserRequest request) {
        BizResult<DeleteOrgUserResponse> bizResult = deleteOrgUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    LoginExecutor loginExecutor;
    /**
     * Login
     *
     * @param request request
     * @return data
     */
    @Doc(value = "Login", retClazz = {LoginResponse.class})
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ApiResult<LoginResponse> login(@RequestBody LoginRequest request) {
        BizResult<LoginResponse> bizResult = loginExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }
    @Resource
    QueryCurrentUserExecutor queryCurrentUserExecutor;
    /**
     * QueryCurrentUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryCurrentUser", retClazz = {QueryCurrentUserResponse.class})
    @RequestMapping(value = "/queryCurrentUser", method = RequestMethod.POST)
    public ApiResult<QueryCurrentUserResponse> queryCurrentUser(@RequestBody QueryCurrentUserRequest request) {
        BizResult<QueryCurrentUserResponse> bizResult = queryCurrentUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    UpdateRbacUserExecutor updateRbacUserExecutor;
    /**
     * UpdateUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "UpdateRbacUser", retClazz = {UpdateRbacUserResponse.class})
    @RequestMapping(value = "/updateRbacUser", method = RequestMethod.POST)
    public ApiResult<UpdateRbacUserResponse> updateUser(@RequestBody UpdateRbacUserRequest request) {
        BizResult<UpdateRbacUserResponse> bizResult = updateRbacUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteUserExecutor deleteUserExecutor;
    /**
     * DeleteUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteUser", retClazz = {DeleteUserResponse.class})
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public ApiResult<DeleteUserResponse> deleteUser(@RequestBody DeleteUserRequest request) {
        BizResult<DeleteUserResponse> bizResult = deleteUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    CreateUserExecutor createUserExecutor;
    /**
     * CreateUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "CreateUser", retClazz = {CreateUserResponse.class})
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ApiResult<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        BizResult<CreateUserResponse> bizResult = createUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryUserExecutor queryUserExecutor;
    /**
     * QueryUser
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryUser", retClazz = {QueryUserResponse.class})
    @RequestMapping(value = "/queryUser", method = RequestMethod.POST)
    public ApiResult<QueryUserResponse> queryUser(@RequestBody QueryUserRequest request) {
        BizResult<QueryUserResponse> bizResult = queryUserExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    UpdateRoleExecutor updateRoleExecutor;
    /**
     * UpdateRole
     *
     * @param request request
     * @return data
     */
    @Doc(value = "UpdateRole", retClazz = {UpdateRoleResponse.class})
    @RequestMapping(value = "/updateRole", method = RequestMethod.POST)
    public ApiResult<UpdateRoleResponse> updateRole(@RequestBody UpdateRoleRequest request) {
        BizResult<UpdateRoleResponse> bizResult = updateRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteRoleExecutor deleteRoleExecutor;
    /**
     * DeleteRole
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteRole", retClazz = {DeleteRoleResponse.class})
    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
    public ApiResult<DeleteRoleResponse> deleteRole(@RequestBody DeleteRoleRequest request) {
        BizResult<DeleteRoleResponse> bizResult = deleteRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryRoleExecutor queryRoleExecutor;
    /**
     * QueryRole
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryRole", retClazz = {QueryRoleResponse.class})
    @RequestMapping(value = "/queryRole", method = RequestMethod.POST)
    public ApiResult<QueryRoleResponse> queryRole(@RequestBody QueryRoleRequest request) {
        BizResult<QueryRoleResponse> bizResult = queryRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    CreateUserRoleExecutor createUserRoleExecutor;
    /**
     * CreateUserRole
     *
     * @param request request
     * @return data
     */
    @Doc(value = "CreateUserRole", retClazz = {CreateUserRoleResponse.class})
    @RequestMapping(value = "/createUserRole", method = RequestMethod.POST)
    public ApiResult<CreateUserRoleResponse> createUserRole(@RequestBody CreateUserRoleRequest request) {
        BizResult<CreateUserRoleResponse> bizResult = createUserRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteUserRoleExecutor deleteUserRoleExecutor;
    /**
     * DeleteUserRole
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteUserRole", retClazz = {DeleteUserRoleResponse.class})
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.POST)
    public ApiResult<DeleteUserRoleResponse> deleteUserRole(@RequestBody DeleteUserRoleRequest request) {
        BizResult<DeleteUserRoleResponse> bizResult = deleteUserRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryUserRoleExecutor queryUserRoleExecutor;
    /**
     * QueryUserRole
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryUserRole", retClazz = {QueryUserRoleResponse.class})
    @RequestMapping(value = "/queryUserRole", method = RequestMethod.POST)
    public ApiResult<QueryUserRoleResponse> queryUserRole(@RequestBody QueryUserRoleRequest request) {
        BizResult<QueryUserRoleResponse> bizResult = queryUserRoleExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteRoleResourceExecutor deleteRoleResourceExecutor;
    /**
     * DeleteRoleResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteRoleResource", retClazz = {DeleteRoleResourceResponse.class})
    @RequestMapping(value = "/deleteRoleResource", method = RequestMethod.POST)
    public ApiResult<DeleteRoleResourceResponse> deleteRoleResource(@RequestBody DeleteRoleResourceRequest request) {
        BizResult<DeleteRoleResourceResponse> bizResult = deleteRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    UpdateRoleResourceExecutor updateRoleResourceExecutor;
    /**
     * UpdateRoleResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "UpdateRoleResource", retClazz = {UpdateRoleResourceResponse.class})
    @RequestMapping(value = "/updateRoleResource", method = RequestMethod.POST)
    public ApiResult<UpdateRoleResourceResponse> updateRoleResource(@RequestBody UpdateRoleResourceRequest request) {
        BizResult<UpdateRoleResourceResponse> bizResult = updateRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    CreateResourceExecutor createResourceExecutor;
    /**
     * CreateResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "CreateResource", retClazz = {CreateResourceResponse.class})
    @RequestMapping(value = "/createResource", method = RequestMethod.POST)
    public ApiResult<CreateResourceResponse> createResource(@RequestBody CreateResourceRequest request) {
        BizResult<CreateResourceResponse> bizResult = createResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    DeleteResourceExecutor deleteResourceExecutor;
    /**
     * UpdateResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "DeleteResource", retClazz = {DeleteResourceResponse.class})
    @RequestMapping(value = "/deleteResource", method = RequestMethod.POST)
    public ApiResult<DeleteResourceResponse> updateResource(@RequestBody DeleteResourceRequest request) {
        BizResult<DeleteResourceResponse> bizResult = deleteResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryResourceExecutor queryResourceExecutor;
    /**
     * QueryResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryResource", retClazz = {QueryResourceResponse.class})
    @RequestMapping(value = "/queryResource", method = RequestMethod.POST)
    public ApiResult<QueryResourceResponse> queryResource(@RequestBody QueryResourceRequest request) {
        BizResult<QueryResourceResponse> bizResult = queryResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryRoleResourceExecutor queryRoleResourceExecutor;
    /**
     * QueryRoleResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryRoleResource", retClazz = {QueryRoleResourceResponse.class})
    @RequestMapping(value = "/queryRoleResource", method = RequestMethod.POST)
    public ApiResult<QueryRoleResourceResponse> queryRoleResource(@RequestBody QueryRoleResourceRequest request) {
        BizResult<QueryRoleResourceResponse> bizResult = queryRoleResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }

    @Resource
    QueryUserResourceExecutor queryUserResourceExecutor;
    /**
     * QueryUserResource
     *
     * @param request request
     * @return data
     */
    @Doc(value = "QueryUserResource", retClazz = {QueryUserResourceResponse.class})
    @RequestMapping(value = "/queryUserResource", method = RequestMethod.POST)
    public ApiResult<QueryUserResourceResponse> queryUserResource(@RequestBody QueryUserResourceRequest request) {
        BizResult<QueryUserResourceResponse> bizResult = queryUserResourceExecutor.execute(getBizContext(), BizRequest.wrap("", request));
        return bizResult.toApiResult();
    }
}
