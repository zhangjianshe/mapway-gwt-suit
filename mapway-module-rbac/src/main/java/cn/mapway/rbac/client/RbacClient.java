package cn.mapway.rbac.client;

import cn.mapway.rbac.client.user.RbacUser;
import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.ui.client.IClientContext;
import cn.mapway.ui.client.IUserInfo;

import java.util.HashMap;
import java.util.Map;

public class RbacClient {
    private static RbacClient instance;
    private  IClientContext clientContext;
    public IClientContext getClientContext(){
        assert clientContext!=null;
        return clientContext;
    }
    public void setClientContext(IClientContext clientContext){
        this.clientContext=clientContext;
    }
    public static RbacClient get() {
        if(instance==null)
        {
            instance=new RbacClient();
        }
        return instance;
    }
    Map<String,Object> context;
    RbacClient() {
        context=new HashMap<>();
    }


    public void setAuthCode(String authCode) {
        context.put(RbacConstant.KEY_AUTH_CODE,authCode);
    }
    public String getAuthCode() {
        return (String)context.get(RbacConstant.KEY_AUTH_CODE);
    }

    public void setUser(IUserInfo currentUser) {
        clientContext.setUserInfo(currentUser);
    }
}
