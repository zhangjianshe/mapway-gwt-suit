package cn.mapway.ui.client.mvc.decorator.link;

import java.util.ArrayList;
import java.util.List;

/**
 * Links
 *
 * @author zhang
 */
public class Links {
    List<Link> data;

    public Links() {
        data = new ArrayList<>();
    }

    public List<Link> getLinks() {
        return data;
    }

    public Link findById(String id) {
        for (Link link : data) {
            if (link.getId().equals(id)) {
                return link;
            }
        }
        return null;
    }

    public void clear() {
        data.clear();
    }

    public boolean remove(Link link) {
        return data.remove(link);
    }

    public Links addLink(Link link) {
        data.add(link);
        return this;
    }

    public void offsetStart(double offsetX, double offsetY) {
        for (Link link : data) {
            link.offsetStart(offsetX, offsetY);
        }
    }

    public void offsetEnd(double offsetX, double offsetY) {
        for (Link link : data) {
            link.offsetEnd(offsetX, offsetY);
        }
    }

    public void offset(double offsetX, double offsetY) {
        for (Link link : data) {
            link.offset(offsetX, offsetY);
        }
    }

    public int size() {
        return data.size();
    }

    public Link getAt(int index) {
        return data.get(index);
    }
}
