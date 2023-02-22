package cn.mapway.common.geo.sfile;

import cn.mapway.geo.shared.vector.Box;
import lombok.Data;

/**
 * SFileInfo
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class SLevelInfo {
    int level = 0;
    long tileCount = 0;
    Box box;

    public SLevelInfo(int level) {
        this.level = level;
        this.tileCount = 0;
        box = new Box(180, 90, -180, -90);
    }

    public SLevelInfo() {
        this(0);
    }
}
