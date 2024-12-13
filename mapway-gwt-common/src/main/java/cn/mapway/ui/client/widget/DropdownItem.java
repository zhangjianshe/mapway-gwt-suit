package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.tree.ImageTextItem;
import com.google.gwt.resources.client.ImageResource;

public class DropdownItem extends ImageTextItem {

    String unicode;

    public DropdownItem(ImageResource resource, String text) {
        super(resource, text);
    }

    public DropdownItem(String resource, String text) {
        super(resource, text);
    }

    public DropdownItem() {
        super();
    }

    private boolean isSelect = false;

    @Override
    public void setSelect(boolean b) {
        super.setSelect(b);
        isSelect = b;
        if( StringUtil.isNotBlank(unicode) ){
            if (b) {
                setIconSuffix(unicode);
            } else {
                setIconSuffix(null);
            }
        }
    }

    public boolean isSelect(){
        return isSelect;
    }

    public void setSuffixUnicode(String unicode) {
        this.unicode = unicode;
    }

}
