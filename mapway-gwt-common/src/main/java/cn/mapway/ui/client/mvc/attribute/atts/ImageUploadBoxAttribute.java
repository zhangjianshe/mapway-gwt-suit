package cn.mapway.ui.client.mvc.attribute.atts;

import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.impl.ImageUploadAttributeEditor;

/**
 * 文本框编辑属性
 */
public abstract class ImageUploadBoxAttribute extends AbstractAttribute {
    public ImageUploadBoxAttribute(String name, String altName) {
        super(name, altName, ImageUploadAttributeEditor.EDITOR_CODE);
    }
}
