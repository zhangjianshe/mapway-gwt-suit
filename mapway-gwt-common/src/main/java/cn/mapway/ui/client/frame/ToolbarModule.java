package cn.mapway.ui.client.frame;

import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.tools.IShowMessage;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.Loading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * ToobarModule
 * 此类的子类可以拥有一个工具栏
 *
 * @author zhangjianshe@gmail.com
 */
public abstract class ToolbarModule extends BaseAbstractModule implements IShowMessage, RequiresResize, ProvidesResize {
    private static final ToolbarModuleUiBinder ourUiBinder = GWT.create(ToolbarModuleUiBinder.class);

    ToolbarUiHolder uiHolder;

    public ToolbarModule() {
    }

    /**
     * 重载初始化组件
     *
     * @param widget
     */
    @Override
    protected void initWidget(Widget widget) {
        uiHolder = new ToolbarUiHolder();
        super.initWidget(ourUiBinder.createAndBindUi(uiHolder));
        uiHolder.root.add(widget);
        uiHolder.lbTitle.setText(getModuleInfo().name);
        setIcon(getModuleInfo().unicode, getModuleInfo().icon);
        setStyleName("ai-top-bar");
    }

    private void setIcon(String unicode, ImageResource icon) {
        if (unicode == null || unicode.isEmpty()) {
            uiHolder.fontIcon.setVisible(false);
            uiHolder.icon.setVisible(true);
            uiHolder.icon.setResource(icon);
        } else {
            uiHolder.icon.setVisible(false);
            uiHolder.fontIcon.setVisible(true);
            uiHolder.fontIcon.setIconUnicode(unicode);
        }
    }

    @Override
    public void setStyleName(String style) {
        uiHolder.panel.setStyleName(style);
    }

    public void setCaption(String caption) {
        if(!StringUtil.isBlank(caption)){
            uiHolder.msgBar.setVisible(true);
            uiHolder.leftToolBar.setVisible(false);
        }
        uiHolder.lbTitle.setText(caption);
    }

    /**
     * 清空操作面板区
     */
    public void clearOperations() {
        uiHolder.operations.clear();
    }

    /**
     * 添加操作面板
     *
     * @param widgets
     */
    public void appendOperation(Widget... widgets) {
        if (widgets != null) {
            for (Widget w : widgets) {
                uiHolder.operations.add(w);
            }
        }
    }

    @Override
    public void onResize() {
        uiHolder.root.onResize();
    }

    @Override
    public void showMessage(int level, Integer code, String message) {
        fireMessage(new MessageObject(level, code, message));
    }

    @Override
    public boolean updateTools(Widget... tools) {
        uiHolder.tools.clear();
        for (Widget w : tools) {
            uiHolder.tools.add(w);
        }
        return true;
    }


    public boolean updateLeftToolBar(Widget... tools) {
        uiHolder.leftToolBar.clear();
        if(tools != null){
            uiHolder.msgBar.setVisible(false);
            uiHolder.leftToolBar.setVisible(true);
        }
        for (Widget w : tools) {
            uiHolder.leftToolBar.add(w);
        }
        return true;
    }


    @Override
    public boolean appendTools(Widget tools) {
        uiHolder.tools.add(tools);
        return true;
    }

    public void msg(String message) {
        uiHolder.lbMessage.setText(StringUtil.brief(message, 10));
    }

    public void enableLoading(Boolean show) {
        uiHolder.loading.setVisible(show);
    }

    interface ToolbarModuleUiBinder extends UiBinder<DockLayoutPanel, ToolbarUiHolder> {
    }

    static class ToolbarUiHolder {

        @UiField
        HorizontalPanel tools;
        @UiField
        HorizontalPanel bar;
        @UiField
        DockLayoutPanel root;
        @UiField
        Image icon;
        @UiField
        Label lbMessage;
        @UiField
        Label lbTitle;
        @UiField
        HTMLPanel panel;
        @UiField
        Loading loading;
        @UiField
        HTMLPanel operations;
        @UiField
        FontIcon fontIcon;
        @UiField
        HorizontalPanel leftToolBar;
        @UiField
        HorizontalPanel msgBar;
    }
}
