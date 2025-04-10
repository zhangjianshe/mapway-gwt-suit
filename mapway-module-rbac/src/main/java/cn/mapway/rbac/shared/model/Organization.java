package cn.mapway.rbac.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;

/**
 * 组织信息
 */
@JsType
public class Organization implements Serializable, IsSerializable {
    public String orgId;
    public String orgCode;
    public String parentId;
    public String orgName;
    public String orgIcon;
    public String summary;
    public String link;
    public String charger;
    public String email;
    public String tel;
    public String regionCode;
    public String userId;
    public String userCode;
    public String userName;
    public String userIcon;
    public Integer rank;
    public Boolean major;//是否为主要组织机构
    public Organization[] children;

    public Integer getRank()
    {
        return rank;
    }
    /**
     * 复制　仅仅复制这一层　不复制子节点
     * @return
     */
    public Organization clone(){
        Organization organization=new Organization();
        organization.orgId=orgId;
        organization.orgCode=orgCode;
        organization.parentId=parentId;
        organization.orgName=orgName;
        organization.orgIcon=orgIcon;
        organization.summary=summary;
        organization.link=link;
        organization.charger=charger;
        organization.email=email;
        organization.tel=tel;
        organization.regionCode=regionCode;
        organization.userId=userId;
        organization.userCode=userCode;
        organization.userName=userName;
        organization.userIcon=userIcon;
        organization.rank=rank;
        organization.major=major;
        organization.children=new Organization[0];
        return organization;
    }
}
