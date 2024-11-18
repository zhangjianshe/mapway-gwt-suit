package cn.mapway.ui.client.widget.dialog;


import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 *
 */
public class AiConfirm extends AiDialog {
    private static final AiConfirmUiBinder ourUiBinder = GWT.create(AiConfirmUiBinder.class);
    @UiField
    Image icon;
    @UiField
    HTML html;
    @UiField
    SaveBar saveBar;
    private Callback mCallback;

    public AiConfirm() {
        super();
        this.add(ourUiBinder.createAndBindUi(this));
        this.setGlassEnabled(true);
        this.setModal(true);
        setPixelSize(600, 350);
        //确认对话框最上层
        this.getElement().getStyle().setZIndex(10001);
    }

    /**
     * confirm information
     *
     * @param title
     * @param content
     * @param callback
     */
    public static AiConfirm confirm(String title, ImageResource imageResource, String content, Callback callback) {
        AiConfirm confirm = new AiConfirm();
        confirm.setInformation(title, imageResource, content, callback);
        confirm.center();
        return confirm;
    }

    /**
     * confirm information
     *
     * @param content
     * @param callback
     */
    public static AiConfirm confirm(ImageResource imageResource, String content, Callback callback) {
        return confirm("信息确认", imageResource, content, callback);
    }

    /**
     * 是否显示 保存按钮
     *
     * @param enabled
     * @return
     */
    public AiConfirm enableConfirmButton(boolean enabled) {
        saveBar.setShowSave(enabled);
        return this;
    }

    public void setInformation(String title, ImageResource imageResource, String htmlContent, Callback callback) {
        if (title == null || title.length() == 0) {
            setText(title);
        } else {
            setText("信息");
        }

        if (imageResource != null) {
            icon.setResource(imageResource);
            icon.setPixelSize(80, 80);
        } else {
            icon.setVisible(false);
        }
        if (htmlContent != null) {
            html.setHTML(htmlContent);
        }
        mCallback = callback;
    }


    @UiHandler("saveBar")
    public void saveBarCommon(CommonEvent event) {
        if (event.isClose()) {
            if (mCallback != null) {
                mCallback.onFailure(getData());
            }
            this.hide(true);
        } else if (event.isOk()) {
            if (mCallback != null) {
                mCallback.onSuccess(getData());
            }
            this.hide(true);
        }
    }


    interface AiConfirmUiBinder extends UiBinder<DockLayoutPanel, AiConfirm> {
    }
}
