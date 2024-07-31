package cn.mapway.ui.client.mvc.attribute;

import java.util.List;

/**
 * 数据源感知接口
 */
public interface IDataSourceAware {

    /**
     * 设置数据源列表
     *
     * @param widgetIdList
     */
    void setDataSourceList(List<String> widgetIdList);
}
