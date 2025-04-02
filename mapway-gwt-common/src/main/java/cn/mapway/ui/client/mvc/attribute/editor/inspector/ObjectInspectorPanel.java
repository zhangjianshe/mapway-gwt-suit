package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributeReadyCallback;
import cn.mapway.ui.client.mvc.attribute.IAttributesProvider;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.client.widget.panel.MessagePanel;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.WindowEvent;
import cn.mapway.ui.shared.WindowTitleData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 目标属性检查器对话框　和　ObjectInspector想对应　只不过　这里提供了对话框式的UI
 */
public class ObjectInspectorPanel extends CommonEventComposite implements IData<IAttributesProvider>, IAttributeReadyCallback {
    private static Dialog<ObjectInspectorPanel> dialog;
    private static final ObjectInspectorPanelUiBinder ourUiBinder = GWT.create(ObjectInspectorPanelUiBinder.class);
    private IAttributesProvider attributeProvider;
    @UiField
    SaveBar saveBar;
    @UiField
    HTMLPanel tabs;
    @UiField
    DockLayoutPanel root;

    public ObjectInspectorPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public static Dialog<ObjectInspectorPanel> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }



    private static Dialog<ObjectInspectorPanel> createOne() {
        ObjectInspectorPanel panel = new ObjectInspectorPanel();
        return new Dialog(panel, "目标属性检查器");
    }

    @Override
    public IAttributesProvider getData() {
        return attributeProvider;
    }

    @Override
    public void setData(IAttributesProvider obj) {
        if(obj==null)
        {
            closeCurrentProvider();
        }
        attributeProvider=obj;
        if (attributeProvider != null) {
            attributeProvider.addAttributeReadyCallback(this);
        }
    }

    private void closeCurrentProvider() {
        if (attributeProvider != null) {
            attributeProvider.removeAttributeReadyCallback(this);
            attributeProvider = null;
           clearGroups();
        }
    }

    private void clearGroups() {
        for(AttributeTable table:currentTables.values())
        {
            freeTable(table);
        }
        currentTables.clear();
        if(currentTable!=null)
        {
            root.remove(currentTable);
            currentTable=null;
        }
        tabs.clear();
    }

    Widget currentTable=null;
    List<AttributeTable> pool=new ArrayList<>();
    public AttributeTable poolTable()
    {
        if(pool.size()>0)
        {
            return pool.remove(0);
        }
        return new AttributeTable();
    }
    public void freeTable(AttributeTable table)
    {
        pool.add(table);
    }
    /**
     * 没一行的列数
     */
    int columnCount=2;
    Map<String,AttributeTable> currentTables;
    public void setColumnCount(int columnCount)
    {
        this.columnCount=columnCount;
        for(AttributeTable table:currentTables.values())
        {
            table.setColumnCount(columnCount);
        }
    }
    List<AttributeValue> values;
    @Override
    public void onAttributeReady(IAttributesProvider attributeProvider) {
        toUI();
        updateValue(this.values);
    }
    /**
     * 更新属性界面的值
     * 根据altName字段进行匹配
     *
     * @param values
     */
    public void updateValue(List<AttributeValue> values) {
        this.values = values;
        if (this.values != null && currentTables != null) {
            for (AttributeTable attributeTable : currentTables.values()) {
                attributeTable.updateValue(values);
            }
        }
    }
    private void toUI() {
        //清空界面
        clearGroups();
        saveBar.setEnableSave(false);

        if (attributeProvider == null) {
            fireEvent(CommonEvent.titleEvent(attributeProvider.getAttributeTitle()));
            return;
        }

        //分组并排序
        Map<String, List<IAttribute>> sortedHash = new HashMap<>();
        for (IAttribute attribute : attributeProvider.getAttributes()) {
            String groupName = attribute.getGroup();
            if (groupName == null) {
                groupName = "";
            }
            List<IAttribute> list = sortedHash.computeIfAbsent(groupName, k -> new ArrayList<>());
            list.add(attribute);
        }
        for (List<IAttribute> list : sortedHash.values()) {
            list.sort(Comparator.comparingInt(IAttribute::getRank));
        }

        // group 怎么排序?　假定　group 的名字前面添加　数字序号　比如　１. 2. 通过提取前面的数字序号　进行排序　如果
        // 没有序号，则按照字典序排序
        List<String> stringList = sortedHash.keySet().stream().sorted().collect(Collectors.toList());
        for (String key : stringList) {
            String groupName = key.replaceFirst("\\d+\\.", "");
            AttributeTable table = currentTables.get(groupName);
            if(table==null)
            {
                table=poolTable();
                table.setColumnCount(columnCount);
                table.setGroupName(groupName);
                currentTables.put(groupName,table);

            }
            for (IAttribute attribute : sortedHash.get(key)) {
                table.appendAttribute(attribute);
            }
        }

        for (AttributeTable attributeTable : currentTables.values()) {
            attributeTable.updateUI();
        }

        if (attributeProvider.getAttributes().size() == 0) {
            currentTable= getMessagePanel();
            root.add(currentTable);
        } else {
            saveBar.setEnableSave(true);
            if(currentTable!=null)
            {
                root.remove(currentTable);
            }
            currentTable=currentTables.values().iterator().next();
            root.add(currentTable);
        }
    }

    MessagePanel getMessagePanel()
    {
        MessagePanel messagePanel=new MessagePanel();
        messagePanel.setText("没有可用的属性");
        return messagePanel;
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if(event.isOk())
        {
            //save button clicked

        }
        else if(event.isClose())
        {
            closeCurrentProvider();
            fireEvent(event);
        }
    }

    interface ObjectInspectorPanelUiBinder extends UiBinder<DockLayoutPanel, ObjectInspectorPanel> {
    }
}