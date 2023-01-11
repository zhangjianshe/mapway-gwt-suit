package cn.mapway.test.client.openapi;

import cn.mapway.doc.openapi.module.OpenApiDocument;
import cn.mapway.doc.openapi.module.PathItemObject;
import cn.mapway.ui.client.util.Logs;
import cn.mapway.ui.client.widget.panel.MessagePanel;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import cn.mapway.ui.client.widget.tree.ZTree;
import jsinterop.base.Js;
import jsinterop.base.JsForEachCallbackFn;
import jsinterop.base.JsPropertyMap;

/**
 * interface tree
 */
public class EntryTree extends ZTree {
    OpenApiDocument openApiDocument;
    public void load(OpenApiDocument document) {
        this.openApiDocument=document;
        toUI();
    }

    MessagePanel messagePanel;

    private void toUI() {
        clear();
        if(openApiDocument.paths==null)
        {
           showEmptyMessage();
        }
        else{
            final int[] count=new int[]{0};
            final JsPropertyMap<PathItemObject> paths = openApiDocument.paths;

            paths.forEach(new JsForEachCallbackFn() {
                @Override
                public void onKey(String path) {
                    Logs.info("path " + path );
                    PathItemObject pathItemObject = Js.uncheckedCast( paths.get(path));
                    pathItemObject = Js.uncheckedCast(pathItemObject.resolve(openApiDocument.components));
                    ImageTextItem item = addFontIconItem(null, path, "");
                    item.setData(pathItemObject);
                    count[0]++;
                }
            });
            if(count[0]==0)
            {
                showEmptyMessage();
            }

        }
    }

    private void showEmptyMessage() {
        add(sureMessagePanel().setText("该文档没有接口"));
    }

    private MessagePanel sureMessagePanel() {
        if(messagePanel==null)
        {
            messagePanel=new MessagePanel();
            messagePanel.setHeight("250px");
        }
        return messagePanel;
    }

}
