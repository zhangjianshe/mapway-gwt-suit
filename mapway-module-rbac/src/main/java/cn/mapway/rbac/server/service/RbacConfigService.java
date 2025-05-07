package cn.mapway.rbac.server.service;

import cn.mapway.rbac.server.dao.RbacConfigDao;
import cn.mapway.rbac.shared.db.postgis.RbacConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RBAC 配置服务
 */
@Service
@Slf4j
public class RbacConfigService {
    @Resource
    RbacConfigDao rbacConfigDao;

    /**
     * 判断是否key is value
     *
     * @param key
     * @param value
     * @return
     */
    public boolean needUpdate(String key, String value) {
        if (Strings.isBlank(key)) {
            return false;
        }
        if (Strings.isBlank(value)) {
            return false;
        }
        RbacConfigEntity configEntity = rbacConfigDao.fetch(key);
        if (configEntity == null || Strings.isBlank(configEntity.getValue())) {
            return true;
        }
        return !configEntity.getValue().equals(value);
    }

    /**
     * 保存配置信息
     *
     * @param key
     * @param value
     */
    public void saveConfig(String key, String value) {
        if (Strings.isBlank(key)) {
            log.warn("RBAC保存配置信息 Key is null");
            return;
        }
        if (Strings.isBlank(value)) {
            log.warn("RBAC保存配置信息 value is null");
            return;
        }
        RbacConfigEntity temp = new RbacConfigEntity();
        temp.setKey(key);
        temp.setValue(value);
        try {
            rbacConfigDao.insertOrUpdate(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
