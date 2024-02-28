package cn.mapway.spring.processor.module;

import java.util.ArrayList;
import java.util.List;

/**
 * 泛型模型参数定义
 */
public class ModulePara {
   public String name;
    public List<String> upBound;
    public  List<String> lowerBound;
    public ModulePara(){
        this("");
    }
    public ModulePara(String name){
        this.name = name;
        upBound=new ArrayList();
        lowerBound=new ArrayList();
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if(upBound.size()>0){
            int index=0;
            sb.append(" extends ");
            for(String up : upBound)
            {
                if(index++>0)
                {
                    sb.append(" & ");
                }
                sb.append(up);
            }
        }
        else if(lowerBound.size()>0){
            int index=0;
            sb.append(" super ");
            for(String lo : lowerBound)
            {
                if(index++>0)
                {
                    sb.append(" & ");
                }
                sb.append(lo);
            }
        }
        return sb.toString();
    }
}
