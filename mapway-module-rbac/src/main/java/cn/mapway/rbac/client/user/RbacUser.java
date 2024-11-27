package cn.mapway.rbac.client.user;

import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
import cn.mapway.ui.client.IUserInfo;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

public class RbacUser implements IUserInfo, IsSerializable {
    RbacUserEntity entity;
    public RbacUser(RbacUserEntity entity){
        this.entity=entity;
    }

    public RbacUser() {
    }

    @Override
    public String getSystemCode() {
        return "rbac";
    }

    @Override
    public String getUserName() {
        return entity.getUserName();
    }

    @Override
    public String getNickName() {
        return entity.getNickName();
    }

    @Override
    public String getAvatar() {
        return entity.getAvatar();
    }

    @Override
    public String getRemark() {
        return entity.getRemark();
    }

    @Override
    public Date getUpdateTime() {
        return entity.getUpdateTime();
    }

    @Override
    public Date getCreateTime() {
        return entity.getCreateTime();
    }

    @Override
    public String getConfig() {
        return entity.getConfig();
    }

    @Override
    public String getRelId() {
        return entity.getRelId();
    }

    @Override
    public String getSex() {
        return entity.getSex();
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public String getUserType() {
        return entity.getUserType();
    }

    @Override
    public String getPhone() {
        return entity.getPhonenumber();
    }

    @Override
    public String getId() {
        return entity.getUserId().toString();
    }

    @Override
    public long getExpireTime() {
        return expires;
    }

    long expires;
    public void setExpireTime(long l) {
        expires = l;
    }
}
