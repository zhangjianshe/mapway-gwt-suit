package cn.mapway.rbac.client.role;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.editor.inspector.ObjectEditor;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.client.widget.SearchBox;
import cn.mapway.ui.client.widget.buttons.AiButton;
import cn.mapway.ui.client.widget.buttons.CheckBoxEx;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
    Button btnDelete;
    @UiField
    HTMLPanel indexPanel;
    @UiField
    FlexTable table;
    ResourceAttrProvider resourceAttrProvider;
    Map<String, List<CheckBoxEx>> checkGroups = new HashMap<>();
    Label selectLabel = null;
    private final ClickHandler scrollToGroup = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            Label source = (Label) event.getSource();
            if (selectLabel != null) {
                CommonEventComposite.setElementSelect(selectLabel.getElement(), false);
            }
            selectLabel = source;
            if (selectLabel != null) {
                CommonEventComposite.setElementSelect(selectLabel.getElement(), true);
            }
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
    private final ClickHandler selectAll = event -> {
        IData source = (IData) event.getSource();
        String catalogName = (String) source.getData();
        List<CheckBoxEx> checkBoxExes = checkGroups.get(catalogName);
        if (checkBoxExes != null) {
            for (CheckBoxEx checkBoxEx : checkBoxExes) {
                checkBoxEx.setValue(true, false);
            }
        }
        updateSelect();
    };
    private final ClickHandler reverSelect = event -> {
        IData source = (IData) event.getSource();
        String catalogName = (String) source.getData();
        List<CheckBoxEx> checkBoxExes = checkGroups.get(catalogName);
        if (checkBoxExes != null) {
            for (CheckBoxEx checkBoxEx : checkBoxExes) {
                checkBoxEx.setValue(!checkBoxEx.getValue(), false);
            }
        }
        updateSelect();
    };
    @UiField
    SStyle style;
    @UiField
    SearchBox searchBox;

    public ResourceTree() {
        initWidget(ourUiBinder.createAndBindUi(this));
        resourceAttrProvider = new ResourceAttrProvider();
        btnDelete.setEnabled(false);
        searchBox.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String name = event.getValue();
                filterName(name);
            }
        });
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

    private void filterName(String name) {
        HTMLTable.RowFormatter rowFormatter = table.getRowFormatter();
        if (name == null || name.length() == 0) {
            for (int i = 0; i < table.getRowCount(); i++) {
                rowFormatter.setVisible(i, true);
            }
        } else {
            for (int row = 0; row < table.getRowCount(); row++) {
                Widget widget = table.getWidget(row, 0);
                if (widget instanceof CheckBoxEx) {
                    String catalog = table.getText(row, 1);
                    if (catalog.contains(name)) {
                        rowFormatter.setVisible(row, true);
                        continue;
                    }
                    if (table.getText(row, 2).contains(name)) {
                        rowFormatter.setVisible(row, true);
                        continue;
                    }
                    if (table.getText(row, 3).contains(name)) {
                        rowFormatter.setVisible(row, true);
                        continue;
                    }
                    rowFormatter.setVisible(row, false);
                } else {
                    rowFormatter.setVisible(row, false);
                }
            }
        }
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
        HTMLTable.RowFormatter rowFormatter = table.getRowFormatter();
        int row = -1;
        int col = 0;
        for (Integer kind : catalogs.keySet()) {

            //分类行
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
            cellFormatter.setColSpan(row, col - 1, 5);
            com.google.gwt.user.client.Element element = rowFormatter.getElement(row);
            element.getStyle().setBackgroundColor("skyblue");

            List<CheckBoxEx> checkBoxExes = checkGroups.get(resourceKind.getName());
            if (checkBoxExes == null) {
                checkBoxExes = new ArrayList<>();
                checkGroups.put(resourceKind.getName(), checkBoxExes);
            }

            //内容行
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
                cellFormatter.setWidth(row, col - 1, "32px");
                table.setText(row, col++, resource.getCatalog());
                table.setText(row, col++, resource.getName());
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
        Dialog<ObjectEditor> dialog1 = ObjectEditor.getDialog(true);
        dialog1.addCommonHandler(new CommonEventHandler() {
            @Override
            public void onCommonEvent(CommonEvent event) {
                if (event.isSave()) {
                    doSave(resourceAttrProvider.getResource());
                } else if (event.isClose()) {
                    dialog1.hide();
                }
            }
        });
        dialog1.getContent().setColumns(2);
        dialog1.getContent().setSaveBarVisible(true);
        dialog1.getContent().setEnableGroup(false);
        dialog1.getContent().setData(resourceAttrProvider);
        dialog1.setPixelSize(800, 450);
        dialog1.center();
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
        Size pt = new Size(0, 0);
        pt.x = Math.max(Window.getClientWidth() - 250, 850);
        pt.y = Math.max(Window.getClientHeight() - 150, 450);
        return pt;
    }

    interface SStyle extends CssResource {
        String catalog();

        String index();
    }

    interface ResourceTreeUiBinder extends UiBinder<DockLayoutPanel, ResourceTree> {
    }
}