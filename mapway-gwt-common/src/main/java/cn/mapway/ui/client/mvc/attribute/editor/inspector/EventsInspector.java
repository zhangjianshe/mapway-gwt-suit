package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.mvc.event.EventInfo;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.IEachElement;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * 对象事件查看
 */
public class EventsInspector extends CommonEventComposite implements IData<String> {
    private static final EventsInspectorUiBinder ourUiBinder = GWT.create(EventsInspectorUiBinder.class);
    List<EventInfo> eventInfos;
    @UiField
    ZTree eventTree;
    private String moduleCode;

    public EventsInspector() {
        initWidget(ourUiBinder.createAndBindUi(this));
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
            eventTree.clear();
            return;
        }
        eventInfos = moduleInfo.getEvents();
        toUI();
    }

    public void updateUIState(String script) {
        // clear all event status
        if (StringUtil.isBlank(script)) {
            eventTree.eachItem(null, item -> {
                Widget rightWidget = item.getRightWidget(0);
                rightWidget.removeStyleName("ai-header");
                return true;
            });
            return;
        }

        eventTree.eachItem(null, new IEachElement<ImageTextItem>() {
            @Override
            public boolean each(ImageTextItem item) {
                EventInfo eventInfo = (EventInfo) item.getData();
                if (script.contains(eventInfo.code)) {
                    Widget rightWidget = item.getRightWidget(0);
                    rightWidget.addStyleName("ai-header");
                }
                return true;
            }
        });
    }

    private void toUI() {

        eventTree.clear();

        for (EventInfo eventInfo : eventInfos) {
            ImageTextItem item = eventTree.addFontIconItem(null, eventInfo.name, Fonts.THUNDER);
            item.setData(eventInfo);
            item.appendWidget(new Label(eventInfo.code), null);
        }
    }

    @UiHandler("eventTree")
    public void eventTreeCommon(CommonEvent event) {
        if (event.isSelect()) {

        } else if (event.isDoubleClick()) {
            ImageTextItem item = event.getValue();
            EventInfo eventInfo = (EventInfo) item.getData();
            fireEvent(CommonEvent.selectEvent(eventInfo));
        }
    }

    interface EventsInspectorUiBinder extends UiBinder<DockLayoutPanel, EventsInspector> {
    }
}