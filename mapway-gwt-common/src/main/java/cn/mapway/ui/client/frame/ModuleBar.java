package cn.mapway.ui.client.frame;

import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.mvc.SwitchModuleData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.buttons.ModuleButton;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import java.util.List;

public class ModuleBar extends CommonEventComposite {
    HorizontalPanel panel;
    ModuleButton selected = null;
    private final ClickHandler itemClicked = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {

            ModuleButton item = (ModuleButton) event.getSource();
            selectItem(item);
        }
    };

    public ModuleBar() {
        panel = new HorizontalPanel();
        panel.setSpacing(10);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        initWidget(panel);
    }

    private void selectItem(ModuleButton item) {
        if (selected != null) {
            selected.setSelect(false);
        }
        selected = item;
        selected.setSelect(true);
        ModuleInfo moduleInfo = selected.getData();
        SwitchModuleData switchModuleData = new SwitchModuleData(moduleInfo.code, "");
        fireEvent(CommonEvent.switchEvent(switchModuleData));
    }

    private void renderMenu(List<String> codes) {
        panel.clear();

        for (String code : codes) {
            ModuleButton item = new ModuleButton();
            item.setData(BaseAbstractModule.getModuleFactory().findModuleInfo(code));
            item.addDomHandler(itemClicked, ClickEvent.getType());
            panel.add(item);
        }
    }
}
