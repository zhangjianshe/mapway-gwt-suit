package cn.mapway.ui.client.mvc.attribute.event;

import cn.mapway.ui.client.mvc.attribute.IAttribute;
import com.google.gwt.event.shared.GwtEvent;

public class AttributeStateChangeEvent extends GwtEvent<AttributeStateChangeEventHandler> {
    public static Type<AttributeStateChangeEventHandler> TYPE = new Type<AttributeStateChangeEventHandler>();
    /**
     * 属性变更的信息
     */
    public IAttribute attribute;
    public AttributeState state;

    public Type<AttributeStateChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AttributeStateChangeEventHandler handler) {
        handler.onAttributeStateChange(this);
    }
}
