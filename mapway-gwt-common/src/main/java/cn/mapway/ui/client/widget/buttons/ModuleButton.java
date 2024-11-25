package cn.mapway.ui.client.widget.buttons;

import cn.mapway.ui.client.mvc.ModuleInfo;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import elemental2.core.JsString;

public class ModuleButton extends CommonEventComposite implements IData<ModuleInfo> , HasClickHandlers {
    private ModuleInfo data;

    @Override
    public ModuleInfo getData() {
        return data;
    }

    @Override
    public void setData(ModuleInfo moduleInfo) {
        data=moduleInfo;
        toUI();
    }

    private void toUI() {
        name.setText(data.name);
        icon.setText(JsString.fromCodePoint(Integer.parseInt(data.unicode, 16)));
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    interface ModuleButtonUiBinder extends UiBinder<HTMLPanel, ModuleButton> {
    }

    private static ModuleButtonUiBinder ourUiBinder = GWT.create(ModuleButtonUiBinder.class);
    @UiField
    Label name;
    @UiField
    Label icon;

    public ModuleButton() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}