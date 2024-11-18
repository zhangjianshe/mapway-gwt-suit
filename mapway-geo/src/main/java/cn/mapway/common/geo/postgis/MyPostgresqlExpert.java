package cn.mapway.common.geo.postgis;

import cn.mapway.geo.geometry.GeoObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Strings;
import org.postgresql.geometric.*;

/**
 * MyPostgresqlExpert
 * NUtz DB  字段类型转换
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */

@Slf4j
public class MyPostgresqlExpert extends PsqlJdbcExpert {
    public MyPostgresqlExpert(JdbcExpertConfigFile conf) {
        super(conf);
        log.info("postGIS 转换初始化");
    }

    @Override
    public void addDefaultValue(StringBuilder sb, MappingField mf) {
        String defaultValue = getDefaultValue(mf);
        if (Strings.isNotBlank(defaultValue)) {
            if (defaultValue.contains("::character")) {
                sb.append(" DEFAULT ").append(defaultValue);
            } else {
                super.addDefaultValue(sb, mf);
            }
        }
    }

    @Override
    public ValueAdaptor getAdaptor(MappingField ef) {
        //重载系统的转换器
        if (ColType.PSQL_JSON == ef.getColumnType()) {
            return new PGObject2JsonObjectAdaptor(ef.getType());
        }
        if (ef.getMirror().isOf(GeoObject.class)) {
            return new GeoObjectAdaptor();
        }
        if (ef.getMirror().isOf(PGpolygon.class)) {
            return new PGpolygonAdaptor();
        }
        if (ef.getMirror().isOf(PGline.class)) {
            return new PGLineAdaptor();
        }
        if (ef.getMirror().isOf(PGpoint.class)) {
            return new PGPointAdaptor();
        }
        if (ef.getMirror().isOf(PGpath.class)) {
            return new PGPathAdaptor();
        }
        if (ef.getMirror().isOf(PGcircle.class)) {
            return new PGCircleAdaptor();
        }
        if (ef.getMirror().isOf(PGbox.class)) {
            return new PGBoxAdaptor();
        } else {
            return super.getAdaptor(ef);
        }
    }

    @Override
    public String evalFieldType(MappingField mf) {
        switch (mf.getColumnType()) {
            case FLOAT:
                // 用户自定义了精度
                if (mf.getWidth() > 0 && mf.getPrecision() > 0) {
                    return "NUMERIC(" + mf.getWidth() + "," + mf.getPrecision() + ")";
                }
                // 用默认精度
                if (mf.getMirror().isDouble())
                    return "NUMERIC(53,10)";
                return "NUMERIC";
            default:
                return super.evalFieldType(mf);
        }
    }
}
