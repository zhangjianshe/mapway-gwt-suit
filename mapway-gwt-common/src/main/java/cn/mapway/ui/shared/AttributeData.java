package cn.mapway.ui.shared;

/**
 * AttributeEvent
 *
 * @author zhang
 */
public class AttributeData {
    public final static int ATTRIBUTE_READY = 0;
    public final static int ATTRIBUTE_ADD = 1;
    public final static int ATTRIBUTE_REMOVE = 2;
    public int type;
    //对象ID
    public String objectId;
    public Object data;

    /**
     * 创建属性ready事件数据
     *
     * @param data
     * @return
     */
    public static AttributeData createAttributeReadyEventData(String objectId, Object data) {
        AttributeData data1 = new AttributeData();
        data1.type = ATTRIBUTE_READY;
        data1.data = data;
        data1.objectId = objectId;
        return data1;
    }

    public boolean isReady() {
        return type == ATTRIBUTE_READY;
    }

    public boolean isAdd() {
        return type == ATTRIBUTE_ADD;
    }

    public boolean isRemove() {
        return type == ATTRIBUTE_REMOVE;
    }
}
