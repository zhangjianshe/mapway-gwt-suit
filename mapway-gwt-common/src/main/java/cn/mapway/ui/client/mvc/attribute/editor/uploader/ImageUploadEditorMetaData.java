package cn.mapway.ui.client.mvc.attribute.editor.uploader;

import cn.mapway.ui.client.mvc.attribute.marker.AbstractEditorMetaData;

/**
 * ImageUploadEditorMetaData 对应的编辑器实例数据
 */
public abstract class ImageUploadEditorMetaData extends AbstractEditorMetaData {
    public ImageUploadEditorMetaData() {
        super(ImageUploadAttributeEditor.EDITOR_CODE);
    }
}
