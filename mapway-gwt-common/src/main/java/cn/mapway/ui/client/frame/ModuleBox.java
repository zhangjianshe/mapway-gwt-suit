package cn.mapway.ui.client.frame;

import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * ModuleBox
 *
 * @author zhang
 */
public class ModuleBox extends CommonEventComposite implements IData<ModuleInfo> {
    private static final ModuleBoxUiBinder ourUiBinder = GWT.create(ModuleBoxUiBinder.class);
    ModuleInfo data;
    private final ClickHandler clickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            fireEvent(CommonEvent.selectEvent(data));
        }
    };
    @UiField
    Image icon;
    @UiField
    Label name;
    @UiField
    Label subtitle;
    @UiField
    FontIcon fontIcon;

    public ModuleBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.addDomHandler(clickHandler, ClickEvent.getType());
    }

    @Override
    public ModuleInfo getData() {
        return data;
    }

    @Override
    public void setData(ModuleInfo obj) {
        data = obj;
        toUI();
    }

    private void toUI() {
        if (data.unicode != null && data.unicode.length() > 0) {
            icon.setVisible(false);
            fontIcon.setVisible(true);
            fontIcon.setIconUnicode(data.unicode);
        } else {
            fontIcon.setVisible(false);
            icon.setVisible(true);
            icon.setResource(data.icon);

        }
        name.setText(data.name);
        subtitle.setText(data.summary);
    }

    interface ModuleBoxUiBinder extends UiBinder<HorizontalPanel, ModuleBox> {
    }
}
