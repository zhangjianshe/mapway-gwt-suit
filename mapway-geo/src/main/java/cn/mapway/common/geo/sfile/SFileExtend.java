package cn.mapway.common.geo.sfile;

import cn.mapway.geo.shared.vector.Box;
import lombok.Data;

/**
 * SFileExtend
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
@Data
public class SFileExtend {
    Box box;
    int minZoom;
    int maxZoom;
}
