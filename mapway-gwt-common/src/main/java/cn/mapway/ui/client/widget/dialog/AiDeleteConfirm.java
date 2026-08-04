package cn.mapway.ui.client.widget.dialog;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.buttons.AiButton;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 确认对话框（卡片式布局）。
 * <p>
 * 白底卡片 → 橙色圆形警告图标 → 标题（可配置）→ 副标题 → 删除/取消按钮。
 *
 * @author zhangjianshe@gmail.com
 */
public class AiDeleteConfirm extends AiDialog {

    private static final AiDeleteConfirmUiBinder ourUiBinder = GWT.create(AiDeleteConfirmUiBinder.class);

    @UiField
    FontIcon icon;

    @UiField
    Label title;

    @UiField
    Label subtitle;

    @UiField
    AiButton btnDelete;

    @UiField
    AiButton btnCancel;

    private Callback<Void, Void> mCallback;

    public AiDeleteConfirm() {
        setWidget(ourUiBinder.createAndBindUi(this));
        icon.setIconUnicode(Fonts.JINGGAOHUANGSE24);
        setGlassEnabled(true);
        setModal(true);
        center();
        getElement().getStyle().setZIndex(10001);

        btnDelete.addDomHandler(e -> {
            if (mCallback != null) {
                mCallback.onSuccess(null);
            }
            hide(true);
        }, ClickEvent.getType());

        btnCancel.addDomHandler(e -> {
            if (mCallback != null) {
                mCallback.onFailure(null);
            }
            hide(true);
        }, ClickEvent.getType());
        setPixelSize(400, 280);
    }

    // ==================== 静态工厂（保持旧 API 兼容） ====================

    public static AiDeleteConfirm confirm(String titleText, ImageResource imageResource,
                                          String content, Callback<Void, Void> callback) {
        return confirm(titleText, content, callback);
    }

    public static AiDeleteConfirm confirm(ImageResource imageResource, String content,
                                          Callback<Void, Void> callback) {
        return confirm("信息确认", content, callback);
    }

    public static AiDeleteConfirm confirm(String titleText, String content,
                                          Callback<Void, Void> callback) {
        AiDeleteConfirm confirm = new AiDeleteConfirm();
        confirm.setInformation(titleText, content, callback);
        confirm.center();
        return confirm;
    }

    public void setInformation(String titleText, String content, Callback<Void, Void> callback) {
        if (titleText != null && !titleText.isEmpty()) {
            title.setText(titleText);
        }
        if (content != null && !content.isEmpty()) {
            subtitle.getElement().setInnerHTML(content);
        }
        mCallback = callback;
    }

    // ==================== UiBinder ====================

    interface AiDeleteConfirmUiBinder extends UiBinder<HTMLPanel, AiDeleteConfirm> {
    }
}
