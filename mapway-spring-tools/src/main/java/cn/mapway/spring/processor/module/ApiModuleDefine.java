package cn.mapway.spring.processor.module;


import cn.mapway.util.console.Ansi;
import cn.mapway.util.console.AnsiColor;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

public class ApiModuleDefine {
    public String qname;
    public List<ModulePara> parameters;
    public List<FieldDefine> fields;
    public TypeName translateName=null;
    //转换后的基类
    public TypeName translateSuper=null;

    //转换后 实现的接口
    public List<TypeName> translateImpls=new ArrayList<TypeName>();
    public String packageName;
    public boolean isInterface=false;


    public ApiModuleDefine(String qname) {
        this.qname = qname;
        parameters = new ArrayList<>();
        fields = new ArrayList<>();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        Ansi boldFormat = Ansi.from(AnsiColor.FG_BLUE).set(AnsiColor.STYLE_HIGH_INTENSITY);
        Ansi yellow = Ansi.from(AnsiColor.FG_YELLOW);
        Ansi green = Ansi.from(AnsiColor.FG_GREEN);
        Ansi lightBlue = Ansi.from(AnsiColor.FG_CYAN);
        Ansi red = Ansi.from(AnsiColor.FG_RED);
        sb.append(boldFormat.val(qname));
        if (parameters.size() > 0) {
            sb.append(yellow.val(" <"));
            int i = 0;
            for (ModulePara p : parameters) {
                if (i++ > 0) {
                    sb.append(" , ");
                }
                sb.append(lightBlue.val(p.toString()));
            }
            sb.append(yellow.val(" > "));
        }
        sb.append("\r\n");

        if(translateName!=null) {
            sb.append(red.val("["+getTranslateName(translateName)+"]"));
            sb.append("\r\n");
        }

        for (FieldDefine field : fields) {
            sb.append(green.val(field.name) + "\t\t" + lightBlue.val(field.tType.toString()));
            sb.append("\r\n");
        }
        return sb.toString();
    }


    private String getTranslateName(TypeName typeName) {

        if(typeName instanceof ParameterizedTypeName)
        {
            StringBuilder sb = new StringBuilder();
            ParameterizedTypeName p = (ParameterizedTypeName) typeName;
            sb.append(p.rawType.toString());
            sb.append("< ");
            int index=0;
            for( TypeName arg: p.typeArguments)
            {
                if(index++>0)
                {
                    sb.append(", ");
                }
                if(arg instanceof TypeVariableName)
                {
                    TypeVariableName typeVariableName= (TypeVariableName) arg;
                    sb.append( typeVariableName.name ).append(" ");
                    int index2=0;
                    for (TypeName bound : typeVariableName.bounds) {
                        if(index2++>0)
                        {
                            sb.append(" & ");
                        }
                        sb.append(getTranslateName(bound));
                    }
                }
                else{
                    sb.append(arg.toString());
                }
            }
            sb.append(">");
            return sb.toString();
        }
        else{
            return typeName.toString();
        }
    }
}
