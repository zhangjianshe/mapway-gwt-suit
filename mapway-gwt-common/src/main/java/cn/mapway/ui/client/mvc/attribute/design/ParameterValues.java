package cn.mapway.ui.client.mvc.attribute.design;

import java.util.ArrayList;

/**
 * 描述一组参数
 */
public class ParameterValues extends ArrayList<ParameterValue> {

    public static String escapeString(Object s) {
        if (s == null) {
            return "";
        }
        String temp = s.toString();
        if (temp.length() == 0) {
            return "";
        }
        temp = temp.replaceAll("\\\\", "\\\\\\\\");
        return temp.replaceAll("\"", "\\\\\"");
    }

    public ParameterValue findNByName(String name) {
        for (ParameterValue value : this) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }

    public ParameterValues addParameter(ParameterValue value) {
        if (value == null) {
            return this;
        }
        return addParameter(value.name, value.value, value.init,value.unicode);
    }

    public ParameterValues removeParameter(String name) {
        if (name == null || name.length() == 0) {
            return this;
        }
        ParameterValue nByName = findNByName(name);
        if (nByName != null) {
            remove(nByName);
        }
        return this;
    }

    public ParameterValues merge(ParameterValues source) {
        if (source != null) {
            addAll(source);
        }
        return this;
    }

    public ParameterValues addParameter(String name, Object value) {
        return addParameter(name, value, false,"");
    }

    public ParameterValues addParameter(String name, Object value, boolean init,String unicode) {
        if (name == null || name.length() == 0) {
            return this;
        }
        ParameterValue nByName = findNByName(name);
        if (nByName == null) {
            add(ParameterValue.create(name, value, init).setUnicode(unicode));
        } else {
            nByName.value = value;
            nByName.init = init;
            nByName.unicode=unicode;
        }
        return this;
    }

    public String toJson() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        int index = 0;
        for (index = 0; index < this.size(); index++) {
            if (index > 0) {
                stringBuilder.append(",");
            }
            ParameterValue parameterValue = get(index);
            stringBuilder.append(parameterValue.toJson());
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
