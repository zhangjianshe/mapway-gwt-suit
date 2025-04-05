package cn.mapway.rbac.shared.model;

import cn.mapway.document.annotation.ApiField;
import jsinterop.annotations.JsType;

/**
 * 资源
 * 每个角色设定多个资源点
 * 加上这一级　***纯属多余***
 */
@JsType
public class Res {
    @ApiField(value = "资源CODE", example = "1")
    public String resourceCode;
    @ApiField(value = "资源名称", example = "1")
    public String name;
    @ApiField(value = "资源类型", example = "1")
    public Integer kind;
    @ApiField(value = "资源数据", example = "1")
    public String data;
    @ApiField(value = "资源描述", example = "1")
    public String summary;
    @ApiField(value = "资源分类", example = "1")
    public String catalog;
}
