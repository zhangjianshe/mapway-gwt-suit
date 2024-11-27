package cn.mapway.ui.client.widget.icon;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

/**
 * IconSelector
 * 图标选择面板
 *
 * @author zhang
 */
public class IconSelector extends CommonEventComposite implements IData<String> {
    private static final IconSelectorUiBinder ourUiBinder = GWT.create(IconSelectorUiBinder.class);
    private static Popup<IconSelector> popup;
    private final MouseOutHandler fontOutHandler = new MouseOutHandler() {
        @Override
        public void onMouseOut(MouseOutEvent event) {
            FontIcon icon = (FontIcon) event.getSource();
            icon.removeStyleName("ai-hover");
        }
    };
    String unicode;
    private final ClickHandler fontClicked = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            FontIcon icon = (FontIcon) event.getSource();

            unicode = icon.getIconUnicode();
            fireEvent(CommonEvent.dataEvent(unicode));
        }
    };
    @UiField
    FlexTable panel;
    @UiField
    Label lbInfo;
    @UiField
    Button btnClear;
    private final MouseOverHandler fontOverHandler = new MouseOverHandler() {
        @Override
        public void onMouseOver(MouseOverEvent event) {
            FontIcon icon = (FontIcon) event.getSource();
            icon.addStyleName("ai-hover");
            String text = icon.getData() + " " + icon.getIconUnicode();
            lbInfo.setText(text);
        }
    };

    public IconSelector() {
        initWidget(ourUiBinder.createAndBindUi(this));
        init();
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

    /**
     * 初始化所有的图标字体
     */
    private void init() {
        int row = 0;
        int col = 0;
        HTMLTable.CellFormatter cellFormatter = panel.getCellFormatter();
        for (String key : Fonts.unicodes.keySet()) {
            FontIcon fontIcon = new FontIcon(Fonts.unicodes.get(key));
            fontIcon.setSize(32, Style.Unit.PX);
            panel.setWidget(row, col, fontIcon);
            cellFormatter.setHorizontalAlignment(row, col, HasHorizontalAlignment.ALIGN_CENTER);
            fontIcon.addClickHandler(fontClicked);
            fontIcon.addMouseOverHandler(fontOverHandler);
            fontIcon.addMouseOutHandler(fontOutHandler);
            fontIcon.setData(key);
            col++;
            if (col > 15) {
                row++;
                col = 0;
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

    interface IconSelectorUiBinder extends UiBinder<DockLayoutPanel, IconSelector> {
    }
}
