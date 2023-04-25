package cn.mapway.ui.client.widget.editor;

import cn.mapway.ui.client.tools.IData;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * CmsEditor
 *
 * @author zhangjianshe <zhangjianshe@gmail.com>
 */
public class CmsEditor extends HTML implements RequiresResize, IData<String>, HasValue<String> {

    Editor editor;

    public CmsEditor() {
        super("");
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        recreateController();
    }

    /**
     * 重新生成编辑空间 解决 当 空间 detach之后 不能编辑的BUG
     */
    private void recreateController() {
        EditOptions options = getEditOptions();
        if (options != null) {
            editor = CmsProxy.replace(this.getElement(), options);
        } else {
            editor = CmsProxy.replace(this.getElement());
        }
    }

    /**
     * 子类需要重载这个方法 提供编辑框选项
     *
     * @return
     */
    public EditOptions getEditOptions() {
        return null;
    }

    @Override
    protected void onDetach() {
        editor.destroy();
        super.onDetach();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
    }

    /**
     * 所在的parent变化了 我们要调整自己的大小
     */
    @Override
    public void onResize() {
        int width = getElement().getParentElement().getClientWidth();
        int height = getElement().getParentElement().getClientHeight();
        if(width==0 || height==0)
        {
            return;
        }
        if (editor != null && !editor.isDestroyed()) {
            editor.resize(width, height);
        }
    }

    @Override
    public String getData() {
        return editor.getData();
    }

    @Override
    public void setData(String obj) {
        editor.setData(obj);
    }

    @Override
    public String getValue() {
        return editor.getData();
    }

    @Override
    public void setValue(String value) {
        editor.setData(value);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        setHTML(value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
