package cn.mapway.rbac.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RbacRole implements Serializable, IsSerializable {
    public String name;
    public String code;
    public String summary;
    public String icon;
    public String parentCode;
    public List<RbacRole> children;

    public void addChild(RbacRole role) {
        if(children==null){
            children=new ArrayList<>();
        }
        role.parentCode=this.code;
        children.add(role);
    }
}
