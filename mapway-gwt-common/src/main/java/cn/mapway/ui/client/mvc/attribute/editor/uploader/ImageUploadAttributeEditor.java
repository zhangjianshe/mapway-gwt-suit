package cn.mapway.ui.client.mvc.attribute.editor.uploader;

import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.fonts.Fonts;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.IAttribute;
import cn.mapway.ui.client.mvc.attribute.design.ParameterAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.*;
import cn.mapway.ui.client.mvc.attribute.editor.common.AbstractAttributeEditor;
import cn.mapway.ui.client.mvc.attribute.editor.common.CommonEditorParameterDesigner;
import cn.mapway.ui.client.widget.ImageUploader;
import cn.mapway.ui.shared.CommonEvent;
import cn.mapway.ui.shared.UploadReturn;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片上传相关属性编辑器
 */
@AttributeEditor(value = ImageUploadAttributeEditor.EDITOR_CODE,
        name = "图像选择",
        group = IAttributeEditor.CATALOG_RUNTIME,
        summary = "上传一个图像",
        author = "ZJS",
        icon = Fonts.ENHANCE_COLOR
)
public class ImageUploadAttributeEditor extends AbstractAttributeEditor<String> {
    public static final String EDITOR_CODE = EditorCodes.EDITOR_IMAGE_UPLOAD;
    private static final ImageUploadAttributeEditorUiBinder ourUiBinder = GWT.create(ImageUploadAttributeEditorUiBinder.class);
    @UiField
    ImageUploader imageUploader;
    String newValue = "";
    CommonEditorParameterDesigner designer;


    public ImageUploadAttributeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    /**
     * 编辑器的唯一识别代码
     *
     * @return
     */
    @Override
    public String getCode() {
        return EDITOR_CODE;
    }

    @Override
    public Widget getDisplayWidget() {
        return imageUploader;
    }


    /**
     * 当数据发生变化后 调用这个方法更新界面的数据
     */
    public void updateUI() {
        processEditorOption();
        IAttribute attribute = getAttribute();
        if (attribute == null) {
            return;
        }

        Object obj = attribute.getValue();
        imageUploader.setUrl(DataCastor.castToString(obj));

        //需要设定高度
        String height = option(ParameterKeys.KEY_HEIGHT, "80px");
        getDisplayWidget().setHeight(height);
    }

    @Override
    public void fromUI() {
        if (getAttribute() != null && newValue != null && newValue.length() > 0) {
            getAttribute().setValue(newValue);
        }
    }

    /**
     * 处理编辑器参数
     */
    private void processEditorOption() {
        imageUploader.setAction(option(ParameterKeys.KEY_IMAGE_UPLOAD_ACTION, ""),
                option(ParameterKeys.KEY_IMAGE_UPLOAD_REL, "project"));
    }

    @Override
    public void onEditorOptionChanged(String key) {
        // don't care key
        processEditorOption();
    }

    @UiHandler("imageUploader")
    public void imageUploaderCommon(CommonEvent event) {
        if (event.isMessage()) {
            fireMessage(event.getValue());
        } else if (event.isOk()) {
            if (getAttribute() != null) {
                UploadReturn uploadReturn = event.getValue();
                newValue = uploadReturn.relPath;
                getAttribute().setValue(uploadReturn.relPath);
                fireMessage(MessageObject.info(0, "上传图片成功，请保存"));
            }
        }
    }

    @Override
    public IEditorDesigner getDesigner() {

        if (designer == null) {
            designer = new CommonEditorParameterDesigner();

            List<IAttribute> parameters = new ArrayList<>();
            parameters.add(new ParameterAttribute(ParameterKeys.KEY_HEIGHT, "高度", "80px"));
            parameters.add(new ParameterAttribute(ParameterKeys.KEY_IMAGE_UPLOAD_ACTION, "上传URL", ""));
            parameters.add(new ParameterAttribute(ParameterKeys.KEY_IMAGE_UPLOAD_REL, "相对路径", "project"));

            designer.setParameters("图像上传组件参数", parameters);
        }

        return designer;
    }

    interface ImageUploadAttributeEditorUiBinder extends UiBinder<HTMLPanel, ImageUploadAttributeEditor> {
    }
}
