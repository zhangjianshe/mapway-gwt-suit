package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.event.EventInfo;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.AiFlexTable;
import cn.mapway.ui.client.widget.AiLabel;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * 对象事件查看
 */
public class EventsInspector extends CommonEventComposite implements IData<String> {
    private static final EventsInspectorUiBinder ourUiBinder = GWT.create(EventsInspectorUiBinder.class);
    @UiField
    AiFlexTable eventTable;
    List<EventInfo> eventInfos;
    private String moduleCode;

    public EventsInspector() {
        initWidget(ourUiBinder.createAndBindUi(this));
        eventTable.addDoubleClickHandler(event -> {
            Size cellForDoubleClickEvent = eventTable.getCellForDoubleClickEvent(event);
            int row = cellForDoubleClickEvent.getYAsInt();
            if (row > 0) {
                Widget widget = eventTable.getWidget(row, 1);
                if (widget instanceof AiLabel) {
                    AiLabel label = (AiLabel) widget;
                    fireEvent(CommonEvent.selectEvent(label.getData()));
                }
            }
        });
        eventTable.addClickHandler((event) -> {
            HTMLTable.Cell cell = eventTable.getCellForEvent(event);
            int row = cell.getRowIndex();
            if (row > 0) {
                Widget widget = eventTable.getWidget(row, 1);
                if (widget instanceof AiLabel) {
                    AiLabel label = (AiLabel) widget;
                    fireEvent(CommonEvent.selectEvent(label.getData()));
                }
            }
        });
    }

    /**
     * @return
     */
    @Override
    public String getData() {
        return moduleCode;
    }

    /**
     * @param moduleCode
     */
    @Override
    public void setData(String moduleCode) {
        this.moduleCode = moduleCode;
        ModuleInfo moduleInfo = BaseAbstractModule.getModuleFactory().findModuleInfo(moduleCode);
        if (moduleInfo == null) {
            eventTable.removeAllRows();
            return;
        }
        eventInfos = moduleInfo.getEvents();
        toUI();
    }

    public void updateUIState(String script) {
        if (StringUtil.isBlank(script)) {
            return;
        }

        for (int i = 1; i < eventTable.getRowCount(); i++) {
            AiLabel label = (AiLabel) eventTable.getWidget(i, 1);
            String code = label.getText();
            if (script.contains(code)) {
                label.addStyleName("ai-bold");
            } else {
                label.removeStyleName("ai-bold");
            }
        }
    }

    private void toUI() {

        eventTable.removeAllRows();

        int row = 0;
        int col = 0;
        eventTable.setWidget(row, col++, new Header("名称"));
        eventTable.setWidget(row, col++, new Header("代码"));
        for (EventInfo eventInfo : eventInfos) {
            row++;
            col = 0;
            eventTable.setWidget(row, col++, new AiLabel(eventInfo.name));
            AiLabel aiLabel = new AiLabel(eventInfo.code);
            aiLabel.setData(eventInfo);
            eventTable.setWidget(row, col++, aiLabel);
        }
    }

    interface EventsInspectorUiBinder extends UiBinder<DockLayoutPanel, EventsInspector> {
    }
}