package cn.mapway.ui.client.mvc.decorator.impl;

import cn.mapway.ui.client.mvc.attribute.*;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AbstractDecoratorPanel
 *
 * @author zhang
 */
public class AbstractDecoratorPanel extends SimplePanel implements IAttributeProvider, IAttributeParser {
    String mId;
    Set<IAttributeReadyCallback> callbacks;
    public AbstractDecoratorPanel() {
        callbacks = new HashSet<>();
    }

    @Override
    public String getAttributeTitle() {
        return "";
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public boolean removeLink(String linkId) {
        return false;
    }

    @Override
    public List<IAttribute> getAttributes() {
        return new ArrayList<>();
    }

    @Override
    public String getAttributeSummary() {
        return "";
    }

    @Override
    public void commit() {
        //都nothing
    }

    @Override
    public List<AttributeValue> flatten() {
        return new ArrayList<>();
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


    @Override
    public List<String> isValidate() {
        return new ArrayList<>();
    }

    @Override
    public void parseAttribute(List<AttributeValue> values) {

    }

    @Override
    public void notifyAttributeReady() {
        for (IAttributeReadyCallback callback : callbacks) {
            callback.onAttributeReady(this);
        }
    }

}
