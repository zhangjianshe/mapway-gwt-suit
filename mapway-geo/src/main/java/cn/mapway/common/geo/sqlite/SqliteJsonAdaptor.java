package cn.mapway.common.geo.sqlite;

import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SqliteJsonAdaptor implements ValueAdaptor {
    Class<?> typeClass;
    boolean isList = false;

    public SqliteJsonAdaptor(Type type) {
        if (type instanceof ParameterizedType) {
            //只处理 List类型的模板类型
            String typeName = ((ParameterizedType) type).getActualTypeArguments()[0].getTypeName();
            try {
                typeClass = Class.forName(typeName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            isList = true;
        } else {
            typeClass = (Class<?>) type;
        }
    }

    @Override
    public Object get(ResultSet rs, String colName) throws SQLException {
        Object object = rs.getObject(colName);
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            String pObejct = (String) object;
            if (isList) {
                return Json.fromJsonAsList(typeClass, pObejct);
            } else {
                return Json.fromJson(typeClass, pObejct);
            }
        }
        return null;
    }

    @Override
    public void set(PreparedStatement stat, Object obj, int index) throws SQLException {
        if (null == obj) {
            stat.setNull(index, Types.NULL);
        } else {
            stat.setObject(index, Json.toJson(obj, JsonFormat.tidy()), Types.OTHER);
        }
    }
}
