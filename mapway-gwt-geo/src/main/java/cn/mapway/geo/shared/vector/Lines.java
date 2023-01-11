package cn.mapway.geo.shared.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lines
 *
 * @author zhang
 */
public class Lines extends GeoObject implements Serializable {
    private final List<Line> lines;
    Box box;

    public Lines() {
        lines = new ArrayList<>();
        box = new Box();
    }

    public int getCount() {
        return lines.size();
    }

    public Line getLine(int index) {
        if (index < 0 || index >= lines.size()) {
            return null;
        }
        return lines.get(index);
    }

    public Box getExtend() {
        return box;
    }

    public void addLine(Line line) {
        lines.add(line);
        box.expand(line.getExtend());
    }
}
