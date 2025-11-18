package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.ModuleInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * ServerModuleInfo
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */

@Setter
@Getter
public class ServerModuleInfo extends ModuleInfo {
    String iconString;

    public ServerModuleInfo() {
        super();
    }

    public ServerModuleInfo(String name, String code, String summary, boolean isPublic, String iconString, String hash, boolean visible) {
        super(name, code, summary, isPublic, null, hash, visible);
        this.iconString = iconString;
    }

    public void setTags(String[] tagList) {
        if (tags == null) {
            tags = new ArrayList<String>();
        }
        tags.clear();
        if (tagList != null) {
            for (String tag : tagList) {
                tag = tag.trim();
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            }
        }
    }

    public void setThemes(String[] themeList) {
        if (themes == null) {
            themes = new ArrayList<>();
        }
        themes.clear();
        if (themeList != null) {
            for (String theme : themeList) {
                theme = theme.trim();
                if (!theme.isEmpty()) {
                    themes.add(theme);
                }
            }
        }
    }

}
