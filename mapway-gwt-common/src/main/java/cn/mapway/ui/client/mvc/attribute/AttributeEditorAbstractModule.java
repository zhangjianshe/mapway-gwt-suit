package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.tools.IData;

/**
 * 属性编辑器基类
 */
public abstract class AttributeEditorAbstractModule extends BaseAbstractModule implements IData {
    Object data;

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        this.data = obj;
    }
}
