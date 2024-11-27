package cn.mapway.rbac.client.role;

import cn.mapway.rbac.shared.ResourceKind;
import cn.mapway.rbac.shared.db.postgis.RbacResourceEntity;
import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.AbstractAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.atts.LabelAttribute;
import cn.mapway.ui.client.mvc.attribute.atts.TextBoxAttribute;
import cn.mapway.ui.client.mvc.attribute.design.IEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.sys.TextAreaAttributeEditorData;

import java.util.Objects;

public class ResourceAttrProvider extends AbstractAttributesProvider {
    RbacResourceEntity resource;

    public RbacResourceEntity getResource() {
        return resource;
    }

    public void rebuild(RbacResourceEntity resource) {
        this.resource = resource;
        if (resource == null) {
            getAttributes().clear();
            notifyAttributeReady();
            return;
        }

        getAttributes().clear();
        ResourceKind resourceKind = ResourceKind.fromCode(resource.getKind());
        if (Objects.requireNonNull(resourceKind) == ResourceKind.RESOURCE_KIND_CUSTOM) {
            buildEditor();
        } else {
            buildReadonly();
        }
        notifyAttributeReady();

    }

    private void buildEditor() {
        getAttributes().add(new TextBoxAttribute("catalog", "分类") {
            @Override
            public Object getValue() {
                return resource.getCatalog();
            }

            @Override
            public void setValue(Object o) {
                resource.setCatalog(DataCastor.castToString(o));
            }
        });
        getAttributes().add(new TextBoxAttribute("name", "资源点名称") {
            @Override
            public Object getValue() {
                return resource.getName();
            }

            @Override
            public void setValue(Object o) {
                resource.setName(DataCastor.castToString(o));
            }
        });
        getAttributes().add(new TextBoxAttribute("code", "资源点代码") {
            @Override
            public Object getValue() {
                return resource.getResourceCode();
            }

            @Override
            public void setValue(Object o) {
                resource.setResourceCode(DataCastor.castToString(o));
            }
        });
        getAttributes().add(new LabelAttribute("kind", "资源类型") {
            @Override
            public Object getValue() {
                return ResourceKind.fromCode(resource.getKind()).getName();
            }
        });
        getAttributes().add(new AbstractAttribute("data", "关联数据") {
            @Override
            public IEditorMetaData getEditorMetaData() {
                TextAreaAttributeEditorData data = new TextAreaAttributeEditorData();
                return data;
            }

            @Override
            public Object getValue() {
                return resource.getData();
            }

            @Override
            public void setValue(Object o) {
                resource.setData(DataCastor.castToString(o));
            }
        });
    }

    private void buildReadonly() {
        getAttributes().add(new LabelAttribute("catalog", "分类") {
            @Override
            public Object getValue() {
                return resource.getCatalog();
            }
        });
        getAttributes().add(new LabelAttribute("name", "资源点名称") {
            @Override
            public Object getValue() {
                return resource.getName();
            }
        });
        getAttributes().add(new LabelAttribute("code", "资源点代码") {
            @Override
            public Object getValue() {
                return resource.getResourceCode();
            }
        });
        getAttributes().add(new LabelAttribute("kind", "资源类型") {
            @Override
            public Object getValue() {
                return ResourceKind.fromCode(resource.getKind()).getName();
            }
        });
        getAttributes().add(new LabelAttribute("data", "关联数据") {
            @Override
            public Object getValue() {
                return resource.getData();
            }
        });

    }

    @Override
    public String getAttributeTitle() {
        if (resource == null) {
            return "选择资源";
        } else {
            return resource.getName();
        }
    }
}
