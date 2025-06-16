package cn.mapway.rbac.server.service;

import cn.mapway.rbac.server.dao.RbacResourceDao;
import cn.mapway.rbac.server.dao.RbacRoleResourceDao;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.db.postgis.RbacRoleResourceEntity;
import cn.mapway.rbac.shared.rpc.RbacResourceOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.trans.Trans;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RbacResourceService {

    @Resource
    RbacResourceDao rbacResourceDao;
    @Resource
    RbacRoleResourceDao rbacRoleResourceDao;

    /**
     * 添加资源
     *
     * @param resource
     */
    public boolean addResource(RbacResourceEntity resource) {
        boolean flag = checkResource(resource);
        if (!flag) {
            return false;
        }
        RbacResourceEntity insert = rbacResourceDao.insert(resource);
        return insert != null;
    }

    /**
     * 批量添加资源
     *
     * @param resources
     */
    public List<RbacResourceEntity> addResources(List<RbacResourceEntity> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        List<RbacResourceEntity> result = resources.stream()
                .filter(this::checkResource)
                .collect(Collectors.toList());
        if (!result.isEmpty()) {
            Dao dao = rbacResourceDao.getDao();
            dao.insert(result);
            return result;
        }
        return new ArrayList<>();
    }

    private boolean checkResource(RbacResourceEntity resource) {
        if (resource == null) {
            return false;
        }
        if (StringUtils.isEmpty(resource.getResourceCode())) {
            return false;
        }
        if (resource.getKind() == null) {
            return false;
        }
        if (StringUtils.isEmpty(resource.getName())) {
            return false;
        }
        RbacResourceEntity fetch = rbacResourceDao.fetch(
                Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "=", resource.getResourceCode())
                        .and(RbacResourceEntity.FLD_KIND, "=", resource.getKind())
        );
        return fetch == null;
    }


    /**
     * 删除资源
     *
     * @param resource
     */
    public boolean removeResource(RbacResourceEntity resource) {
        boolean checkFlag = checkRemoveResource(resource);
        if (!checkFlag) {
            return false;
        }
        Trans.exec(() -> {
            rbacResourceDao.delete(resource.getResourceCode());
            rbacRoleResourceDao.clear(Cnd.where(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "=", resource.getResourceCode()));
        });
        return true;
    }

    /**
     * 批量删除资源
     *
     * @param resources
     */
    public int removeResources(List<RbacResourceEntity> resources) {
        if (resources == null || resources.isEmpty()) {
            return 0;
        }
        List<String> deleteList = resources.stream()
                .filter(this::checkRemoveResource)
                .map(RbacResourceEntity::getResourceCode)
                .collect(Collectors.toList());
        if(deleteList.isEmpty()) {
            return 0;
        }
        Trans.exec(() -> {
            rbacResourceDao.clear(Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "in", deleteList));
            rbacRoleResourceDao.clear(Cnd.where(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "in", deleteList));
        });
        return deleteList.size();
    }


    public boolean updateResource(RbacResourceEntity entity) {
        boolean checkFlag = checkRemoveResource(entity);
        if (!checkFlag) {
            return false;
        }
        rbacResourceDao.update(entity);
        return true;
    }

    public int updateResources(List<RbacResourceEntity> resources) {
        if (resources == null || resources.isEmpty()) {
            return 0;
        }
        List<RbacResourceEntity> updateList = resources.stream()
                .filter(this::checkRemoveResource)
                .collect(Collectors.toList());
        if(updateList.isEmpty()) {
            return 0;
        }
        int updateCount = 0;
        for (RbacResourceEntity resource : updateList) {
            if(rbacResourceDao.update(resource) > 0){
                updateCount ++;
            }
        }
        return updateCount;
    }

    public boolean checkRemoveResource(RbacResourceEntity resource) {
        if(resource == null){
            return false;
        }
        String resourceCode = resource.getResourceCode();
        if(StringUtils.isEmpty(resourceCode)){
            return false;
        }
        RbacResourceEntity fetch = rbacResourceDao.fetch(resourceCode);
        if(fetch == null){
            return false;
        }
        return true;
    }

    public List<RbacResourceOperation> syncResource(List<RbacResourceEntity> newList, ResourceKind kind) {
        if (newList == null) {
            throw new NullPointerException(" newList is null");
        }
        if (kind == null) {
            throw new NullPointerException(" kind is null");
        }
        List<RbacResourceOperation> result = new ArrayList<>();
        Map<String, RbacResourceEntity> newCache = new HashMap<>();
        for (RbacResourceEntity resourceEntity : newList) {
            newCache.put(resourceEntity.getResourceCode(), resourceEntity);
        }
        List<RbacResourceEntity> addList = new ArrayList<>();
        List<RbacResourceEntity> removeList = new ArrayList<>();
        List<RbacResourceEntity> updateList = new ArrayList<>();

        synchronized (kind.getName()) {
            List<RbacResourceEntity> oldList = rbacResourceDao.query(Cnd.where(RbacResourceEntity.FLD_KIND, "=", kind.code));
            if (oldList == null) {
                oldList = new ArrayList<>();
            }
            for (RbacResourceEntity oldEntity : oldList) {
                String key = oldEntity.getResourceCode();
                if (newCache.containsKey(key)) {
                    // 判断是否需要更新
                    RbacResourceEntity newEntity = newCache.get(key);
                    if (!oldEntity.equals(newEntity)) {
                        updateList.add(newEntity);
                    }
                    // 之后从map中删除
                    newCache.remove(key);
                } else {
                    removeList.add(oldEntity);
                }
            }
            for (String key : newCache.keySet()) {
                addList.add(newCache.get(key));
            }

            // 更新数据库
            // 新增
            if (!addList.isEmpty()) {
                List<RbacResourceEntity> rbacResourceEntities = addResources(addList);
                for (RbacResourceEntity rbacResourceEntity : rbacResourceEntities) {
                    rbacResourceEntity.setResourceCode(null);
                    result.add(new RbacResourceOperation(rbacResourceEntity, RbacResourceOperation.OPERATION_ADD));
                }
            }
            // 删除
            if (!removeList.isEmpty()) {
                List<String> deleteKeyList = removeList.stream().map(RbacResourceEntity::getResourceCode).collect(Collectors.toList());
                Trans.exec(() -> {
                    rbacResourceDao.clear(Cnd.where(RbacResourceEntity.FLD_RESOURCE_CODE, "in", deleteKeyList));
                    rbacRoleResourceDao.clear(Cnd.where(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "in", deleteKeyList));
                });
                for (RbacResourceEntity rbacResourceEntity : removeList) {
                    rbacResourceEntity.setResourceCode(null);
                    result.add(new RbacResourceOperation(rbacResourceEntity, RbacResourceOperation.OPERATION_REMOVE));
                }
            }
            // 更新
            if (!updateList.isEmpty()) {
                for (RbacResourceEntity rbacResourceEntity : updateList) {
                    rbacResourceDao.update(rbacResourceEntity);
                    rbacResourceEntity.setResourceCode(null);
                    result.add(new RbacResourceOperation(rbacResourceEntity, RbacResourceOperation.OPERATION_UPDATE));
                }
            }
        }
        return result;
    }

    public Integer assignResourceKeyToRole(List<String> resourceKeys, String roleKey) {
        if (resourceKeys == null || resourceKeys.isEmpty() || StringUtils.isEmpty(roleKey)) {
            return 0;
        }
        // 检查资源是否存在
        List<RbacRoleResourceEntity> resourceList = rbacRoleResourceDao.query(Cnd.where(RbacRoleResourceEntity.FLD_RESOURCE_CODE, "in", resourceKeys));
        // 去重
        if(resourceList == null){
            resourceList = new ArrayList<>();
        }
        for (RbacRoleResourceEntity resourceEntity : resourceList) {
            String resourceCode = resourceEntity.getResourceCode();
            resourceKeys.remove(resourceCode);
        }
        resourceList = new ArrayList<>();
        for (String resourceKey : resourceKeys) {
            RbacRoleResourceEntity resourceEntity = new RbacRoleResourceEntity();
            resourceEntity.setResourceCode(resourceKey);
            resourceEntity.setRoleCode(roleKey);
            resourceList.add(resourceEntity);
        }
        rbacRoleResourceDao.getDao().insert(resourceList);
        return resourceList.size();
    }

    public Integer assignResourceToRole(List<RbacResourceEntity> resources, String roleKey) {
        if (resources == null || resources.isEmpty() || StringUtils.isEmpty(roleKey)) {
            return 0;
        }
        List<String> resourceKeys = resources.stream().map(RbacResourceEntity::getResourceCode).collect(Collectors.toList());
        return assignResourceKeyToRole(resourceKeys, roleKey);
    }


}
