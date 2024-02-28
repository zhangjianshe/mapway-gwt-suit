package cn.mapway.geo.shared.vector;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Features
 *
 * @author zhang
 */
@Slf4j
public class Features extends GeoObject  {
    public List<Feature> features;

    public Features() {
        features = new ArrayList<>();
    }

    @Override
    public Box getExtend() {
        Box box = new Box();
        for (Feature feature : features) {
            box.expand(feature.getExtend());
        }
        return box;
    }

    public void addFeature(Feature feature) {
        features.add(feature);
    }

    public List<Feature> filter(Box box) {
        log.info("filter box:{}", box);
        ArrayList subFeature = new ArrayList();
        for (int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            if (feature.getExtend().intersect(box)) {
                subFeature.add(feature);
            }
        }
        return subFeature;
    }
}
