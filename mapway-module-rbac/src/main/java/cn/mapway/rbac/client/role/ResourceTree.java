package cn.mapway.rbac.client.role;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.editor.inspector.ObjectInspector;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.client.widget.buttons.AiButton;
import cn.mapway.ui.client.widget.buttons.CheckBoxEx;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import elemental2.dom.Element;
import elemental2.dom.ScrollIntoViewOptions;
import jsinterop.base.Js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceTree extends CommonEventComposite {
    private static final ResourceTreeUiBinder ourUiBinder = GWT.create(ResourceTreeUiBinder.class);
    private static Dialog<ResourceTree> dialog;
    @UiField
    Button btnCreate;
    @UiField
    SaveBar saveBar;
    @UiField
    ObjectInspector objectInspector;
    @UiField
    Button btnDelete;
    @UiField
    HTMLPanel indexPanel;
    @UiField
    FlexTable table;
    ResourceAttrProvider resourceAttrProvider;
    Map<String, List<CheckBoxEx>> checkGroups = new HashMap<>();
    private final ClickHandler scrollToGroup = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            Label source = (Label) event.getSource();
            String catalogName = source.getText();
            List<CheckBoxEx> checkBoxExes = checkGroups.get(catalogName);
            if (checkBoxExes != null && !checkBoxExes.isEmpty()) {
                Element element = Js.uncheckedCast(checkBoxExes.get(0).getElement());
                ScrollIntoViewOptions viewOptions = ScrollIntoViewOptions.create();
                viewOptions.setBlock("start");
                viewOptions.setBehavior("smooth");
                element.scrollIntoView(viewOptions);
            }
        }
    };
    List<RbacResourceEntity> selectedItems = new ArrayList<>();
    private final ClickHandler selectAll = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            IData source = (IData) event.getSource();
            String catalogName = (String) source.getData();
            List<CheckBoxEx> checkBoxExes = checkGroups.get(catalogName);
            if (checkBoxExes != null) {
                for (CheckBoxEx checkBoxEx : checkBoxExes) {
                    checkBoxEx.setValue(true, false);
                }
            }
            updateSelect();
        }
    };
    private final ClickHandler reverSelect = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            IData source = (IData) event.getSource();
            String catalogName = (String) source.getData();
            List<CheckBoxEx> checkBoxExes = checkGroups.get(catalogName);
            if (checkBoxExes != null) {
                for (CheckBoxEx checkBoxEx : checkBoxExes) {
                    checkBoxEx.setValue(!checkBoxEx.getValue(), false);
                }
            }
            updateSelect();
        }
    };
    @UiField
    SStyle style;

    public ResourceTree() {
        initWidget(ourUiBinder.createAndBindUi(this));
        resourceAttrProvider = new ResourceAttrProvider();
        objectInspector.setData(resourceAttrProvider);
        btnDelete.setEnabled(false);
    }

    public static Dialog<ResourceTree> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }

    private static Dialog<ResourceTree> createOne() {
        ResourceTree tree = new ResourceTree();
        return new Dialog(tree, "资源权限点");
    }

    public void load() {
        QueryResourceRequest request = new QueryResourceRequest();
        RbacServerProxy.get().queryResource(request, new AsyncCallbackLambda<RpcResult<QueryResourceResponse>>() {
            @Override
            public void onResult(RpcResult<QueryResourceResponse> result) {
                if (result.isSuccess()) {
                    renderTable(result.getData());
                } else {
                    DataBus.get().message(result.getMessage());
                }
            }
        });
    }

    private void updateSelect() {
        selectedItems.clear();
        for (List<CheckBoxEx> checkBoxExes : checkGroups.values()) {
            for (CheckBoxEx checkBoxEx : checkBoxExes) {
                if (checkBoxEx.getValue()) {
                    selectedItems.add((RbacResourceEntity) checkBoxEx.getData());
                }
            }
        }
        String message = "当前选择" + selectedItems.size() + "个资源点";
        saveBar.msg(message);
        saveBar.enableSave(!selectedItems.isEmpty());
        btnDelete.setEnabled(canDelete());
    }

    private void renderTable(QueryResourceResponse data) {
        indexPanel.clear();
        checkGroups.clear();
        Map<Integer, List<RbacResourceEntity>> catalogs = new HashMap<>();

        //分类
        for (RbacResourceEntity resource : data.getResources()) {
            Integer kind = ResourceKind.fromCode(resource.getKind()).code;
            List<RbacResourceEntity> list = catalogs.get(kind);
            if (list == null) {
                list = new ArrayList<>();
                catalogs.put(kind, list);
            }
            list.add(resource);
        }


        FlexTable.FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
        int row = -1;
        int col = 0;
        for (Integer kind : catalogs.keySet()) {

            ResourceKind resourceKind = ResourceKind.fromCode(kind);
            Label catalogLabel = new Label(resourceKind.getName());
            catalogLabel.setStyleName(style.catalog());
            indexPanel.add(catalogLabel);
            catalogLabel.addClickHandler(scrollToGroup);
            List<RbacResourceEntity> list = catalogs.get(kind);
            row++;
            col = 0;
            HorizontalPanel catalogHeader = new HorizontalPanel();
            catalogHeader.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

            catalogHeader.setSpacing(6);
            Header header = new Header(resourceKind.getName());
            header.setWidth("200px");
            catalogHeader.add(header);

            AiButton all = new AiButton("全选");
            AiButton reverse = new AiButton("反选");
            catalogHeader.add(all);
            catalogHeader.add(reverse);
            all.setData(resourceKind.getName());
            reverse.setData(resourceKind.getName());

            all.addClickHandler(selectAll);
            reverse.addClickHandler(reverSelect);

            table.setWidget(row, col++, catalogHeader);
            cellFormatter.setColSpan(row, col-1, 4);

            List<CheckBoxEx> checkBoxExes = checkGroups.get(resourceKind.getName());
            if (checkBoxExes == null) {
                checkBoxExes = new ArrayList<>();
                checkGroups.put(resourceKind.getName(), checkBoxExes);
            }


            for (RbacResourceEntity resource : list) {
                row++;
                col = 0;
                CheckBoxEx checkBoxEx = new CheckBoxEx();
                checkBoxEx.setValue(false, false);
                checkBoxEx.setData(resource);
                checkBoxExes.add(checkBoxEx);
                checkBoxEx.addValueChangeHandler(event -> {
                    updateSelect();
                });
                table.setWidget(row, col++, checkBoxEx);
                cellFormatter.setWidth(row,col-1,"32px");
                table.setText(row, col++, resource.getCatalog()+"/"+resource.getName());
                table.setText(row, col++, resource.getResourceCode());
                table.setText(row, col++, resource.getSummary());
            }
        }

    }

    @UiHandler("btnCreate")
    public void btnCreateClick(ClickEvent event) {
        RbacResourceEntity newResource = new RbacResourceEntity();
        newResource.setName("新资源");
        newResource.setCatalog("");
        newResource.setResourceCode("");
        newResource.setSummary("");
        newResource.setKind(ResourceKind.RESOURCE_KIND_CUSTOM.getCode());
        newResource.setData("");
        resourceAttrProvider.rebuild(newResource);
        objectInspector.setSaveButtonEnable(true);
        objectInspector.setSaveButtonVisible(true);
    }

    private boolean canDelete() {
        return selectedItems.size() == 1;
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            fireEvent(CommonEvent.okEvent(selectedItems));
        } else {
            fireEvent(event);
        }
    }

    @UiHandler("objectInspector")
    public void objectInspectorCommon(CommonEvent event) {
        if (event.isSave()) {
            doSave(resourceAttrProvider.getResource());
        }
    }

    private void doSave(RbacResourceEntity resource) {
        CreateResourceRequest request = new CreateResourceRequest();
        request.setResource(resource);
        RbacServerProxy.get().createResource(request, new AsyncCallback<RpcResult<CreateResourceResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                saveBar.msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<CreateResourceResponse> result) {
                if (result.isSuccess()) {
                    load();
                } else {
                    saveBar.msg(result.getMessage());
                }
            }
        });
    }

    @UiHandler("btnDelete")
    public void btnDeleteClick(ClickEvent event) {
        if (!canDelete()) {
            return;
        }
        DeleteResourceRequest request = new DeleteResourceRequest();
        RbacResourceEntity resourceEntity = selectedItems.get(0);
        request.setResourceCode(resourceEntity.getResourceCode());
        request.setKind(resourceEntity.getKind());
        RbacServerProxy.get().deleteResource(request, new AsyncCallback<RpcResult<DeleteResourceResponse>>() {
            @Override
            public void onFailure(Throwable caught) {
                saveBar.msg(caught.getMessage());
            }

            @Override
            public void onSuccess(RpcResult<DeleteResourceResponse> result) {
                if (result.isSuccess()) {
                    load();
                } else {
                    saveBar.msg(result.getMessage());
                }
            }
        });
    }

    @Override
    public Size requireDefaultSize() {
        int width = Window.getClientWidth();
        int height = Window.getClientHeight();
        return new Size(width - 300, height - 400);
    }

    interface SStyle extends CssResource {
        String catalog();

        String index();
    }

    interface ResourceTreeUiBinder extends UiBinder<DockLayoutPanel, ResourceTree> {
    }
}