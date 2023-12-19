package cn.mapway.ui.client.mvc.attribute.editor.editorselector;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.design.EditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorFactory;
import cn.mapway.ui.client.mvc.attribute.editor.AttributeEditorInfo;
import cn.mapway.ui.client.mvc.attribute.editor.IAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.IEditorDesigner;
import cn.mapway.ui.client.mvc.attribute.editor.proxy.AttributeItemEditorProxy;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.client.widget.dialog.SaveBar;
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
    SaveBar saveBar;
    @UiField
    HTMLPanel designPanel;
    @UiField
    HTMLPanel previewPlaceholder;
    @UiField
    Label lbSummary;
    @UiField
    AttributeItemEditorProxy preview;
    @UiField
    TabLayoutPanel tableLayout;
    AttributeEditorInfo selectEditor = null;

    IEditorDesigner currentDesign = null;
    //正在编辑的属性中定义的editorData实例
    EditorMetaData currentEditData;
    String initEditorCode;
    Map<String, EditorHorizontalPanel> panelCache = new HashMap<>();

    public EditorSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
        update();


        tableLayout.addSelectionHandler(event -> {
            Widget tabWidget = tableLayout.getWidget(event.getSelectedItem());
            if (tabWidget instanceof EditorHorizontalPanel) {
                EditorHorizontalPanel horizontalPanel = (EditorHorizontalPanel) tabWidget;
                horizontalPanel.selectEditorCode(initEditorCode);
            }
        });
        loadData();
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

        panelCache.clear();

        Map<String, List<AttributeEditorInfo>> groups = new HashMap<>();

        List<AttributeEditorInfo> editors = AttributeEditorFactory.get().getEditors();
        for (AttributeEditorInfo info : editors) {
            if (info.group == null || info.group.length() == 0) {
                info.group = IAttributeEditor.CATALOG_UNKNOWN;
            }
            List<AttributeEditorInfo> group = groups.get(info.group);
            if (group == null) {
                group = new ArrayList<>();
                groups.put(info.group, group);
            }
            group.add(info);
        }
        //排序
        groups.values().forEach(list -> {
            Collections.sort(list, Comparator.comparingInt(i -> i.rank));
        });
        //分好组后创建 UI


        for (String key : groups.keySet()) {
            EditorHorizontalPanel panel = new EditorHorizontalPanel();
            panel.addCommonHandler(event -> {
                if (event.isSelect()) {
                    selectEditor = event.getValue();
                    changePreview();
                }
            });
            panel.setData(groups.get(key));
            String html = "<span style='font-size:1.1rem;'>" + key + "</span>";
            tableLayout.add(panel, html, true);
            panelCache.put(key, panel);
        }

    }

    private void changePreview() {
        designPanel.clear();
        //顶一个一 FakeAttribute
        AbstractAttribute adaptor = new AbstractAttribute("preview", selectEditor.name, selectEditor.code) {
            @Override
            public Object getValue() {
                return null;
            }

            @Override
            public void setValue(Object value) {
            }
        };
        preview.createEditorInstance(adaptor);

        currentDesign = preview.getEditor().getDesigner();
        if (currentDesign != null) {
            Widget designWidget = currentDesign.getDesignRoot();
            designPanel.add(designWidget);
            if (selectEditor.code.equals(this.currentEditData.getEditorCode())) {
                //原来的设计器和新选择的设计类型一致 currentEditValue 用户之前选中的编辑器里的设计数据
                currentDesign.updateValue(currentEditData.getParameterValues());
            }
        } else {
            HTML html = new HTML("该输入组件无需参数");
            html.setStyleName("ai-flex-panel");
            html.setHeight("200px");
            designPanel.add(html);
        }
        AttributeEditorInfo info = AttributeEditorFactory.get().findByCode(selectEditor.code);
        String tip = info.name + " (" + info.summary + ") " + preview.getEditor().getEditorTip();
        lbSummary.setText(tip);
        update();
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
            if (selectEditor != null) {

                EditorMetaData editorMetaData = new EditorMetaData();
                editorMetaData.setEditorCode(selectEditor.code);
                editorMetaData.setEditorName(selectEditor.name);
                if (currentDesign != null && currentDesign.getParameterValues() != null) {
                    editorMetaData.getParameterValues().addAll(currentDesign.getParameterValues());
                }
                fireEvent(CommonEvent.okEvent(editorMetaData));
            }
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
    public void editorValue(EditorMetaData editValue) {
        currentEditData = editValue;
        //缺省选中 editValue 对应的组件
        initEditorCode = currentEditData.getEditorCode();
        AttributeEditorInfo info = AttributeEditorFactory.get().findByCode(editValue.getEditorCode());

        int widgetIndex = tableLayout.getWidgetIndex(panelCache.get(info.group));
        if (widgetIndex == tableLayout.getSelectedIndex()) {
            //不切换分组 需要手工触发事件
            Widget tabWidget = tableLayout.getWidget(widgetIndex);
            if (tabWidget instanceof EditorHorizontalPanel) {
                ((EditorHorizontalPanel) tabWidget).selectEditorCode(initEditorCode);
            }
        } else {
            tableLayout.selectTab(widgetIndex, true);
        }
    }


    interface EditorSelectorUiBinder extends UiBinder<DockLayoutPanel, EditorSelector> {
    }
}