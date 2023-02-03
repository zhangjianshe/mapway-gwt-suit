package cn.mapway.spring.processor.module;

import com.squareup.javapoet.TypeName;

public class FieldDefine {
    public String name;
    public String qTypeName;
    public TypeName tType;
    public boolean isStatic;
    public String initValue;
    public String summary;

    public FieldDefine()
    {

    }
}
