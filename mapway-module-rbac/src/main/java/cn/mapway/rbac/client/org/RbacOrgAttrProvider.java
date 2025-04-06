package cn.mapway.rbac.client.org;

import cn.mapway.rbac.shared.db.postgis.RbacOrgEntity;
import cn.mapway.ui.client.mvc.attribute.AbstractAttribute;
import cn.mapway.ui.client.mvc.attribute.AbstractAttributesProvider;
import cn.mapway.ui.client.mvc.attribute.DataCastor;
import cn.mapway.ui.client.mvc.attribute.atts.LabelAttribute;
import cn.mapway.ui.client.mvc.attribute.atts.TextBoxAttribute;
import cn.mapway.ui.client.mvc.attribute.design.IEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.icon.IconSelectorEditorMetaData;
import cn.mapway.ui.client.mvc.attribute.editor.sys.TextAreaAttribute;
import cn.mapway.ui.client.mvc.attribute.editor.sys.TextAreaAttributeEditorData;

public class RbacOrgAttrProvider extends AbstractAttributesProvider {
    RbacOrgEntity org;
    public void rebuild(RbacOrgEntity org) {
        this.org=org;
        if(org==null)
        {
            getAttributes().clear();
            notifyAttributeReady();
            return;
        }

        getAttributes().clear();

        getAttributes().add(new LabelAttribute("pid","上级组织") {
            @Override
            public Object getValue() {
                return org.getParentId();
            }
        });
        getAttributes().add(new TextBoxAttribute("name", "组织名称") {
            @Override
            public Object getValue() {
                return org.getName();
            }

            @Override
            public void setValue(Object o) {
                org.setName(DataCastor.castToString(o));
            }
        });

        getAttributes().add(new TextBoxAttribute("code", "组织代码") {
            @Override
            public Object getValue() {
                return org.getCode();
            }

            @Override
            public void setValue(Object o) {
                org.setCode(DataCastor.castToString(o));
            }
        });

        getAttributes().add(new TextBoxAttribute("regionCode", "行政区划代码") {
            @Override
            public Object getValue() {
                return org.getRegionCode();
            }

            @Override
            public void setValue(Object o) {
                org.setRegionCode(DataCastor.castToString(o));
            }
        });

        getAttributes().add(new TextBoxAttribute("charge","排序") {
            @Override
            public Object getValue() {
                return org.getRank();
            }

            @Override
            public String getDefaultValue() {
                return "100";
            }

            @Override
            public void setValue(Object o) {
                org.setRank(DataCastor.castToInteger(o));
            }
        }.asNumber());

        getAttributes().add(new TextBoxAttribute("charge","负责人") {
            @Override
            public Object getValue() {
                return org.getCharger();
            }

            @Override
            public void setValue(Object o) {
                     org.setCharger(DataCastor.castToString(o));
            }
        });


        getAttributes().add(new AbstractAttribute("tel","电话") {
            @Override
            public Object getValue() {
                return org.getTel();
            }

            @Override
            public void setValue(Object o) {
                org.setTel(DataCastor.castToString(o));
            }
        });
        getAttributes().add(new TextBoxAttribute("email","电子邮件") {
            @Override
            public Object getValue() {
                return org.getEmail();
            }

            @Override
            public void setValue(Object o) {
                org.setEmail(DataCastor.castToString(o));
            }
        });

        getAttributes().add(new TextBoxAttribute("web","网址") {
            @Override
            public Object getValue() {
                return org.getLink();
            }

            @Override
            public void setValue(Object o) {
                org.setLink(DataCastor.castToString(o));
            }
        });



        getAttributes().add(new TextBoxAttribute("address","地址") {
            @Override
            public Object getValue() {
                return org.getAddress();
            }

            @Override
            public void setValue(Object o) {
                org.setAddress(DataCastor.castToString(o));
            }
        });

        getAttributes().add(new TextAreaAttribute("summary","简要介绍") {
            @Override
            public Object getValue() {
                return org.getSummary();
            }

            @Override
            public void setValue(Object o) {
                org.setSummary(DataCastor.castToString(o));
            }
        });

        getAttributes().add(new AbstractAttribute("icon","图标") {
            @Override
            public IEditorMetaData getEditorMetaData() {
                return new IconSelectorEditorMetaData();
            }

            @Override
            public Object getValue() {
                return org.getIcon();
            }

            @Override
            public void setValue(Object o) {
                org.setIcon(DataCastor.castToString(o));
            }
        });


        notifyAttributeReady();
    }
    public RbacOrgEntity getOrg() {
        return org;
    }

    @Override
    public String getAttributeTitle() {
        if(org==null)
        {
            return "选择组织";
        }

        return org.getName();
    }
}
