package cn.mapway.spring.processor.module;

public class FieldDefine {
    public String name;
    public String qTypeName;
    public FieldDefine(String qtype,String name)
    {
        this.name = name;
        this.qTypeName = qtype;
    }
}
