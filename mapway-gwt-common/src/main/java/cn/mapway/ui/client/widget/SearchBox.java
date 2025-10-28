package cn.mapway.ui.client.widget;

import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.util.StringUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 搜索框
 */
public class SearchBox extends CommonEventComposite implements HasValueChangeHandlers<String>, HasValue<String> {
    private static final SearchBoxUiBinder ourUiBinder = GWT.create(SearchBoxUiBinder.class);
    @UiField
    TextBox txtSearch;
    @UiField
    FontIcon iconSearch;

    public SearchBox() {
        initWidget(ourUiBinder.createAndBindUi(this));
        iconSearch.setIconUnicode(Fonts.SEARCH);
        txtSearch.addKeyDownHandler((event) -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                String value = txtSearch.getValue();
                ValueChangeEvent.fire(this, value);
            }
        });
        iconSearch.addClickHandler((event) -> {
            String value = txtSearch.getValue();
            ValueChangeEvent.fire(this, value);
        });
        txtSearch.addDomHandler(event -> {
            String placeholder = getPlaceholder();
            if (StringUtil.isNotBlank(placeholder)) {
                txtSearch.setValue(placeholder, true);
            } else {
                String v = txtSearch.getValue();
                txtSearch.setSelectionRange(0, v.length() - 1);
            }
        }, DoubleClickEvent.getType());
        setPlaceholder("输入关键词");
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    public String getPlaceholder() {
        return txtSearch.getElement().getAttribute("placeholder");
    }

    public void setPlaceholder(String placeholder) {
        txtSearch.getElement().setAttribute("placeholder", placeholder);
    }

    public void setValue(String value, boolean fireEvent) {
        if (value == null) {
            return;
        }
        txtSearch.setValue(value);
        if (fireEvent) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public String getValue() {
        return txtSearch.getValue();
    }

    public void setValue(String value) {
        setValue(value, false);
    }

    interface SearchBoxUiBinder extends UiBinder<HTMLPanel, SearchBox> {
    }
}