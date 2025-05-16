package cn.mapway.ui.client.widget;

public interface ICheckRole {
    void setRole(String rule);
    void setAllRole(String rule);
    boolean isAssign(int type);
    Integer ALL_TYPE = -1000;

}
