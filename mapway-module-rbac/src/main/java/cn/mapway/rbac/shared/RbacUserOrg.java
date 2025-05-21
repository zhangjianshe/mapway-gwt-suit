package cn.mapway.rbac.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;


public class RbacUserOrg implements Serializable, IsSerializable {
    public String systemCode;
    public String orgId;
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
    public Boolean major;
    public Integer orgRank;
    public Integer getOrgRank()
    {
        return orgRank;
    }
}
