package cn.mapway.ui.client.widget.dialog;

import cn.mapway.ui.client.event.IEventHandler;
import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.tools.IShowMessage;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.buttons.AiButton;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;


/**
 * SaveBar
 * fire close ok two event
 *
 * @author zhangjianshe@gmail.com
 */
public class SaveBar extends CommonEventComposite implements IShowMessage, IData<Object>, IEventHandler {
    private static final SaveBarUiBinder ourUiBinder = GWT.create(SaveBarUiBinder.class);
    @UiField
    AiButton btnSave;
    @UiField
    AiButton btnCancel;
    @UiField
    Label lbMessage;
    @UiField
    HTMLPanel btnPanel;

    Object data;

    String topic = null;

    public SaveBar() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void html(String html) {
        lbMessage.getElement().setInnerHTML(html);
    }

    public void message(String message) {
        lbMessage.setText(message);
        lbMessage.setTitle(message);
    }

    public void setButtonWidth(int width) {
        btnSave.setWidth(width + "px");
        btnCancel.setWidth(width + "px");
    }
    public AiButton addNewButton(String name) {
        if(name==null || "".equals(name)){
            return null;
        }
        List<Widget> children=new ArrayList<Widget>();
        AiButton aiButton=new AiButton();
        aiButton.setText(name);
        children.add(aiButton);
        for(int i=0;i<btnPanel.getWidgetCount();i++){
            children.add(btnPanel.getWidget(i));
        }
        btnPanel.clear();
        for (Widget child : children) {
            btnPanel.add(child);
        }
        return aiButton;
    }

    public void addNewWidget(Widget widget) {
        if(widget==null){
            return;
        }
        List<Widget> children=new ArrayList<Widget>();
        children.add(widget);
        for(int i=0;i<btnPanel.getWidgetCount();i++){
            children.add(btnPanel.getWidget(i));
        }
        btnPanel.clear();
        for (Widget child : children) {
            btnPanel.add(child);
        }
    }

    public void msg(Object message) {
        if (message == null) {
            return;
        }
        if (message instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) message;
            showMessage(messageObject.level, messageObject.code, messageObject.message);
        } else {
            message(message.toString());
        }
    }

    @Override
    public void showMessage(int level, Integer code, String message) {
        message(message);
    }

    public void setEnableSave(Boolean enabled) {
        enableSave(enabled);
    }

    public void enableSave(Boolean enabled) {
        btnSave.setEnabled(enabled);
    }

    public void setEnableCancel(Boolean enabled) {
        btnCancel.setVisible(enabled);
    }

    @UiHandler("btnSave")
    protected void btnSaveClick(ClickEvent event) {
        fireEvent(CommonEvent.okEvent(data));
    }

    @UiHandler("btnCancel")
    protected void btnCancelClick(ClickEvent event) {
        fireEvent(CommonEvent.closeEvent(null));
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    public String getSaveText() {
        return btnSave.getText();
    }

    public void setSaveText(String okText) {
        btnSave.setText(okText);
    }

    public String getCloseText() {
        return btnCancel.getText();
    }

    public void setCloseText(String text) {
        btnCancel.setText(text);
    }

    public void setShowSave(boolean b) {
        btnSave.setVisible(b);
    }

    public void setShowCancel(boolean b) {
        btnCancel.setVisible(b);
    }

    public void setWarning(String warningHtml) {
        msg(warningHtml);
    }

    /**
     * 开始保存消息 如果为 null 或者空字符串 使用系统缺省的消息 BizConstant.MESSAGE_START_SAVE
     *
     * @param saveMessage
     */
    public void startSaveMessage(String saveMessage) {
        if (StringUtil.isBlank(saveMessage)) {
            msg("开始保存");
        } else {
            msg(saveMessage);
        }
    }


    /**
     * 保存成功的消息 如果为 null 或者空字符串 使用系统缺省的消息 BizConstant.MESSAGE_END_SAVE
     *
     * @param
     */
    public void endSaveMessage(String saveMessage) {
        if (StringUtil.isBlank(saveMessage)) {
            msg("已保存");
        } else {
            msg(saveMessage);
        }
    }

    public String enableLoadingMonitor(boolean flag) {
        if (flag) {
            // 开启监听
            topic = this.getId();
            DataBus.get().register(topic, this);
            btnSave.setLoading(true);
        } else {
            // 关闭监听
            DataBus.get().unregister(topic, this);
            topic = null;
            btnSave.setLoading(false);
        }
        return topic;
    }

    public void processSaveStateMessage(CommonEvent event) {
        if (event == null) {
            return;
        }
        if (event.isSaveEnable()) {
            setData(event.getValue());
            enableSave(true);
        } else if (event.isSaveDisable()) {
            setData(event.getValue());
            enableSave(false);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (topic != null) {
            DataBus.get().register(topic, this);
        }
    }

    @Override
    public void onEvent(String topic, int type, Object event) {
        if (topic.equals(this.topic)) {
            // 有消息就解除加载中的状态, 并解除监听
            enableLoadingMonitor(false);
            btnSave.setLoading(false);
        }
    }

    public AiButton getBtnSave() {
        return btnSave;
    }

    interface SaveBarUiBinder extends UiBinder<HTMLPanel, SaveBar> {
    }
}
