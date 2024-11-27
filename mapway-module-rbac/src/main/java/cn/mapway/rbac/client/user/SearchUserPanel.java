package cn.mapway.rbac.client.user;

import cn.mapway.rbac.client.RbacServerProxy;
import cn.mapway.rbac.shared.db.postgis.RbacUserEntity;
import cn.mapway.rbac.shared.rpc.QueryUserRequest;
import cn.mapway.rbac.shared.rpc.QueryUserResponse;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Dialog;
import cn.mapway.ui.client.widget.dialog.SaveBar;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchUserPanel extends CommonEventComposite {
    private static final SearchUserPanelUiBinder ourUiBinder = GWT.create(SearchUserPanelUiBinder.class);
    private static Dialog<SearchUserPanel> dialog;
    @UiField
    TextBox txtSearch;
    @UiField
    VerticalPanel list;
    @UiField
    SaveBar saveBar;
    ImageTextItem selectedItem = null;
    private final ClickHandler itemSelected = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            ImageTextItem label = (ImageTextItem) event.getSource();
            selectItem(label, true);
        }
    };

    public SearchUserPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        txtSearch.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                search(txtSearch.getValue());
            }
        });
    }

    public static Dialog<SearchUserPanel> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }

    private static Dialog<SearchUserPanel> createOne() {
        SearchUserPanel panel = new SearchUserPanel();
        return new Dialog<>(panel, "选择用户");
    }

    private void selectItem(ImageTextItem label, boolean b) {
        if (selectedItem != null) {
            selectedItem.setSelect(false);
        }
        selectedItem = label;
        if (selectedItem != null) {
            selectedItem.setSelect(true);

        }
        updateUI();
    }

    private void updateUI() {
        saveBar.enableSave(selectedItem != null);
    }

    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isOk()) {
            fireEvent(CommonEvent.okEvent(selectedItem.getData()));
        } else {
            fireEvent(event);
        }
    }

    public void search(String value) {
        QueryUserRequest request = new QueryUserRequest();
        request.setSearchText(value);

        RbacServerProxy.get().queryUser(request, new AsyncCallback<RpcResult<QueryUserResponse>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(RpcResult<QueryUserResponse> result) {
                if (result.isSuccess()) {
                    renderResponse(result.getData());
                }
            }
        });
    }

    private void renderResponse(QueryUserResponse data) {

        list.clear();
        for (RbacUserEntity user : data.getUsers()) {
            ImageTextItem item = new ImageTextItem();
            item.setText(user.getUserName());
            item.setData(user);
            item.addDomHandler(itemSelected, ClickEvent.getType());
            list.add(item);
        }
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(500, 700);
    }

    interface SearchUserPanelUiBinder extends UiBinder<DockLayoutPanel, SearchUserPanel> {
    }


}