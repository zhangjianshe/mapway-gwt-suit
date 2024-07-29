package cn.mapway.ui.client.mvc.attribute.editor;

/**
 * 属性编辑器的信息
 */
public class AttributeEditorInfo {
    /**
     * 属性代码
     */
    public String code;
    /**
     * 属性名称
     */
    public String name;

    /**
     * 属性分组信息
     */
    public String group;
    /**
     * 简要说明
     */
    public String summary;

    /**
     * 作者
     */
    public String author;

    public String icon;
    public int rank;



    public AttributeEditorInfo(String code, String name, String group) {
        this.code = code;
        this.name = name;
        this.group = group;
    }

    public AttributeEditorInfo setAuthor(String author) {
        this.author = author;
        return this;
    }

    public AttributeEditorInfo setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public AttributeEditorInfo setRank(int rank) {
        this.rank = rank;
        return this;
    }

    public AttributeEditorInfo setIcon(String icon) {
        this.icon = icon;
        return this;
    }

}
