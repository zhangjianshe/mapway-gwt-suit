package cn.mapway.ui.client.mvc.tip;


import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * IPageTip
 * 页面提示接口
 * 所有从 CommonEventComposite 继承的组件 都会实现这个接口 这个接口 提供对页面元素的提示功能
 * 子组件只需要提供相应的 数据即可 比如是 Widget ,title,html 这三个元素
 *
 * @author zhang
 */
public interface IPageTip {

    /**
     * 获取组件名称
     *
     * @return
     */
    String getComponentName();

    /**
     * 设置组件名称
     *
     * @param componentName
     * @return
     */
    IPageTip setComponentName(String componentName);

    /**
     * 获取当前tip的版本信息
     *
     * @return
     */
    Integer getTipVersion();

    /**
     * 设置TIpVersion
     *
     * @param tipVersion
     * @return
     */
    IPageTip setTipVersion(int tipVersion);

    /**
     * 添加一个html表示的内容
     *
     * @param target
     * @param title
     * @param html
     * @return
     */
    IPageTip addTipDataHtml(Widget target, String title, String html);

    /**
     * 添加一个Widget表示的内容
     *
     * @param target
     * @param title
     * @param content
     * @return
     */
    IPageTip addTipDataWidget(Widget target, String title, Widget content);

    /**
     * 添加一个Url表示的内容
     *
     * @param target
     * @param title
     * @param url
     * @return
     */
    IPageTip addTipDataUrl(Widget target, String title, String url);

    /**
     * 获取页面Tip列表
     *
     * @return
     */
    List<TipData> getTipDataList();

    /**
     * 显示页面提示
     *
     * @return
     */
    IPageTip showPageTip();

    /**
     * 清空提示信息
     *
     * @return
     */
    IPageTip clearTipData();
}
