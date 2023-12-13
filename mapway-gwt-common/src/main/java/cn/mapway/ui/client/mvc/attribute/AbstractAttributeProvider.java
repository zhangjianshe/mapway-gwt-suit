package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEvent;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEventHandler;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AbstractAttributeProvider
 *
 * @author zhang
 */
public abstract class AbstractAttributeProvider implements IAttributeProvider, HasCommonHandlers {
    SimpleEventBus eventBus;
    List<IAttribute> attributes;
    Set<IAttributeReadyCallback> callbacks;

    public AbstractAttributeProvider() {
        eventBus = new SimpleEventBus();
        attributes = new ArrayList<IAttribute>();
        callbacks = new HashSet<>();
    }

    @Override
    public String getAttributeTitle() {
        return "";
    }

    @Override
    public void addAttributeReadyCallback(IAttributeReadyCallback callback) {
        if (callback == null) {
            return;
        }
        if (!callbacks.contains(callback)) {
            this.callbacks.add(callback);
        }
    }

    @Override
    public void removeAttributeReadyCallback(IAttributeReadyCallback callback) {
        if (callback == null) {
            return;
        }
        callbacks.remove(callback);
    }

    /**
     * 通知属性准备好
     */
    @Override
    public void notifyAttributeReady() {
        for (IAttributeReadyCallback callback : callbacks) {
            callback.onAttributeReady(this);
        }
    }

    @Override
    public List<IAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public List<AttributeValue> flatten() {
        List<AttributeValue> values = new ArrayList<AttributeValue>();
        for (IAttribute attribute : attributes) {
            String value = "";
            Object obj = attribute.getValue();
            if (obj instanceof String) {
                value = (String) obj;
            } else if (obj instanceof Boolean) {
                Boolean bValue = (Boolean) obj;
                value = bValue ? "True" : "False";
            } else if (obj instanceof Double
                    || obj instanceof Integer
                    || obj instanceof Float
            ) {
                value = String.valueOf(obj);
            } else if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
            values.add(new AttributeValue(attribute.getName(), attribute.getAltName(), value, InputTypeEnum.INPUT_OTHERS.code));
        }
        return values;
    }

    @Override
    public void commit() {

    }

    @Override
    public List<String> isValidate() {
        ArrayList<String> errors = new ArrayList<>();
        if (attributes == null) {
            return errors;
        }
        for (IAttribute attribute : attributes) {
            String regx = attribute.getValidateRegx();
            if (regx != null && regx.length() > 0) {
                if (attribute.getValue() != null) {

                }
            }
        }
        return errors;
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        return eventBus.addHandler(CommonEvent.TYPE, handler);
    }

    @Override
    public String getAttributeSummary() {
        return "";
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        eventBus.fireEventFromSource(event, this);
    }

    //数据类型转换

    public Integer castToInteger(Object obj) {
        return DataCastor.castToInteger(obj);
    }

    public String castToString(Object obj) {
        return DataCastor.castToString(obj);
    }

    public Double castToDouble(Object obj) {
        return DataCastor.castToDouble(obj);
    }

    public Float castToFloat(Object obj) {
        return DataCastor.castToFloat(obj);
    }

    public Boolean castToBoolean(Object obj) {
        return DataCastor.castToBoolean(obj);
    }

    public Long castToLong(Object obj) {
        return DataCastor.castToLong(obj);
    }

    @Override
    public void parseAttribute(List<AttributeValue> values) {
        // not implementens
    }

    @Override
    public HandlerRegistration addAttributeStateChangeHandler(AttributeStateChangeEventHandler handler) {
        return eventBus.addHandler(AttributeStateChangeEvent.TYPE, handler);
    }
}
