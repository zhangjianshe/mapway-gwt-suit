package cn.mapway.common.geo.postgis;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Regex;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 數據庫工具
 */
public class DbTools {
    private static String extractSerialName(String defaultValue) {
        if (Strings.isBlank(defaultValue)) {
            return "";
        }
        String regex = "nextval\\('([^']*)'::regclass\\)";
        Pattern pattern = Regex.getPattern(regex);
        Matcher matcher = pattern.matcher(defaultValue);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group;
        }
        return "";
    }

    /**
     * 檢查序列
     *
     * @param clazz
     */
    public static void checkFieldSerial(Dao dao, Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Sql> sqlList = new ArrayList<>();
        for (Field field : fields) {
            Default fieldAnnotation = field.getAnnotation(Default.class);
            if (fieldAnnotation != null) {
                String defaultValue = fieldAnnotation.value();
                String serialName = extractSerialName(defaultValue);
                if (Strings.isNotBlank(serialName)) {
                    //model_user_relation_id_seq
                    String sql = "CREATE SEQUENCE IF NOT EXISTS public." + serialName + ";";
                    sqlList.add(Sqls.create(sql));
                }
            }
        }
        if (sqlList.size() > 0) {
            for (Sql sql : sqlList) {
                dao.execute(sql);
            }
        }
    }
}
