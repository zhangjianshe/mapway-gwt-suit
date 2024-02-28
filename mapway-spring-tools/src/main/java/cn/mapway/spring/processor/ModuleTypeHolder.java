package cn.mapway.spring.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.nutz.lang.Mirror;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Data
public class ModuleTypeHolder {
    String name;
    ClassName typeName;
    Class<?>  forName;
    TypeSpec.Builder builder;
    String targeName;
    TypeName targeTypeName;

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(String.format("===> %s <=======\r\n", name,typeName.simpleName()));
        if(forName!=null)
        {
            Mirror<?> mirror = Mirror.me(forName);
            Field[] fields = mirror.getFields(true, false);
            Type[] genericsTypes = mirror.getGenericsTypes();
            if(genericsTypes!=null)
            {
                for(Type type : genericsTypes)
                {
                    sb.append(String.format(",%s",type.getTypeName()));
                }
            }
            sb.append("\r\n");
            if(fields!=null)
            {
                for(Field field : fields)
                {
                    sb.append(String.format("\t%s  %s  \r\n",field.getType().getSimpleName(),field.getName()));
                }
            }

        }
        return sb.toString();
    }
}
