package cn.mapway.ui.client.mvc.attribute;

import cn.mapway.ui.client.tools.JSON;
import cn.mapway.ui.client.util.Logs;
import elemental2.core.JsArray;
import javaemul.internal.annotations.HasNoSideEffects;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;

import java.util.ArrayList;
import java.util.List;

import static jsinterop.base.InternalPreconditions.checkType;

public class OptionProvider implements IOptionProvider {
    List<Option> options = new ArrayList<>();
    IOptionProviderCallback callback;

    @HasNoSideEffects
    public static native boolean hasLength(Object obj) /*-{
        return typeof obj == 'object' && typeof obj.length == 'number';
    }-*/;

    public void parse(String optionString) {

        if (optionString == null || optionString.length() == 0) {
            return;
        }


        try {
            Object parse = JSON.parse(optionString);
            String typeof = Js.typeof(parse);
            if (typeof == "string") {
                String string = Js.asString(parse);
                Option option = new Option(string, string);
                options.add(option);
            } else if (typeof == "boolean") {
                boolean b = Js.asBoolean(parse);
                Option option = new Option(String.valueOf(b), String.valueOf(b));
                options.add(option);
            } else if (typeof == "number") {
                double b = Js.asAny(parse).asDouble();
                Option option = new Option(String.valueOf(b), String.valueOf(b));
                options.add(option);
            } else if (hasLength(parse)) {
                //arrayLike
                JsArrayLike<Object> arrayLike = Js.asArrayLike(parse);
                for (int i = 0; i < arrayLike.getLength(); i++) {
                    Object item = arrayLike.getAt(i);
                    typeof = Js.typeof(item);
                    if (typeof == "string") {
                        String string = Js.asString(item);
                        Option option = new Option(string, string);
                        options.add(option);
                    } else if (typeof == "boolean") {
                        boolean b = Js.asBoolean(item);
                        Option option = new Option(String.valueOf(b), String.valueOf(b));
                        options.add(option);
                    } else if (typeof == "item") {
                        double b = Js.asAny(parse).asDouble();
                        Option option = new Option(String.valueOf(b), String.valueOf(b));
                        options.add(option);
                    } else if (typeof == "object") {
                        JsPropertyMap propertyMap = Js.asPropertyMap(item);
                        String name = (String) propertyMap.get("name");
                        String value = (String) propertyMap.get("value");
                        String icon = (String) propertyMap.get("icon");
                        Option option = new Option(name, value, icon);
                        options.add(option);
                    }
                }
            }

        } catch (Exception e) {
            Logs.info(e.getMessage());
        }


        if (callback != null) {
            callback.setOptions(options);
        }
    }

    @Override
    public List<Option> getOptions() {
        return options;
    }

    @Override
    public void setCallback(IOptionProviderCallback callback) {
        this.callback = callback;
    }
}
