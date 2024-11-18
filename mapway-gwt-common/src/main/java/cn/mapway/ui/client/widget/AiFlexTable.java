package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.mvc.Size;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

public class AiFlexTable extends FlexTable {
    public AiFlexTable() {
    }

    /**
     * x is columen ; y is row
     *
     * @param event
     * @return
     */
    public Size getCellForDoubleClickEvent(DoubleClickEvent event) {
        Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
        if (td == null) {
            return null;
        }

        int row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
        int column = TableCellElement.as(td).getCellIndex();
        return new Size(column, row);
    }
}
