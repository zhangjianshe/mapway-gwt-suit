package cn.mapway.rbac.client.role;

import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.widget.AiFlexTable;
import cn.mapway.ui.client.widget.FontIcon;
import cn.mapway.ui.client.widget.Header;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.CommonEventHandler;
import cn.mapway.ui.shared.HasCommonHandlers;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;

import java.util.List;

/**
 * 资源列表
 */
public class ResourceTable extends AiFlexTable implements HasCommonHandlers {
    private final ClickHandler deleteHandler = event -> {
        event.preventDefault();
        event.stopPropagation();
        FontIcon icon = (FontIcon) event.getSource();
        RbacResourceEntity resource = (RbacResourceEntity) icon.getData();
        fireEvent(CommonEvent.deleteEvent(resource));
    };

    Boolean enableDelete=true;
    public void setEnableDelete(boolean enableDelete)
    {
        this.enableDelete=enableDelete;
    }
    public ResourceTable() {
        setStyleName("attr-table");
    }

    public void setData(List<RbacResourceEntity> resources) {
        removeAllRows();

        int row = 0;
        int col = 0;
        if(enableDelete) {
            setWidget(row, col++, new Header("删除"));
        }
        setWidget(row, col++, new Header("资源编码"));
        setWidget(row, col++, new Header("资源名称"));
        setWidget(row, col++, new Header("资源类型"));
        setWidget(row, col++, new Header("说明"));

        for (RbacResourceEntity resource : resources) {
            row++;
            col = 0;
            if(enableDelete) {
                FontIcon deleteButton = new FontIcon();
                deleteButton.setIconUnicode(Fonts.DELETE);
                deleteButton.setData(resource);
                deleteButton.addClickHandler(deleteHandler);
                setWidget(row, col++, deleteButton);
            }
            setWidget(row, col++, new Label(resource.getResourceCode()));
            setWidget(row, col++, new Label(resource.getCatalog()+"/"+resource.getName()));
            setWidget(row, col++, new Label(ResourceKind.fromCode(resource.getKind()).getName()));
            setWidget(row, col++, new Label(resource.getSummary()));

        }

        ColumnFormatter columnFormatter = getColumnFormatter();
         col=0;
        if(enableDelete) {
            columnFormatter.setWidth(col++, "40px");
        }
        columnFormatter.setWidth(col++, "100px");
    }

    @Override
    public HandlerRegistration addCommonHandler(CommonEventHandler commonEventHandler) {
        return addHandler(commonEventHandler, CommonEvent.TYPE);
    }
}
