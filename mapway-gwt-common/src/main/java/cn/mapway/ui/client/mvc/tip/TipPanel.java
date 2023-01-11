package cn.mapway.ui.client.mvc.tip;


import cn.mapway.ui.client.tools.IData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.List;


/**
 * TipPanel
 * 第一次使用 显示提示信息
 * 这是一个全屏的 遮罩层
 *
 * @author zhang
 */
public class TipPanel extends PopupPanel implements IData<List<TipData>> {
    private static final TipPanelUiBinder ourUiBinder = GWT.create(TipPanelUiBinder.class);
    private static TipPanel GLOBAL_TIP_PANEL;
    List<TipData> tips;
    int tipIndex = -1;
    @UiField
    HTML left;
    @UiField
    HTML right;
    @UiField
    HTML top;
    @UiField
    HTML bottom;
    @UiField
    HTML content;
    @UiField
    LayoutPanel root;
    @UiField
    Button btnPrev;
    @UiField
    Button btnNext;
    @UiField
    Button btnClose;
    @UiField
    DockLayoutPanel tipLayer;
    @UiField
    Label header;
    @UiField
    HTMLPanel container;
    @UiField
    ScrollPanel scroller;

    Widget current;
    Frame frame;

    public TipPanel() {
        setWidget(ourUiBinder.createAndBindUi(this));
        setStyleName("NULL");
        getElement().getStyle().setZIndex(60001);
        current = scroller;
    }

    public static TipPanel get() {
        if (GLOBAL_TIP_PANEL == null) {
            GLOBAL_TIP_PANEL = new TipPanel();
        }
        GLOBAL_TIP_PANEL.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
        GLOBAL_TIP_PANEL.setPopupPosition(0, 0);
        return GLOBAL_TIP_PANEL;
    }

    @Override
    public List<TipData> getData() {
        return tips;
    }

    @Override
    public void setData(List<TipData> obj) {
        tips = obj;
        toUI();
    }

    private void toUI() {
        if (tips == null || tips.size() == 0) {
            hide();
            return;
        }
        tipIndex = -1;
        nextTip();
    }

    private void nextTip() {
        tipIndex++;
        if (tipIndex >= tips.size()) {
            tipIndex = 0;
        }
        showTip(tipIndex);
    }

    private void prevTip() {
        tipIndex--;
        if (tipIndex < 0) {
            tipIndex = tips.size() - 1;
        }
        showTip(tipIndex);
    }

    public void showTip(int index) {
        if (tips == null || tips.size() == 0) {
            hide();
        }
        if (index < 0 || index >= tips.size()) {
            tipIndex = 0;
        }
        //获取 widget的 空间位置
        TipData tipData = tips.get(index);
        int leftPos = tipData.target.getAbsoluteLeft();
        int topPos = tipData.target.getAbsoluteTop();
        int width = tipData.target.getOffsetWidth();
        int height = tipData.target.getOffsetHeight();
        int clientWidth = Window.getClientWidth();
        int clientHeight = Window.getClientHeight();

        root.setWidgetLeftWidth(left, 0, Style.Unit.PX, leftPos, Style.Unit.PX);
        root.setWidgetLeftRight(right, leftPos + width, Style.Unit.PX, 0, Style.Unit.PX);
        root.setWidgetTopHeight(top, 0, Style.Unit.PX, topPos, Style.Unit.PX);
        root.setWidgetLeftWidth(top, leftPos, Style.Unit.PX, width, Style.Unit.PX);
        root.setWidgetTopBottom(bottom, topPos + height, Style.Unit.PX, 0, Style.Unit.PX);
        root.setWidgetLeftWidth(bottom, leftPos, Style.Unit.PX, width, Style.Unit.PX);

        root.setWidgetTopHeight(content, topPos, Style.Unit.PX, height, Style.Unit.PX);
        root.setWidgetLeftWidth(content, leftPos, Style.Unit.PX, width, Style.Unit.PX);
        container.clear();
        switch (tipData.type) {
            case TipData.TIP_TYPE_HTML:
                switchToScroll();
                container.add(new HTML(tipData.getHtml()));
                break;
            case TipData.TIP_TYPE_WIDGET:
                switchToScroll();
                content.setVisible(true);
                container.add(tipData.content);
                break;
            case TipData.TIP_TYPE_URL:
                switchToFrame();
                frame.setUrl(tipData.getUrl());
                break;
        }
        header.setText(tipData.header);

        int tipLeft;
        int tipTop;
        int tipWidth = 300;
        int tipHeight = 300;

        boolean center = false;
        //调正提示的位置
        if ((leftPos + width) + tipWidth < clientWidth) {
            tipLeft = leftPos + width + 20;
        } else if (leftPos < tipWidth) {
            center = true;
            tipLeft = clientWidth / 2 - tipWidth / 2;
        } else {
            tipLeft = leftPos - tipWidth - 20;
        }

        if (center) {
            if (topPos + height + tipHeight + 20 < clientHeight) {
                tipTop = topPos + height + 20;
            } else {
                tipTop = topPos - tipWidth - 20;
            }
        } else {
            if (topPos + height >= clientHeight) {
                tipTop = Math.min(tipHeight + 20, topPos);
            } else {
                tipTop = topPos;
            }
        }
        root.setWidgetLeftWidth(tipLayer, tipLeft, Style.Unit.PX, tipWidth, Style.Unit.PX);
        root.setWidgetTopHeight(tipLayer, tipTop, Style.Unit.PX, tipHeight, Style.Unit.PX);
    }

    @UiHandler("btnClose")
    public void btnCloseClick(ClickEvent event) {
        hide(true);
    }

    @UiHandler("btnNext")
    public void btnNextClick(ClickEvent event) {
        nextTip();
    }

    @UiHandler("btnPrev")
    public void btnPrevClick(ClickEvent event) {
        prevTip();
    }

    private void switchToFrame() {
        if (frame == null) {
            frame = new Frame();
            frame.setSize("100%", "100%");
        }
        if (current != frame) {
            tipLayer.remove(current);
            current = frame;
            tipLayer.add(current);
        }
    }

    private void switchToScroll() {

        if (current != scroller) {
            tipLayer.remove(current);
            current = scroller;
            tipLayer.add(scroller);
        }
    }

    interface TipPanelUiBinder extends UiBinder<LayoutPanel, TipPanel> {
    }
}
