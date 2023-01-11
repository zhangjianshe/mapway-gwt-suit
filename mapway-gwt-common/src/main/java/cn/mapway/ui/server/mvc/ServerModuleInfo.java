package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.ModuleInfo;

/**
 * ServerModuleInfo
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */

public class ServerModuleInfo extends ModuleInfo {
    String iconString;

    public ServerModuleInfo(String name, String code, String summary, boolean isPublic, String iconString, String hash, boolean visible) {
        super(name, code, summary, isPublic, null, hash, visible);
        this.iconString = iconString;
    }
    public void setTags(String[] tagList) {
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
    public String getIconString() {
        return iconString;
    }

    public void setIconString(String iconString) {
        this.iconString = iconString;
    }
}
