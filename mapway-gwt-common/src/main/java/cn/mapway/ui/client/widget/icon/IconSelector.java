package cn.mapway.ui.client.widget.icon;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.AiLabel;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.SearchBox;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * IconSelector
 * 图标选择面板
 *
 * @author zhang
 */
public class IconSelector extends CommonEventComposite implements IData<String> {
    private static final IconSelectorUiBinder ourUiBinder = GWT.create(IconSelectorUiBinder.class);
    private static Popup<IconSelector> popup;
    private final ClickHandler fontClicked = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            AiLabel label = (AiLabel) event.getSource();
            String key = (String) label.getData();
            fireEvent(CommonEvent.dataEvent(Fonts.unicodes.get(key)));
        }
    };
    String unicode;
    @UiField
    HTMLPanel panel;
    @UiField
    Label lbInfo;
    private final MouseOverHandler fontOverHandler = new MouseOverHandler() {
        @Override
        public void onMouseOver(MouseOverEvent event) {
            AiLabel label = (AiLabel) event.getSource();
            String key = (String) label.getData();
            String text = key + " " + Fonts.unicodes.get(key);
            lbInfo.setText(text);
        }
    };
    @UiField
    Button btnClear;
    @UiField
    SStyle style;
    @UiField
    SearchBox searchBox;

    public IconSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));

        searchBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                filter(event.getValue());
            }
        });

        filter("");
    }

    public static Popup<IconSelector> getPopup(boolean reuse) {
        if (reuse) {
            if (popup == null) {
                popup = createOne();
            }
            return popup;
        } else {
            return createOne();
        }
    }

    private static Popup<IconSelector> createOne() {
        IconSelector selector = new IconSelector();
        Popup<IconSelector> popup = new Popup<>(selector);
        popup.setAutoHideEnabled(true);
        return popup;
    }

    private void filter(String value) {
        if (StringUtil.isBlank(value)) {
            panel.clear();
            for (String key : Fonts.unicodes.keySet()) {
                AiLabel label = new AiLabel();
                label.getElement().setInnerHTML(Fonts.toHtmlEntity(Fonts.unicodes.get(key)));
                label.setStyleName(style.item());
                panel.add(label);
                label.addMouseOverHandler(fontOverHandler);
                label.setData(key);
                label.addClickHandler(fontClicked);
            }
        } else {
            String v = value.toLowerCase();
            panel.clear();
            for (String key : Fonts.unicodes.keySet()) {
                if (key.toLowerCase().contains(v)) {
                    AiLabel label = new AiLabel("");
                    label.getElement().setInnerHTML(Fonts.toHtmlEntity(Fonts.unicodes.get(key)));
                    label.setStyleName(style.item());
                    panel.add(label);
                    label.addMouseOverHandler(fontOverHandler);
                    label.setData(key);
                    label.addClickHandler(fontClicked);
                }

            }
        }
    }

    @Override
    public String getData() {
        return unicode;
    }

    @Override
    public void setData(String obj) {
        unicode = obj;
    }

    @UiHandler("btnClear")
    public void btnClearClick(ClickEvent event) {
        fireEvent(CommonEvent.clearEvent(null));
    }

    interface SStyle extends CssResource {

        String item();

        String box();

        String panel();

        String topbar();

        String info();
    }

    interface IconSelectorUiBinder extends UiBinder<DockLayoutPanel, IconSelector> {
    }
}
