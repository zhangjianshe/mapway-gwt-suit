package cn.mapway.spring.processor;

import com.squareup.javapoet.ClassName;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

public class ModuleTypeHolders {
    List<ModuleTypeHolder> modules;

    public ModuleTypeHolders(){
        modules = new ArrayList<ModuleTypeHolder>();
    }
    public void put(ClassName className)
    {
        String name=className.canonicalName();
        if(name.startsWith("java.lang.String"))
        {
            return;
        }
        boolean found=find(name);
        if(!found)
        {
            //需要获取这个类的 所有字段 以及该类的 变量信息
            Class<?> forName=null;
            try {
                forName = Class.forName(className.canonicalName());
            } catch (ClassNotFoundException e) {
                System.out.println("找不到类 "+className.canonicalName()+"的定义");
            }

            ModuleTypeHolder holder=new ModuleTypeHolder();
            holder.setTypeName(className);
            holder.setName(name);
            holder.setForName(forName);
            modules.add(holder);
        }
    }

    private boolean find(String name)
    {
        for(ModuleTypeHolder holder : modules)
        {
            if(holder.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
            StringBuilder sb = new StringBuilder();
            for(ModuleTypeHolder holder : modules)
            {
                sb.append(holder.toString()).append('\n');
            }
            return sb.toString();
    }

    /**
     * 解析一个 typeMirror
     *
     * https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html
     *                              (type     args   )
     *   ParamerizedType    Result<Time1,Time2<Integer>>
     *                              (type parameter)
     *   GenericType        Result<      T,K             >
     *       Type Time1
     *       Type Time2<T>
     *
     * @param mirror
     */
    public static void parseTypeMirror(TypeMirror mirror,List<TypeMirror> components)
    {

    }
}
