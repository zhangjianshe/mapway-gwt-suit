package cn.mapway.ui.shared.db;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class TableIndex implements Serializable, IsSerializable {
    public String name;
    public Boolean isUnique;
    public Boolean isPrimary;
    public String indexType;
    public String columns;
    public String definition;
}
