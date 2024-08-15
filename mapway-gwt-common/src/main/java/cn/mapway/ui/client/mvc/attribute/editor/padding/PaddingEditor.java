package cn.mapway.ui.client.mvc.attribute.editor.padding;

import cn.mapway.ui.client.mvc.Size;
import cn.mapway.ui.client.tools.IData;
import cn.mapway.ui.client.util.StringUtil;
import cn.mapway.ui.client.widget.CommonEventComposite;
import cn.mapway.ui.client.widget.dialog.Popup;
import cn.mapway.ui.shared.CommonEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

class PaddingEditor extends CommonEventComposite implements IData<String[]> {
    private static final PaddingEditorUiBinder ourUiBinder = GWT.create(PaddingEditorUiBinder.class);
    private static Popup<PaddingEditor> dialog;
    @UiField
    TextBox txtTop;
    @UiField
    TextBox txtLeft;
    @UiField
    TextBox txtRight;
    @UiField
    TextBox txtBottom;
    String[] data;

    public PaddingEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initEvent(txtLeft);
        initEvent(txtTop);
        initEvent(txtRight);
        initEvent(txtBottom);
    }

    public static Popup<PaddingEditor> getDialog(boolean reuse) {
        if (reuse) {
            if (dialog == null) {
                dialog = createOne();
            }
            return dialog;
        } else {
            return createOne();
        }
    }

    private static Popup<PaddingEditor> createOne() {
        PaddingEditor editor = new PaddingEditor();
        return new Popup<>(editor);
    }

    private void initEvent(TextBox box) {
        box.addBlurHandler(event -> {
            fromUI(true);
        });
        box.addMouseWheelHandler(this::mouseWheelHandler);
        box.addValueChangeHandler(event -> {
            fromUI(true);
        });
    }

    private void mouseWheelHandler(MouseWheelEvent event) {
        TextBox txtBox = (TextBox) event.getSource();
        String value = txtBox.getValue();
        if (value == null || value.length() == 0) {
            txtBox.setValue("0");
            return;
        }
        String[] strings = StringUtil.departCssValue(value);
        if (StringUtil.isBlank(strings[0])) {
            strings[0] = "0";
            txtBox.setValue("0");
            return;
        }
        Integer intValue = Integer.parseInt(strings[0]);
        if (event.isNorth()) {
            intValue++;
            txtBox.setValue(intValue + strings[1], true);
        } else if (event.isSouth()) {
            intValue--;
            txtBox.setValue(intValue + strings[1], true);
        }
    }

    private void fromUI(boolean fireEvent) {
        data[0] = txtTop.getValue();
        data[1] = txtRight.getValue();
        data[2] = txtBottom.getValue();
        data[3] = txtLeft.getValue();
        if (fireEvent) {
            fireEvent(CommonEvent.valueChangedEvent(data));
        }
    }

    @Override
    public Size requireDefaultSize() {
        return new Size(150, 145);
    }

    @Override
    public String[] getData() {
        return data;
    }

    @Override
    public void setData(String[] obj) {
        data = obj;
        toUI();
    }

    private void toUI() {
        if (data == null) {
            data = new String[4];
            data[0] = "0";
            data[1] = "0";
            data[2] = "0";
            data[3] = "0";
        }
        txtTop.setValue(data[0]);
        txtRight.setValue(data[1]);
        txtBottom.setValue(data[2]);
        txtLeft.setValue(data[3]);
    }

    interface PaddingEditorUiBinder extends UiBinder<LayoutPanel, PaddingEditor> {
    }
}