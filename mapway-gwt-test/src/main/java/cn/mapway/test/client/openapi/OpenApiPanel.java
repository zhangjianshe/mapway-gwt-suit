package cn.mapway.test.client.openapi;

import cn.mapway.doc.openapi.module.OpenApiDocument;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;

public class OpenApiPanel extends CommonEventComposite implements IData<OpenApiDocument> {
    private OpenApiDocument data;

    @Override
    public OpenApiDocument getData() {
        return data;
    }

    @Override
    public void setData(OpenApiDocument obj) {
        data=obj;
        toUI();
    }

    private void toUI() {
        lbName.setText(data.info.title);
        lbVersion.setText(data.info.version);
        tree.load(data);
        ddlServers.setServerList(data.servers);
    }

    interface OpenApiPanelUiBinder extends UiBinder<DockLayoutPanel, OpenApiPanel> {
    }

    private static OpenApiPanelUiBinder ourUiBinder = GWT.create(OpenApiPanelUiBinder.class);
    @UiField
    Label lbName;
    @UiField
    Button btnLoad;
    @UiField
    Label lbVersion;
    @UiField
    EntryTree tree;
    @UiField
    ServersDropdown ddlServers;

    public OpenApiPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("btnLoad")
    public void btnLoadClick(ClickEvent event) {
        fireEvent(CommonEvent.loadEvent(null));
    }

    @UiHandler("tree")
    public void treeCommon(CommonEvent event) {
    }
}