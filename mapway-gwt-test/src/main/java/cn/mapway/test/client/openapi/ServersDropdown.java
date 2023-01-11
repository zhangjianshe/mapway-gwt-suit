package cn.mapway.test.client.openapi;

import cn.mapway.doc.openapi.module.ServerObject;
import cn.mapway.ui.client.widget.Dropdown;
import elemental2.core.JsArray;
import jsinterop.base.Js;

/**
 * OpenAPI provider Server List
 */
public class ServersDropdown extends Dropdown {
    /**
     * 设置服务其列表
     * @param servers
     * @return
     */
    public ServersDropdown setServerList(JsArray<ServerObject> servers){
        clear();

        if(servers!=null) {
            for (int i = 0; i < servers.getLength(); i++) {
                ServerObject serverObject = Js.uncheckedCast(servers.getAt(i));
                addItem("", serverObject.getName(), serverObject);
            }
            if(servers.getLength()>0)
            {
                setSelectedIndex(0);
            }
        }
        return this;
    }
}
