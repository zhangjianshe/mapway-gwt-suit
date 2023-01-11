package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.event.IEventHandler;
import cn.mapway.ui.client.event.ISuccess;
import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributeProvider;
import cn.mapway.ui.client.mvc.attribute.IAttributeReadyCallback;
import cn.mapway.ui.client.mvc.decorator.IEnabled;
import cn.mapway.ui.client.mvc.decorator.IProvideSize;
import cn.mapway.ui.client.mvc.decorator.ISelectable;
import cn.mapway.ui.client.mvc.tip.IPageTip;
import cn.mapway.ui.client.mvc.tip.TipData;
import cn.mapway.ui.client.mvc.tip.TipPanel;
import cn.mapway.ui.client.tools.DataBus;
import cn.mapway.ui.client.tools.Id;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import cn.mapway.ui.shared.rpc.RpcResult;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CommonEventComposite
 * 这是一个组件 不是容器面板panel
 * 组件一定是放在一个容器中的 这个组件也可以通过装饰器来装饰
 *
 * @author zhangjianshe@gmail.com
 */
public class CommonEventComposite extends Composite implements ISelectable, IEnabled, HasCommonHandlers, Id, IEventHandler, IProvideSize, IAttributeProvider, IPageTip {
    private static final String ATTR_TIP = "tip";
    HandlerRegistration commonHandler = null;
    Set<IAttributeReadyCallback> callbacks;
    Set<String> topics = new HashSet<>();
    List<TipData> tipDataList = new ArrayList<TipData>();
    int tipVersion = 0;
    String componentName = "";
    private String _id;

    public CommonEventComposite() {
        _id = randomId();
        callbacks = new HashSet<>();
    }

    @Override
    public void setSelect(boolean select) {
        setElementSelect(getWidget().getElement(), select);
    }

    public static void setElementSelect(com.google.gwt.dom.client.Element element, boolean select) {
        if (select) {
            element.setAttribute(ISelectable.SELECT_ATTRIBUTE, "true");
        } else {
            element.removeAttribute(ISelectable.SELECT_ATTRIBUTE);
        }
    }

    private String randomId() {
        Double d = Random.nextDouble();
        String s = StringUtil.formatFloat(d.floatValue(), 8);
        return s;
    }

    /**
     * 向总线注册一个topic事件
     * 内部保留一个list 维护订阅的事件列表
     * 重复订阅只保留一个订阅实例
     *
     * @param topic
     */
    public void registerBusEvent(String topic) {
        if (topic == null || topic.trim().length() == 0) {
            Logs.info("没有设定topic");
            return;
        }
        for (String t : topics) {
            if (t.equals(topic)) {
                Logs.info("已订阅topic" + t);
                return;
            }
        }
        DataBus.get().register(topic, this);
        topics.add(topic);
    }

    /**
     * 取消对topic的事件注册
     *
     * @param topic
     */
    public void unregisterBusEvent(String topic) {
        if (topic == null || topic.trim().length() == 0) {
            Logs.info("cancel 没有设定topic");
            return;
        }
        if (topics.remove(topic)) {
            DataBus.get().unregister(topic, this);
        }
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        for (String topic : topics) {
            DataBus.get().register(topic, this);
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        for (String topic : topics) {
            DataBus.get().unregister(topic, this);
        }
    }

    /**
     * 清空所有的事件列表
     */
    public void clearBusEvent() {
        for (String topic : topics) {
            DataBus.get().unregister(topic, this);
        }
        topics.clear();
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler handler) {
        if (commonHandler != null) {
            commonHandler.removeHandler();
        }
        commonHandler = addHandler(handler, CommonEvent.TYPE);
        return commonHandler;
    }

    /**
     * 发送消息记录
     *
     * @param message
     */
    public void fireMessage(MessageObject message) {
        CommonEvent ev = CommonEvent.messageEvent(message);
        fireEvent(ev);
    }

    public void processServiceCode(RpcResult result) {
        if (result != null && result.getCode().equals(300001)) {
            throw new RuntimeException("300001");
        }
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public void setId(String id) {
        this._id = id;
    }

    @Override
    public void onEvent(String topic, int type, Object event) {

    }

    /**
     * 缺省不提供尺寸
     *
     * @return
     */
    @Override
    public Size requireDefaultSize() {
        return null;
    }

    @Override
    public boolean getEnabled() {
        String attribute = getWidget().getElement().getAttribute(IEnabled.ENABLED_ATTRIBUTE);
        return !"false".equals(attribute);
    }

    @Override
    public void setEnabled(boolean enabled) {
        com.google.gwt.dom.client.Element element = getWidget().getElement();
        if (enabled) {
            element.setAttribute(IEnabled.ENABLED_ATTRIBUTE, "true");
            element.getStyle().clearProperty("pointerEvents");
        } else {
            element.setAttribute(IEnabled.ENABLED_ATTRIBUTE, "false");
            element.getStyle().setProperty("pointerEvents", "auto");
        }
    }

    @Override
    public String getAttributeTitle() {
        return "";
    }

    @Override
    public List<IAttribute> getAttributes() {
        return new ArrayList<>();
    }

    @Override
    public String getAttributeSummary() {
        return "";
    }

    @Override
    public void commit() {
        // 都nothing
    }

    @Override
    public List<AttributeValue> flatten() {
        return new ArrayList<>();
    }

    @Override
    public void addAttributeReadyCallback(IAttributeReadyCallback callback) {
        if (callback == null) {
            return;
        }
        if (!callbacks.contains(callback)) {
            this.callbacks.add(callback);
        }
    }

    @Override
    public void removeAttributeReadyCallback(IAttributeReadyCallback callback) {
        if (callback == null) {
            return;
        }
        callbacks.remove(callback);
    }

    @Override
    public List<String> isValidate() {
        return new ArrayList<>();
    }

    /**
     * 通知属性准备好了
     */
    @Override
    public void notifyAttributeReady() {
        for (IAttributeReadyCallback callback : callbacks) {
            callback.onAttributeReady(this);
        }
    }

    public void setAttribute(String name, String value) {
        getWidget().getElement().setAttribute(name, value);
    }

    @Override
    public String getTitle() {
        return getAttribute(ATTR_TIP);
    }

    @Override
    public void setTitle(String title) {
        setAttribute(ATTR_TIP, title);
    }

    private String getAttribute(String attrTip) {
        return getWidget().getElement().getAttribute(attrTip);
    }

    @Override
    public IPageTip setTipVersion(int tipVersion) {
        this.tipVersion = tipVersion;
        return this;
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    @Override
    public IPageTip setComponentName(String componentName) {
        this.componentName = componentName;
        return this;
    }

    @Override
    public Integer getTipVersion() {
        return tipVersion;
    }

    @Override
    public IPageTip addTipDataHtml(Widget target, String title, String html) {
        TipData tipData = new TipData(target, title);
        tipData.setHtml(html);
        tipDataList.add(tipData);
        return this;
    }

    @Override
    public IPageTip addTipDataWidget(Widget target, String title, Widget content) {
        TipData tipData = new TipData(target, title);
        tipData.setWidget(content);
        tipDataList.add(tipData);
        return this;
    }

    @Override
    public IPageTip addTipDataUrl(Widget target, String title, String url) {
        TipData tipData = new TipData(target, title);
        tipData.setUrl(url);
        tipDataList.add(tipData);
        return this;
    }

    @Override
    public List<TipData> getTipDataList() {
        return tipDataList;
    }

    @Override
    public IPageTip showPageTip() {
        TipPanel tipPanel = TipPanel.get();
        tipPanel.setData(tipDataList);
        tipPanel.show();
        return this;
    }

    @Override
    public IPageTip clearTipData() {
        tipDataList.clear();
        return this;
    }

    /**
     * 简化数据处理逻辑
     *
     * @param data
     * @param successHandler
     * @param <T>
     */
    public <T> void processResult(RpcResult<T> data, ISuccess<T> successHandler) {
        //是否需要重新登录
        processServiceCode(data);
        //执行操作是否成功
        if (!data.isSuccess()) {
            fireMessage(MessageObject.error(data.getCode(), data.getMessage()));
            return;
        }
        if (successHandler != null) {
            successHandler.onSuccess(data.getData());
        } else {
            fireMessage(MessageObject.info(200, "操作成功"));
        }
    }
}
