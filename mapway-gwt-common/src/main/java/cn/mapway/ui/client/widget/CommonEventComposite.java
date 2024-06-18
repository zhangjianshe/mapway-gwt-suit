package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.event.IEventHandler;
import cn.mapway.ui.client.event.ISuccess;
import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.mvc.attribute.AttributeValue;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.IAttributeReadyCallback;
import cn.mapway.ui.client.mvc.attribute.IAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEvent;
import cn.mapway.ui.client.mvc.attribute.event.AttributeStateChangeEventHandler;
import cn.mapway.ui.client.mvc.attribute.marker.IAttributeInit;
import cn.mapway.ui.client.mvc.tip.IPageTip;
import cn.mapway.ui.client.mvc.tip.TipData;
import cn.mapway.ui.client.mvc.tip.TipPanel;
import cn.mapway.ui.client.mvc.window.IEnabled;
import cn.mapway.ui.client.mvc.window.IErrorMessage;
import cn.mapway.ui.client.mvc.window.IProvideSize;
import cn.mapway.ui.client.mvc.window.ISelectable;
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
public class CommonEventComposite extends Composite implements ISelectable, IErrorMessage, IEnabled, HasCommonHandlers, Id, IAttributeInit, IEventHandler, IProvideSize, IAttributesProvider, IPageTip {
    public final static String TRUE = "true";
    private static final String ATTR_TIP = "tip";
    //外部提供一个属性提供器
    IAttributesProvider attributeProvider;
    Set<IAttributeReadyCallback> callbacks;
    Set<String> topics = new HashSet<>();
    List<TipData> tipDataList = new ArrayList<TipData>();
    int tipVersion = 0;
    String componentName = "";
    HandlerRegistration oldHandler;
    private String _id;

    public CommonEventComposite() {
        attributeProvider = null;
        _id = randomId();
        callbacks = new HashSet<>();
    }

    public static void setElementSelect(com.google.gwt.dom.client.Element element, boolean select) {
        if (select) {
            element.setAttribute(ISelectable.SELECT_ATTRIBUTE, TRUE);
        } else {
            element.removeAttribute(ISelectable.SELECT_ATTRIBUTE);
        }
    }

    public static boolean isElementSelect(com.google.gwt.dom.client.Element element) {
        return TRUE.equals(element.getAttribute(ISelectable.SELECT_ATTRIBUTE));
    }

    public boolean isSelected() {
        return isElementSelect(getWidget().getElement());
    }

    @Override
    public void setSelect(boolean select) {
        setElementSelect(getWidget().getElement(), select);
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
        if (oldHandler != null) {
            oldHandler.removeHandler();
            oldHandler = null;
        }
        if (handler == null) {
            return null;
        }
        oldHandler = addHandler(handler, CommonEvent.TYPE);
        return oldHandler;
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
        if (enabled) {
            getWidget().getElement().setAttribute(IEnabled.ENABLED_ATTRIBUTE, "true");
        } else {
            getWidget().getElement().setAttribute(IEnabled.ENABLED_ATTRIBUTE, "false");
        }
    }

    public void setReadonly(boolean readonly) {
        com.google.gwt.dom.client.Element element = getWidget().getElement();
        if (readonly) {
            element.setAttribute(IEnabled.ENABLED_ATTRIBUTE, "false");
            element.getStyle().setProperty("pointerEvents", "auto");
        } else {
            element.removeAttribute(IEnabled.ENABLED_ATTRIBUTE);
            element.getStyle().clearProperty("pointerEvents");
        }
    }

    @Override
    public String getAttributeTitle() {
        if (attributeProvider != null) {
            return attributeProvider.getAttributeTitle();
        }
        return "";
    }

    @Override
    public List<IAttribute> getAttributes() {
        if (attributeProvider != null) {
            return attributeProvider.getAttributes();
        }
        return new ArrayList<>();
    }

    @Override
    public String getAttributeSummary() {
        if (attributeProvider != null) {
            return attributeProvider.getAttributeSummary();
        }
        return "";
    }

    @Override
    public void commit() {
        if (attributeProvider != null) {
            attributeProvider.commit();
        }
        // 都nothing
    }

    @Override
    public List<AttributeValue> flatten() {
        if (attributeProvider != null) {
            return attributeProvider.flatten();
        }
        return new ArrayList<>();
    }

    @Override
    public void addAttributeReadyCallback(IAttributeReadyCallback callback) {
        if (attributeProvider != null) {
            attributeProvider.addAttributeReadyCallback(callback);
            return;
        }
        if (callback == null) {
            return;
        }
        if (!callbacks.contains(callback)) {
            this.callbacks.add(callback);
        }
    }

    @Override
    public void removeAttributeReadyCallback(IAttributeReadyCallback callback) {

        if (attributeProvider != null) {
            attributeProvider.removeAttributeReadyCallback(callback);
            return;
        }

        if (callback == null) {
            return;
        }
        callbacks.remove(callback);
    }

    @Override
    public List<String> isValidate() {
        if (attributeProvider != null) {
            return attributeProvider.isValidate();
        }
        return new ArrayList<>();
    }

    /**
     * 通知属性准备好了
     */
    @Override
    public void notifyAttributeReady() {
        if (attributeProvider != null) {
            attributeProvider.notifyAttributeReady();
            return;
        }

        for (IAttributeReadyCallback callback : callbacks) {
            callback.onAttributeReady(this);
        }
    }

    @Override
    public IAttribute findAttributeByName(String name) {
        if (attributeProvider != null) {
            return null;
        } else {
            return attributeProvider.findAttributeByName(name);
        }
    }

    public void setAttr(String name, String value) {
        getWidget().getElement().setAttribute(name, value);
    }

    @Override
    public String getTitle() {
        return getAttr(ATTR_TIP);
    }

    @Override
    public void setTitle(String title) {
        setAttr(ATTR_TIP, title);
    }

    private String getAttr(String attrTip) {
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

    @Override
    public void setErrorMessage(String message) {
        if (message == null || message.length() == 0) {
            getElement().removeAttribute(UIConstants.ERROR_MSG_KEY);
        } else {
            getElement().setAttribute(UIConstants.ERROR_MSG_KEY, message);
        }
    }

    @Override
    public void initAttributes(IAttributesProvider attributeProvider) {
        this.attributeProvider = attributeProvider;
    }

    @Override
    public void updateAttributeValues(List<AttributeValue> values) {
        if (attributeProvider != null) {
            attributeProvider.updateAttributeValues(values);
        }
        // not implements
    }

    @Override
    public HandlerRegistration addAttributeStateChangeHandler(AttributeStateChangeEventHandler handler) {
        return addHandler(handler, AttributeStateChangeEvent.TYPE);
    }


}
