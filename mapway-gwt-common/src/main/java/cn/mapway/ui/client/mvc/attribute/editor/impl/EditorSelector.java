package cn.mapway.ui.client.mvc.attribute.editor.impl;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.editor.*;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.*;

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
    @UiField
    HTMLPanel designPanel;
    @UiField
    HTMLPanel previewPlaceholder;
    AttributeEditorInfo selectEditor = null;
    Map<String, List<AttributeEditorInfo>> groups;

    int selectRow = -1;
    List<AttributeEditorInfo> currentList;

    IEditorDesigner currentDesign = null;
    EditorOption currentEditValue;

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
            //新选择了一个设计期重新渲染 属性编辑器的参数设计UI
            renderSelectDesign(selectEditor);
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

    /**
     * 切换组件时 重新设置组件参数UI
     *
     * @param selectEditor 设计期的编辑器信息
     */
    private void renderSelectDesign(AttributeEditorInfo selectEditor) {
        clearPreview();
        IAttributeEditor editor = AttributeEditorFactory.get().createEditor(selectEditor.code, false);
        if (editor == null) {
            previewPlaceholder.add(new Label("创建组件失败"));
        } else {
            previewPlaceholder.add(editor.getDisplayWidget());
            currentDesign = editor.getDesigner();
            if (currentDesign != null) {
                Widget designWidget = currentDesign.getDesignRoot();
                designPanel.add(designWidget);
                if (selectEditor.code.equals(this.currentEditValue.get(EditorOption.KEY_EDITOR_CODE))) {
                    //原来的设计器和新选择的设计类型一致
                    currentDesign.parseDesignOptions(currentEditValue.getDesignOptions());
                }
            }
        }
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

        //排序
        groups.values().forEach(list -> {
            Collections.sort(list, Comparator.comparingInt(i -> i.rank));
        });
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
            clearPreview();
        }
    }

    private void clearPreview() {
        designPanel.clear();
        previewPlaceholder.clear();
        currentDesign = null;
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
        table.setWidget(row, col++, header("#"));
        table.setWidget(row, col++, header("名称"));
        table.setWidget(row, col++, header("作者"));
        table.setWidget(row, col++, header("介绍"));
        row++;
        HTMLTable.ColumnFormatter columnFormatter = table.getColumnFormatter();
        for (AttributeEditorInfo info : infos) {
            col = 0;
            table.setWidget(row, col++, new FontIcon<>(info.icon));
            table.setText(row, col++, info.name);
            table.setText(row, col++, info.author);
            table.setText(row, col++, info.summary);
            row++;
        }
        columnFormatter.setWidth(0, "30px");
    }

    private void update() {
        saveBar.enableSave(selectEditor != null);
        if (selectEditor != null) {
            saveBar.message(selectEditor.name);
        }
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            EditorOption option = new EditorOption();
            option.set(EditorOption.KEY_EDITOR_CODE, selectEditor.code);
            option.set(EditorOption.KEY_EDITOR_NAME, selectEditor.name);

            if (currentDesign != null) {
                option.setDesignOptions(currentDesign.toDesignOptions());
            }
            fireEvent(CommonEvent.okEvent(option));
        } else {
            fireEvent(event);
        }
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(950, 520);
    }

    /**
     * 设置设计时的参数
     * 每一个属性都对应一个设计期的参数 在弹出这个对话框的期间这个值是不变的
     * 除非用户选择了其他的编辑器，并且同时变更了在点击OK按钮后 会重新变更这个数据 传递给属性定义
     * 每次弹出 必须调用这个方法
     *
     * @param editValue
     */
    public void editorValue(EditorOption editValue) {
        currentEditValue = editValue;
    }

    interface EditorSelectorUiBinder extends UiBinder<DockLayoutPanel, EditorSelector> {
    }
}