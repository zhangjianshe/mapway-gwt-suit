package cn.mapway.test.client.openapi;

import cn.mapway.doc.openapi.module.OpenApiDocument;
import cn.mapway.ui.client.tools.JSON;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import elemental2.dom.Event;
import elemental2.dom.ProgressEvent;
import elemental2.dom.XMLHttpRequest;
import jsinterop.base.Js;

public class OpenApiWindow extends CommonEventComposite {
    interface OpenApiWindowUiBinder extends UiBinder<LayoutPanel, OpenApiWindow> {
    }

    private static final OpenApiWindowUiBinder ourUiBinder = GWT.create(OpenApiWindowUiBinder.class);
    @UiField
    Button btnSearch;
    @UiField
    TextBox txtUrl;
    @UiField
    OpenApiPanel panel;
    @UiField
    LayoutPanel root;
    @UiField
    HTMLPanel panelSearch;
    @UiField
    Label lbMessage;

    public OpenApiWindow() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("btnSearch")
    public void btnSearchClick(ClickEvent event) {

        final XMLHttpRequest request = new XMLHttpRequest();

        request.open("GET", txtUrl.getValue());
        request.onload = new XMLHttpRequest.OnloadFn() {
            @Override
            public void onInvoke(ProgressEvent p0) {
                root.setWidgetVisible(panel, true);
                root.setWidgetVisible(panelSearch, false);
                OpenApiDocument doc = Js.uncheckedCast(JSON.parse(request.responseText));
                panel.setData(doc);
            }
        };
        request.onerror = new XMLHttpRequest.OnerrorFn() {
            @Override
            public Object onInvoke(Event p0) {
                root.setWidgetVisible(panel, false);
                root.setWidgetVisible(panelSearch, true);

                return null;
            }
        };
        request.send();

    }

    @UiHandler("panel")
    public void panelCommon(CommonEvent event) {
        if (event.isLoad()) {
            root.setWidgetVisible(panel, false);
            root.setWidgetVisible(panelSearch, true);
            lbMessage.setText("");
        }
    }
}