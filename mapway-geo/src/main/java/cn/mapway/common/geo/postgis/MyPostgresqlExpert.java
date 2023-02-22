package cn.mapway.common.geo.postgis;

import cn.mapway.geo.geometry.GeoObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.jdbc.psql.PsqlJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.jdbc.ValueAdaptor;
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
    public ValueAdaptor getAdaptor(MappingField ef) {
        //重载系统的转换器
        if (ColType.PSQL_JSON == ef.getColumnType()) {
            return new PGObject2JsonObjectAdaptor( ef.getType());
        }
        if (ef.getTypeMirror().isOf(GeoObject.class)) {
            return new GeoObjectAdaptor();
        }
        if (ef.getTypeMirror().isOf(PGpolygon.class)) {
            return new PGpolygonAdaptor();
        }
        if (ef.getTypeMirror().isOf(PGline.class)) {
            return new PGLineAdaptor();
        }
        if (ef.getTypeMirror().isOf(PGpoint.class)) {
            return new PGPointAdaptor();
        }
        if (ef.getTypeMirror().isOf(PGpath.class)) {
            return new PGPathAdaptor();
        }
        if (ef.getTypeMirror().isOf(PGcircle.class)) {
            return new PGCircleAdaptor();
        }
        if (ef.getTypeMirror().isOf(PGbox.class)) {
            return new PGBoxAdaptor();
        } else {
            return super.getAdaptor(ef);
        }
    }
}
