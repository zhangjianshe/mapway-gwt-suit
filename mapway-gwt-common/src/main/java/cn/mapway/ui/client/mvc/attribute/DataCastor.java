package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.tools.JSON;
import cn.mapway.ui.client.util.Logs;
import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import java.util.ArrayList;
import java.util.List;

public class DataCastor {

    public static Long castToLong(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            str = str.trim();
            if (str.length() == 0) {
                return 0L;
            }
            return Long.parseLong(str);
        } else if (obj instanceof Integer) {
            Integer v = (Integer) obj;
            return v.longValue();
        } else if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Double) {
            Double v = (Double) obj;
            return v.longValue();
        } else if (obj instanceof Float) {
            Float v = (Float) obj;
            return v.longValue();
        } else if (obj instanceof Number) {
            Number v = (Number) obj;
            return v.longValue();
        }  else if (obj instanceof Boolean) {
            Boolean v = (Boolean) obj;
            return v?1L:0L;
        } else {
            Logs.info("转换数据类型错误");
            return 0L;
        }
    }


    public static Integer castToInteger(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            str = str.trim();
            if (str.length() == 0) {
                return 0;
            }
            return Integer.parseInt(str);
        } else if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Double) {
            Double v = (Double) obj;
            return v.intValue();
        } else if (obj instanceof Long) {
            Long v = (Long) obj;
            return v.intValue();
        } else if (obj instanceof Float) {
            Float v = (Float) obj;
            return v.intValue();
        } else if (obj instanceof Number) {
            Number v = (Number) obj;
            return v.intValue();
        }  else if (obj instanceof Boolean) {
            Boolean v = (Boolean) obj;
            return v?1:0;
        } else {
            Logs.info("转换数据类型错误");
            return 0;
        }
    }

    public static Boolean castToBoolean(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            str = str.trim();
            if (str.length() == 0) {
                return false;
            }
            return "true".compareToIgnoreCase(str) == 0;
        } else if (obj instanceof Integer) {
            Integer v = (Integer) obj;
            return v != 0;
        } else if (obj instanceof Double) {
            Double v = (Double) obj;
            return Math.abs(v - 0.0001) > 0;
        } else if (obj instanceof Long) {
            Long v = (Long) obj;
            return v != 0;
        } else if (obj instanceof Float) {
            Float v = (Float) obj;
            return Math.abs(v - 0.0001) > 0;
        } else if (obj instanceof Number) {
            Number v = (Number) obj;
            return v.intValue() != 0;
        }  else if (obj instanceof Boolean) {
           return (Boolean) obj;
        } else {
            Logs.info("转换数据类型错误");
            return false;
        }
    }

    public static String castToString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Integer) {
            return obj.toString();
        } else if (obj instanceof Double) {
            Double v = (Double) obj;
            return v.toString();
        } else if (obj instanceof Float) {
            Float v = (Float) obj;
            return v.toString();
        } else if (obj instanceof Long) {
            Long v = (Long) obj;
            return v.toString();
        } else if (obj instanceof Number) {
            Number v = (Number) obj;
            return v.toString();
        }  else if (obj instanceof Boolean) {
            Boolean v = (Boolean) obj;
            return v?"true":"false";
        } else {
            Logs.info("转换数据类型错误");
            return "";
        }
    }

    public static Double castToDouble(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            str = str.trim();
            if (str.length() == 0) {
                return 0.;
            }
            return Double.parseDouble(str);
        } else if (obj instanceof Integer) {
            Integer v = (Integer) obj;
            return v.doubleValue();
        } else if (obj instanceof Double) {
            Double v = (Double) obj;
            return v;
        } else if (obj instanceof Long) {
            Long v = (Long) obj;
            return v.doubleValue();
        } else if (obj instanceof Float) {
            Float v = (Float) obj;
            return v.doubleValue();
        } else if (obj instanceof Number) {
            Number v = (Number) obj;
            return v.doubleValue();
        }  else if (obj instanceof Boolean) {
            Boolean v = (Boolean) obj;
            return v?1.0:0.0;
        } else {
            Logs.info("转换数据类型错误");
            return 0.;
        }
    }

    public static Float castToFloat(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            str = str.trim();
            if (str.length() == 0) {
                return 0.f;
            }
            return Float.parseFloat(str);
        } else if (obj instanceof Integer) {
            Integer v = (Integer) obj;
            return v.floatValue();
        } else if (obj instanceof Double) {
            Double v = (Double) obj;
            return v.floatValue();
        } else if (obj instanceof Float) {
            Float v = (Float) obj;
            return v;
        } else if (obj instanceof Number) {
            Number v = (Number) obj;
            return v.floatValue();
        }  else if (obj instanceof Boolean) {
            Boolean v = (Boolean) obj;
            return v?1.0f:0.0f;
        } else {
            Logs.info("转换数据类型错误");
            return 0f;
        }
    }

    public static String attrsValueToJson(List<AttributeValue> attributeValues) {
        if (attributeValues == null) {
            return "[]";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        boolean first = true;
        for (AttributeValue attributeValue : attributeValues) {
            if (!first) {
                stringBuilder.append(",");
            }
            stringBuilder.append("{");
            stringBuilder.append("\"name\":\"").append(attributeValue.getName()).append("\",");
            stringBuilder.append("\"altName\":\"").append(attributeValue.getAltName()).append("\",");
            stringBuilder.append("\"value\":\"").append(attributeValue.getValue()).append("\",");
            stringBuilder.append("\"inputType\":").append(attributeValue.getInputType());
            stringBuilder.append("}");
            if (first) {
                first = false;
            }

        }
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public static List<AttributeValue> attrsValueFromJson(String json) {
        List<AttributeValue> attributeValues = new ArrayList<>();
        if (json == null || json.length() == 0) {
            return attributeValues;
        }
        JsArray array = Js.uncheckedCast(JSON.parse(json));
        if (array == null) {
            return attributeValues;
        }

        for (int i = 0; i < array.length; i++) {
            Object at = array.getAt(i);
            JsPropertyMap<Object> map = Js.asPropertyMap(at);
            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setValue((String) map.get("value"));
            attributeValue.setName((String) map.get("name"));
            attributeValue.setAltName((String) map.get("altName"));
            int o = map.getAsAny("inputType").asInt();
            attributeValue.setInputType(o);

            attributeValues.add(attributeValue);
        }
        return attributeValues;
    }

}
