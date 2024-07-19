package cn.mapway.ui.client.util;

import elemental2.core.JsObject;
import jsinterop.base.Js;

public class Jss {

    /**
     * 将　plain Object 转化为　Class<T></T>
     *
     * @param instance
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T castTo(Object instance, Class<T> clazz) {
        // Create a new instance of the class to get its prototype
        T prototypeInstance = Js.uncheckedCast(
                JsObject.getPrototypeOf(Js.asConstructorFn(clazz).construct())
        );
        return Js.uncheckedCast(JsObject.setPrototypeOf(instance, prototypeInstance));
    }

}
