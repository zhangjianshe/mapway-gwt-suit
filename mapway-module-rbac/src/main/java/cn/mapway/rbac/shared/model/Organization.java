package cn.mapway.rbac.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import jsinterop.annotations.JsType;

import java.io.Serializable;
import java.util.ArrayList;

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
    public Number rank;
    public Boolean major;//是否为主要组织机构
    public ArrayList<Organization> children;
    public String location;//地理位置 (lng,lat,zoom)

    public final Number getRank()
    {
        return rank;
    }
    /**
     * 复制　仅仅复制这一层　不复制子节点
     * @return
     */
    public final Organization clone(){
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
        organization.location=location;
        organization.children=new ArrayList<>();
        return organization;
    }
}
