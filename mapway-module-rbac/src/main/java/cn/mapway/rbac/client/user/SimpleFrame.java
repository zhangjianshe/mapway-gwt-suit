package cn.mapway.rbac.client.user;

import cn.mapway.rbac.shared.RbacConstant;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.BaseAbstractModule;
import cn.mapway.ui.client.mvc.IModule;
import cn.mapway.ui.client.mvc.ModuleMarker;
import cn.mapway.ui.client.mvc.ModuleParameter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

@ModuleMarker(value = SimpleFrame.MODULE_CODE,
        name = "简单模块",
        unicode = Fonts.MESSAGE_LIST,
        summary = "SimpleModule",
        group = RbacConstant.MODULE_GROUP_RBAC,
        order = 1
)
public class SimpleFrame extends BaseAbstractModule {

    public static final String MODULE_CODE = "simple_frame";

    interface SimpleFrameUiBinder extends UiBinder<LayoutPanel, SimpleFrame> {
    }

    private static SimpleFrameUiBinder ourUiBinder = GWT.create(SimpleFrameUiBinder.class);
    @UiField
    LayoutPanel root;

    public SimpleFrame() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public boolean initialize(IModule parentModule, ModuleParameter parameter) {
        boolean b= super.initialize(parentModule, parameter);
        Object object=parameter.get();
        root.clear();
        if(object instanceof Widget)
        {
            Widget widget= (Widget) object;
            appendWidget(widget);
        }
        else if(object instanceof String)
        {
            HTMLPanel panel=new HTMLPanel("");
            panel.setStyleName("ai-flex-panel");
            HTML html=new HTML((String) object);
            html.setStyleName("ai-message");
            panel.add(html);
            appendWidget(panel);
        }
        return b;
    }

    private  void appendWidget(Widget widget)
    {
        root.clear();
        root.add(widget);
        root.setWidgetLeftRight(widget, 0, Style.Unit.PX, 0, Style.Unit.PX);
        root.setWidgetTopBottom(widget, 0, Style.Unit.PX, 0, Style.Unit.PX);
    }

    @Override
    public String getModuleCode() {
        return MODULE_CODE;
    }
}