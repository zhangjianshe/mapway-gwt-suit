package cn.mapway.spring.processor.module;

import java.util.HashMap;
import java.util.Map;

/**
 * 包含系统接口中使用到的所有 模型类
 */
public class AllModules {
    private static final AllModules INSTANCE=new AllModules();
    public static AllModules getInstance() {
        return INSTANCE;
    }
    private Map<String, ApiModuleDefine> modules=new HashMap<String, ApiModuleDefine>();

    public ApiModuleDefine getModule(String qname){
        return modules.get(qname);
    }

    public void put(ApiModuleDefine define)
    {
        modules.put(define.qname, define);
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();

        for(ApiModuleDefine define : modules.values()){
            sb.append("\r\n");
            sb.append(define.toString());
        }
        return sb.toString();
    }
}
