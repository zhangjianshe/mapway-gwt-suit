package cn.mapway.echart.client;

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * TooltipParameter
 *
 * @author zhang
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class TooltipParameter {
    public String componentType;//: 'series',
    // 系列类型
    public String seriesType;
    // 系列在传入的 option.series 中的 index
    public int seriesIndex;
    // 系列名称
    public String seriesName;
    // 数据名，类目名
    public String name;
    // 数据在传入的 data 数组中的 index
    public int dataIndex;
    // 传入的原始数据项
    public JsObject data;
    // 传入的数据值。在多数系列下它和 data 相同。在一些系列下是 data 中的分量（如 map、radar 中）
    //number|Array|Object
    public JsObject value;//: number|Array|Object,
    // 坐标轴 encode 映射信息，
    // key 为坐标轴（如 'x' 'y' 'radius' 'angle' 等）
    // value 必然为数组，不会为 null/undefied，表示 dimension index 。
    // 其内容如：
    // {
    //     x: [2] // dimension index 为 2 的数据映射到 x 轴
    //     y: [0] // dimension index 为 0 的数据映射到 y 轴
    // }
    public JsObject encode;
    // 维度名列表
    public JsArray<String> dimensionNames;
    // 数据的维度 index，如 0 或 1 或 2 ...
    // 仅在雷达图中使用。
    public int dimensionIndex;
    // 数据图形的颜色
    public String color;
    // 饼图，漏斗图的百分比
    public float percent;
}
