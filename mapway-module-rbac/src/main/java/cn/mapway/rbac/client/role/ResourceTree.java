package cn.mapway.rbac.client.role;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.rbac.shared.rpc.*;
import cn.mapway.ui.client.event.AsyncCallbackLambda;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.editor.inspector.ObjectInspector;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceTree extends CommonEventComposite {
    private static final ResourceTreeUiBinder ourUiBinder = GWT.create(ResourceTreeUiBinder.class);
    private static Dialog<ResourceTree> dialog;
    @UiField
    ZTree resourceTree;
    @UiField
    Button btnCreate;
    @UiField
    SaveBar saveBar;
    @UiField
    ObjectInspector objectInspector;
    @UiField
    Button btnDelete;
    Map<String, ImageTextItem> pathNodeMapper = new HashMap<>();
    RbacResourceEntity selectedResource;
    ResourceAttrProvider resourceAttrProvider;

    public ResourceTree() {
        initWidget(ourUiBinder.createAndBindUi(this));
        resourceAttrProvider = new ResourceAttrProvider();
        objectInspector.setData(resourceAttrProvider);
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
                    renderTree(result.getData());

                } else {
                    DataBus.get().message(result.getMessage());
                }
            }
        });
    }

    private void renderTree(QueryResourceResponse data) {
        resourceTree.clear();
        pathNodeMapper.clear();
        for (RbacResourceEntity resource : data.getResources()) {
            ImageTextItem pathItem = surePathItem(resource.getCatalog());
            ImageTextItem item = resourceTree.addFontIconItem(pathItem, resource.getName(), Fonts.RBAC_RESOURCE);
            Label lbCode=new Label("["+resource.getResourceCode()+"]");
            item.appendWidget(lbCode,null);
            item.setData(resource);
        }
    }

    private ImageTextItem surePathItem(String catalog) {
        if (catalog == null) {
            return null;
        }
        catalog = catalog.trim();
        if (StringUtil.isBlank(catalog)) {
            return null;
        }

        if (pathNodeMapper.containsKey(catalog)) {
            return pathNodeMapper.get(catalog);
        }

        List<String> list = StringUtil.splitIgnoreBlank(catalog, "/");
        String path = "";
        ImageTextItem parent = null;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                path = list.get(i);
            } else {
                path = path + "/" + list.get(i);
            }

            if (pathNodeMapper.containsKey(path)) {
                parent = pathNodeMapper.get(path);
                continue;
            } else {
                ImageTextItem item = resourceTree.addItem(parent, list.get(i), null);
                item.setData(null);
                pathNodeMapper.put(path, item);
                parent = item;
            }
        }
        return parent;
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

    @UiHandler("resourceTree")
    public void resourceTreeCommon(CommonEvent event) {
        if (event.isSelect()) {
            ImageTextItem item = event.getValue();
            selectedResource = (RbacResourceEntity) item.getData();
            resourceAttrProvider.rebuild(selectedResource);
            if (ResourceKind.fromCode(selectedResource.getKind()) == ResourceKind.RESOURCE_KIND_CUSTOM) {
                objectInspector.setSaveButtonEnable(true);
                objectInspector.setSaveButtonVisible(true);
            } else {
                objectInspector.setSaveButtonEnable(false);
                objectInspector.setSaveButtonVisible(false);
            }
        }
        updateUI();
    }

    private void updateUI() {
        saveBar.enableSave(selectedResource != null);
        btnDelete.setEnabled(canDelete());
    }

    private boolean canDelete() {
        if (selectedResource == null) {
            return false;
        }
       return true;
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            fireEvent(CommonEvent.okEvent(selectedResource));
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
        DeleteResourceRequest request = new DeleteResourceRequest();
        request.setResourceCode(selectedResource.getResourceCode());
        request.setKind(selectedResource.getKind());
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
        return new Size(900, 700);
    }

    interface ResourceTreeUiBinder extends UiBinder<DockLayoutPanel, ResourceTree> {
    }
}