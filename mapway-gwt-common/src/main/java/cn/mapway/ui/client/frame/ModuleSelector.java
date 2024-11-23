package cn.mapway.ui.client.frame;

import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.ModuleFactory;
import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.mvc.SwitchModuleData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.List;

/**
 * ModuleSelector
 * 模型选择
 *
 * @author zhang
 */
public class ModuleSelector extends CommonEventComposite {
    private static final ModuleSelectorUiBinder ourUiBinder = GWT.create(ModuleSelectorUiBinder.class);
    private final CommonEventHandler moduleBoxHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent event) {
            if (event.isSelect()) {
                ModuleInfo moduleInfo = (ModuleInfo) event.getValue();
                SwitchModuleData switchModuleData = new SwitchModuleData(moduleInfo.code, "");
                fireEvent(CommonEvent.switchEvent(switchModuleData));
            }
        }
    };
    @UiField
    HTMLPanel list;

    public ModuleSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setData(List<String> moduleCodes) {
        list.clear();
        ModuleFactory moduleFactory = BaseAbstractModule.getModuleFactory();
        if (moduleCodes != null) {
            for (String moduleCode : moduleCodes) {
                ModuleInfo moduleInfo = moduleFactory.findModuleInfo(moduleCode);
                ModuleBox moduleBox = new ModuleBox();
                moduleBox.setData(moduleInfo);
                moduleBox.addCommonHandler(moduleBoxHandler);
                list.add(moduleBox);
            }
        }
    }

    interface ModuleSelectorUiBinder extends UiBinder<DockLayoutPanel, ModuleSelector> {
    }
}
