package cn.mapway.ui.client.widget.dialog;

import cn.mapway.ui.client.mvc.ISaveble;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.window.IProvideSize;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 保存对话框
 *
 * @param <T>
 */
public class SaveDialog<T extends Widget> extends AiDialog {

    DockLayoutPanel rootPanel;
    SaveBar saveBar;
    private final CommonEventHandler commonHandler = (event) -> {
        if (event.isTitle()) {
            this.setText(event.getValue());
        } else if (event.isMessage()) {
            saveBar.msg(event.getValue());
        } else {
            if (event.isSaveEnable()) {
                saveBar.setData(event.getValue());
                saveBar.enableSave(true);
            } else if (event.isSaveDisable()) {
                saveBar.setData(event.getValue());
                saveBar.enableSave(false);
            }
            this.fireEvent(event);
        }
    };
    T content;
    private final CommonEventHandler saveBarHandler = new CommonEventHandler() {
        @Override
        public void onCommonEvent(CommonEvent commonEvent) {
            if (commonEvent.isOk()) {
                ISaveble saveble = (ISaveble) content;
                saveble.save();
            } else {
                fireEvent(commonEvent);
            }
        }
    };

    public SaveDialog(T content, String title) {
        super();
        rootPanel = new DockLayoutPanel(Style.Unit.PX);
        saveBar = new SaveBar();
        rootPanel.addSouth(saveBar, 68);
        this.setText(title);
        this.setGlassEnabled(true);
        this.setModal(true);
        this.content = content;
        this.setWidget(rootPanel);
        rootPanel.add(content);
        saveBar.addCommonHandler(saveBarHandler);
        if (content instanceof HasCommonHandlers) {
            ((HasCommonHandlers) content).addCommonHandler(this.commonHandler);
        }
        saveBar.setEnableSave(content instanceof ISaveble);
        if (content instanceof IProvideSize) {
            IProvideSize w2 = (IProvideSize) content;
            Size size = w2.requireDefaultSize();
            if (size != null) {
                this.setPixelSize(size.getXAsInt(), size.getYAsInt());
            } else {
                this.setPixelSize(900, 500);
            }
        } else {
            this.setPixelSize(900, 500);
        }
    }

    /**
     * 处理保存状态消息　更改SaveBar save 按钮状态
     *
     * @param event
     */
    public void processSaveStateMessage(CommonEvent event) {
        saveBar.processSaveStateMessage(event);
    }

    public SaveDialog<T> setSaveText(String text) {
        saveBar.setSaveText(text);
        return this;
    }

    public T getContent() {
        return this.content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
