package cn.mapway.ui.client;

import jsinterop.annotations.JsType;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息的标准接口
 */
@JsType
public interface IUserInfo extends Serializable {
    String getSystemCode();
    String getUserName();
    String getNickName();
    String getAvatar();
    String getRemark();
    Date getUpdateTime();
    Date getCreateTime();
    String getConfig();
    String getRelId();
    String getSex();
    String getEmail();
    String getUserType();
    String getPhone();
    String getId();

    /**
     * session expire time
     * @return
     */
    long getExpireTime();
}
