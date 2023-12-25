package cn.mapway.geo.client.raster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum MetadataEnum {

    META_KEY_WAVELENGTH("wavelength", "波长"),

    META_KEY_WAVELENGTH_UNITS("wavelength_units", "波长单位",
            new MetadataValue("nanometers", "纳米"),
            new MetadataValue("micrometers", "微米"));
    private final String key;

    private final String name;

    private MetadataValue[] values;
    MetadataEnum(String key, String name, MetadataValue... values){
        this.key = key;
        this.name = name;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public MetadataValue[] getValues() {
        return values;
    }



    public static List<MetadataResult> getMetadataKeys(Map<String, String> map){
        List<MetadataResult> result = new ArrayList<MetadataResult>();
        if(map != null){
            Set<String> keySet = map.keySet();
            for (String key: keySet){
                MetadataEnum metadataEnum = getMetadataEnum(key);
                if(metadataEnum != null){
                    MetadataResult meta = new MetadataResult(metadataEnum.getKey(), metadataEnum.getName());
                    result.add(meta);
                }
            }
        }
        return result;
    }


    public static MetadataResult getMetadataValue(String key, String value){
        MetadataEnum metadataEnum = getMetadataEnum(key);
        if(metadataEnum != null && value != null){
            MetadataValue[] values = metadataEnum.getValues();
            if(values != null){
                value = value.toLowerCase();
                for(MetadataValue metaValue : values){
                    if(metaValue.getKey().equals(value)){
                        return new MetadataResult(value, metaValue.getName());
                    }
                }
            }
        }
        return new MetadataResult(key, value);
    }

    public static MetadataEnum getMetadataEnum(String key){
        MetadataEnum[] values = MetadataEnum.values();
        for(MetadataEnum value: values){
            if(value.getKey().equals(key)){
                return value;
            }
        }
        return null;
    }

}
