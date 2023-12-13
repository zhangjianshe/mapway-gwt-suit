package cn.mapway.ui.client.mvc.attribute.editor.design;


import cn.mapway.ui.client.util.Logs;
import elemental2.core.Global;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * 设计时的选项
 */
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class DesignOption {
    public String code;
    public String options;

    /**
     * 解析设计事情的参数属性
     *
     * @param options
     * @return
     */
    public static DesignOption parse(String options) {

        Object parse = null;
        try {
            parse = Global.JSON.parse(options);
        } catch (Exception e) {
            Logs.info("Error parsing design option " + options);
        }
        if (parse == null) {
            //没有解析成功
            DesignOption designOption = new DesignOption();
            designOption.code = "";
            designOption.options = "";
            return designOption;
        } else {
            DesignOption designOption = Js.uncheckedCast(parse);
            if (designOption.code == null) {
                designOption.code = "";
            }
            return designOption;
        }
    }
}

