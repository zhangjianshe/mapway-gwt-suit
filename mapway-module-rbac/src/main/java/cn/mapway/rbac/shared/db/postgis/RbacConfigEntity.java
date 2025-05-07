package cn.mapway.rbac.shared.db.postgis;

import cn.mapway.document.annotation.ApiField;
import cn.mapway.document.annotation.Doc;
import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.Getter;
import lombok.Setter;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * <b>rbac config table</b>
 *
 * @author zhangjianshe@gmail.com
 * select key,value from rbac_table ;
 */
@Table(value = "rbac_config")
@Doc("rbac_config")
@Getter
@Setter
public class RbacConfigEntity implements Serializable, IsSerializable {

    public static final String TBL_RBAC_CONFIG = "rbac_config";
    public static final String TBL_FIELD_KEY = "key";
    public static final String TBL_FIELD_VALUE = "value";

    /**
     * 配置键
     * 缺省值:null
     * 数据类型varchar
     * 数据库字段长度:64(不允许为空)
     */
    @ApiField("配置键")
    @Name
    @ColDefine(
            type = ColType.VARCHAR,
            width = 64
    )
    @Comment("配置键")
    private String key;

    /**
     * 配置键值
     * 缺省值:null
     * 数据类型varchar
     * 数据库字段长度:2046(不允许为空)
     */
    @ApiField("配置键值")
    @Column("value")
    @ColDefine(
            type = ColType.VARCHAR,
            width = 2046
    )
    @Comment("配置键值")
    private String value;

}
