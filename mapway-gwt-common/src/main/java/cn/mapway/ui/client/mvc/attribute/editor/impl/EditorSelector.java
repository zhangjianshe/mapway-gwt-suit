package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorFactory;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorInfo;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编辑器选择
 */
public class EditorSelector extends CommonEventComposite {
    private static final EditorSelectorUiBinder ourUiBinder = GWT.create(EditorSelectorUiBinder.class);
    private static Popup<EditorSelector> popup;
    @UiField
    ZTree catalog;
    @UiField
    FlexTable table;
    @UiField
    SaveBar saveBar;
    AttributeEditorInfo selectEditor = null;
    Map<String, List<AttributeEditorInfo>> groups;

    int selectRow = -1;
    List<AttributeEditorInfo> currentList;

    public EditorSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
        update();
        groups = new HashMap<>();
        loadData();
        table.addClickHandler(event -> {
            HTMLTable.Cell cellForEvent = table.getCellForEvent(event);
            int rowIndex = cellForEvent.getRowIndex();
            if (rowIndex < 1 || rowIndex >= table.getRowCount()) {
                return;
            }
            HTMLTable.RowFormatter rowFormatter = table.getRowFormatter();
            if (selectRow >= 1) {
                rowFormatter.getElement(selectRow).removeAttribute(SELECT_ATTRIBUTE);
            }
            selectRow = rowIndex;
            rowFormatter.getElement(selectRow).setAttribute(SELECT_ATTRIBUTE, "true");
            selectEditor = currentList.get(rowIndex - 1); //" minus 1 " because we add a header line in the table
            update();
        });
    }

    public static Popup<EditorSelector> getPopup(boolean reuse) {
        if (reuse) {
            if (popup == null) {
                popup = createOne();
            }
            return popup;
        } else {
            return createOne();
        }
    }

    private static Popup<EditorSelector> createOne() {
        EditorSelector selector = new EditorSelector();
        return new Popup<>(selector);
    }

    private void loadData() {
        groups.clear();
        List<AttributeEditorInfo> editors = AttributeEditorFactory.get().getEditors();
        for (AttributeEditorInfo info : editors) {
            if (info.group == null || info.group.length() == 0) {
                info.group = IAttributeEditor.CATALOG_UNKNOWN;
            }
            List<AttributeEditorInfo> infos = groups.get(info.group);
            if (infos == null) {
                infos = new ArrayList<>();
                groups.put(info.group, infos);
            }
            infos.add(info);
        }

        ImageTextItem fireItem = null;
        for (String key : groups.keySet()) {
            ImageTextItem item = catalog.addItem(null, key, null);
            item.setData(groups.get(key));
            if (fireItem == null) {
                fireItem = item;
            }
        }
        if (fireItem != null) {
            catalog.setValue(fireItem, true);
        }
    }

    @UiHandler("catalog")
    public void catalogCommon(CommonEvent event) {
        if (event.isSelect()) {
            selectRow = -1;
            selectEditor = null;
            update();
            ImageTextItem item = event.getValue();
            currentList = (List<AttributeEditorInfo>) item.getData();
            renderData(currentList);
        }
    }

    private Label header(String name) {
        Label label = new Label(name);
        label.setStyleName("ai-header");
        return label;
    }

    private void renderData(List<AttributeEditorInfo> infos) {
        table.removeAllRows();
        int row = 0;
        int col = 0;
        table.setWidget(row, col++, header("名称"));
        table.setWidget(row, col++, header("作者"));
        table.setWidget(row, col++, header("介绍"));
        row++;
        for (AttributeEditorInfo info : infos) {
            col = 0;
            table.setText(row, col++, info.name);
            table.setText(row, col++, info.author);
            table.setText(row, col++, info.summary);
            row++;
        }
    }

    private void update() {
        saveBar.enableSave(selectEditor != null);
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            AttributeEditorMetaData editorValue = new AttributeEditorMetaData();
            editorValue.code = selectEditor.code;
            editorValue.name = selectEditor.name;
            fireEvent(CommonEvent.okEvent(editorValue));
        } else {
            fireEvent(event);
        }
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(600, 380);
    }


    interface EditorSelectorUiBinder extends UiBinder<DockLayoutPanel, EditorSelector> {
    }
}