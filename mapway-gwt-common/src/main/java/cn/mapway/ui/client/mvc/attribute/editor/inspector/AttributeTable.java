package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributePropertyChangeCallback;
import cn.mapway.ui.client.mvc.attribute.editor.proxy.AttributeItemEditorProxy;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class AttributeTable extends CommonEventComposite implements IAttributePropertyChangeCallback {
    private final CommonEventHandler itemEditorProxyHandler = commonEvent -> fireEvent(commonEvent);

    @Override
    public void onAttributePropertyChange(IAttribute senderAttribute) {

    }

    interface AttributeTableUiBinder extends UiBinder<HTMLPanel, AttributeTable> {
    }

    private static AttributeTableUiBinder ourUiBinder = GWT.create(AttributeTableUiBinder.class);
    @UiField
    HTMLPanel table;

    public void setColumnCount(int columnCount) {
        if(columnCount>0){
            Style style = table.getElement().getStyle();
            String columnTemplate="";
            for(int i=0;i<columnCount;i++){
                columnTemplate+="1fr ";
            }
            style.setProperty("gridTemplateColumns",columnTemplate);
        }
    }
    int labelWidth=150;
    public void setLabelWidth(int width) {
        this.labelWidth = width;
        for (int i = 0; i < table.getWidgetCount(); i++) {
            Widget w = table.getWidget(i);
            if (w instanceof AttributeItemEditorProxy) {
                ((AttributeItemEditorProxy) w).setLabelWidth(width);
            }
        }
    }

    String groupName="分组名称";
    public void setGroupName(String group) {
        this.groupName = group;
    }
    public String getGroupName() {
        return groupName;
    }

    /**
     * 属性族中添加一个属性
     *
     * @param attribute
     */
    public void appendAttribute(IAttribute attribute) {
        AttributeItemEditorProxy itemEditorProxy = new AttributeItemEditorProxy();
        itemEditorProxy.addCommonHandler(itemEditorProxyHandler);
        itemEditorProxy.createEditorInstance(attribute);
        itemEditorProxy.setLabelWidth(labelWidth);
        //属性特性变更后毁掉到此类
        attribute.setChangeCallback(this);
        table.add(itemEditorProxy);
        itemEditorProxy.setVisible(attribute.getAttrVisible());
    }
    public AttributeTable() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void updateUI() {
        int visibleCount = 0;
        for (int index = 0; index < table.getWidgetCount(); index++) {
            Widget w = table.getWidget(index);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy editor = (AttributeItemEditorProxy) w;
                editor.updateUI();
                if (editor.isVisible()) {
                    visibleCount++;
                }
            }
        }
    }

    /**
     * 更新属性值
     *
     * @param values
     */
    public void updateValue(List<AttributeValue> values) {
        for (int i = 0; i < table.getWidgetCount(); i++) {
            Widget w = table.getWidget(i);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy proxy = (AttributeItemEditorProxy) w;
                String attributeName = proxy.getAttribute().getName();
                for (AttributeValue attributeValue : values) {
                    if (attributeValue.getName().equals(attributeName)) {
                        proxy.getAttribute().setValue(attributeValue.getValue());
                        break;
                    }
                }
            }
        }
    }

    public AttributeItemEditorProxy findEditorProxy(IAttribute attribute) {
        for (int index = 0; index < table.getWidgetCount(); index++) {
            Widget w = table.getWidget(index);
            if (w instanceof AttributeItemEditorProxy) {
                AttributeItemEditorProxy editor = (AttributeItemEditorProxy) w;
                IAttribute attr = editor.getAttribute();
                if (attr.getId().equals(attribute.getId())) {
                    return editor;
                }
            }
        }
        return null;
    }
}