package cn.mapway.ui.client.mvc.attribute;

import com.google.gwt.user.client.rpc.IsSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AttributeValue
 * 描述一个属性的值
 *
 * @author zhang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeValue implements Serializable, IsSerializable {
    String name;
    String altName;
    String value;
    Integer inputType;
}
