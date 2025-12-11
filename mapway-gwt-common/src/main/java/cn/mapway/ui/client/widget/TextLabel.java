package cn.mapway.ui.client.widget;


import cn.mapway.ui.client.mvc.window.ISelectable;
import cn.mapway.ui.client.tools.IData;
import com.google.gwt.user.client.ui.Label;

/**
 * TextLabel
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class TextLabel extends Label implements IData {

    Object data;
    int messageCount = 0;

    public TextLabel() {
        this("");
    }

    public TextLabel(String text) {
        super(text);
        setStyleName("ai-text");
    }

    public void setPrefixIconUnicode(String unicode) {
        if(unicode==null || unicode.length()==0){
            setProperty("data-prefix",null);
        }
        else{
            int codepoint=Integer.parseInt(unicode.toUpperCase(),16);
            String t= new String(new int[]{codepoint},0,1);
            setProperty("data-prefix",t);
        }
    }
    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object obj) {
        data = obj;
    }

    public void setSelected(boolean b) {
        if (b) {
            setProperty(ISelectable.SELECT_ATTRIBUTE, "true");
        } else {
            setProperty(ISelectable.SELECT_ATTRIBUTE,null);
        }
    }

    public void setProperty(String name, String value) {
        if (name != null) {
            if (value == null) {
                getElement().removeAttribute(name);
            } else {
                getElement().setAttribute(name, value);
            }
        }
    }

    public void addMessageCount(int count) {
        messageCount += count;
        setProperty("count", messageCount + "");
    }

    public void setMessageCount(int count) {
        messageCount = count;
        if (messageCount <= 0) {
            setProperty("count", null);
        } else {
            setProperty("count", messageCount + "");
        }
    }
}
