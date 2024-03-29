package cn.mapway.ui.client.mvc;


import com.google.gwt.resources.client.ImageResource;

import java.util.ArrayList;
import java.util.List;

/**
 * 模块对象.
 *
 * @author zhangjianshe
 */
public class ModuleInfo {

    /**
     * 排列顺序
     */
    public int order;
    /**
     * The name.
     */
    public String name;

    /**
     * The code.
     */
    public String code;

    /**
     * 自定义图标标识码 &#x [6323]
     * 不包括前缀&#x
     */
    public String unicode;

    /**
     * sumamry.
     */
    public String summary;
    /**
     * 是否是公共模块
     */
    public boolean isPublic;
    /**
     * module icon data base64
     */
    public ImageResource icon;

    /**
     * 是否单例创建.
     */
    public Boolean single;

    /**
     * 模块Hash值
     */
    public String hash;

    /**
     * The Is visible.
     */
    public boolean isVisible;

    /**
     * 模块分组信息
     */
    public String group;

    /**
     * 模块标签
     */
    public List<String> tags;
    /**
     * 模块标签
     */
    public List<String> themes;
    /**
     * 模块的父模块名称
     */
    public String parent;

    public List<ModuleInfo> children;

    /**
     * Instantiates a new module item.
     *
     * @param name         the name
     * @param code         the code
     * @param summary      the summary
     * @param isPublic     the is public
     * @param iconResource the icon
     * @param hash         the hash
     * @param visible      the visible
     */
    public ModuleInfo(String name, String code, String summary, boolean isPublic, ImageResource iconResource,
                      String hash, boolean visible) {
        this.name = name;
        this.code = code;
        this.isPublic = isPublic;
        this.icon = iconResource;
        this.summary = summary;
        this.hash = hash;
        this.isVisible = visible;
        this.tags = new ArrayList<>();
        this.themes = new ArrayList<>();
        children = new ArrayList<>();
    }

    /**
     * Instantiates a new Module info.
     *
     * @param name         the name
     * @param code         the code
     * @param summary      the summary
     * @param isPublic     the is public
     * @param iconResource the icon
     * @param hash         the hash
     * @param visible      the visible
     * @param group        the group
     * @param unicode      the unicode
     */
    public ModuleInfo(String name, String code, String summary, boolean isPublic, ImageResource iconResource,
                      String hash, boolean visible, String group, String unicode) {
        this(name, code, summary, isPublic, iconResource, hash, visible);
        this.group = group;
        this.unicode = unicode;
    }

    /**
     * Copy module info.
     *
     * @return the module info
     */
    public ModuleInfo copy() {
        ModuleInfo n = new ModuleInfo(name, code, summary, isPublic, icon, hash, isVisible);
        if (themes != null) {
            for (String theme : themes) {
                n.addTheme(theme);
            }
        }
        if (tags != null) {
            for (String tag : tags) {
                n.addTag(tag);
            }
        }
        return n;
    }

    private void addTag(String tag) {
        if (tag == null || tag.length() == 0) {
            return;
        }
        if (tags == null) {
            tags = new ArrayList();
        }
        tags.add(tag);
    }

    public void addTheme(String theme) {
        if (theme == null || theme.length() == 0) {
            return;
        }
        if (themes == null) {
            themes = new ArrayList();
        }
        themes.add(theme);
    }

    /**
     * 设置单例模式.
     *
     * @param single the single
     * @return single
     */
    public ModuleInfo setSingle(boolean single) {
        this.single = single;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int value) {
        order = order;
    }

    public boolean forTheme(String themeName) {
        if (themes == null || themeName == null || themeName.length() == 0) {
            return true;
        }

        for (String theme : themes) {
            if (themeName.equals(theme)) {
                return true;
            }
        }
        return themes.size() == 0;
    }

    /**
     * 模块是否有 TAG标签
     *
     * @param tag
     * @return
     */
    public boolean hasTag(String tag) {
        if (tags == null || tag == null || tag.length() == 0) {
            return false;
        }
        for (String t : tags) {
            if (tag.equals(t)) {
                return true;
            }
        }
        return false;
    }
}
