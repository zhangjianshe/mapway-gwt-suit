package cn.mapway.rbac.server.service;

import cn.mapway.biz.core.AbstractBizExecutor;
import cn.mapway.biz.core.BizContext;
import cn.mapway.biz.core.BizRequest;
import cn.mapway.biz.core.BizResult;
import cn.mapway.rbac.server.dao.RbacResourceDao;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.rbac.shared.rpc.CreateResourceRequest;
import cn.mapway.rbac.shared.rpc.CreateResourceResponse;
import cn.mapway.ui.client.IUserInfo;
import cn.mapway.ui.shared.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * CreateResourceExecutor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Component
@Slf4j
public class CreateResourceExecutor extends AbstractBizExecutor<CreateResourceResponse, CreateResourceRequest> {

    @Resource
    RbacUserService rbacUserService;
    @Resource
    RbacResourceDao rbacResourceDao;

    @Override
    protected BizResult<CreateResourceResponse> process(BizContext context, BizRequest<CreateResourceRequest> bizParam) {
        CreateResourceRequest request = bizParam.getData();
        log.info("CreateResourceExecutor {}", Json.toJson(request, JsonFormat.compact()));
        IUserInfo user = (IUserInfo) context.get(CommonConstant.KEY_LOGIN_USER);

        //判断权限
        BizResult<Boolean> assignResource = rbacUserService.isAssignResource(user, null, RbacConstant.RESOURCE_RBAC_MAINTAINER);
        if(assignResource.isFailed()){
            return assignResource.asBizResult();
        }

        assertNotNull(request.getResource(), "没有资源数据");
        assertNotNull(request.getResource().getKind(), "资源点类型");
        assertTrue(Strings.isNotBlank(request.getResource().getResourceCode()), "资源点编码");
        // todo only system manager can operator this function
        //创建资源点　一般会在系统初始化的时候初始化这张表
        //也允许ＵＩ创建　但是创建完成并不会其作用　因为没有代码参与其中
        //为了与其他系统对接　可以创建资源点　通过API的形式供第三方调用

        rbacResourceDao.insertOrUpdate(request.getResource());

        return BizResult.success(new CreateResourceResponse());
    }
}
