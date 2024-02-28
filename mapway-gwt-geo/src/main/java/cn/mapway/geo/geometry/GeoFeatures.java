package cn.mapway.geo.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoFeature
 *
 * @author zhangjianshe@gmail.com
 */
public class GeoFeatures extends GeoObject {

    List<GeoFeature> featureList;

    public GeoFeatures() {
        type = GeoJsonType.GEO_FEATURE_COLLECTION;
        featureList = new ArrayList<>();
    }

    public List<GeoFeature> getFeatureList() {
        return featureList;
    }

    public String toGeoJson() {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"type\":\"").append(type).append("\"");
        sb.append(",\"features\":[");
        int index = 0;
        for (GeoFeature f : featureList) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append(f.toGeoJson());
            index++;
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public GeoExtend getExtend() {
        if(featureList.size()>0)
        {
            return featureList.get(0).getGeometry().getExtend();
        }
        return  null;
    }

    public void add(GeoFeature feature) {
        featureList.add(feature);
    }
}
