package cn.mapway.ui.client.mvc.attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * 联合属性提供器
 * 可以组合几个属性提供其为一个
 */
public class ComposeAttributeProvider extends AbstractAttributesProvider implements IAttributeReadyCallback {

    List<IAttributesProvider> providers = new ArrayList<>();

    public void addProvider(IAttributesProvider attributeProvider) {
        attributeProvider.addAttributeReadyCallback(this);
        providers.add(attributeProvider);
    }

    @Override
    public String getAttributeSummary() {
        String html = "";
        for (IAttributesProvider attributeProvider : providers) {
            html += attributeProvider.getAttributeSummary();
        }
        return html;
    }

    @Override
    public String getAttributeTitle() {
        if (providers.size() == 0) {
            return "没有数据";
        }
        return providers.get(0).getAttributeTitle();
    }

    @Override
    public List<IAttribute> getAttributes() {

        List<IAttribute> attributes = new ArrayList<>();
        for (IAttributesProvider attributeProvider : providers) {
            attributes.addAll(attributeProvider.getAttributes());
        }
        return attributes;

    }

    public void clearProviders() {
        for (IAttributesProvider attributeProvider : providers) {
            attributeProvider.removeAttributeReadyCallback(this);
        }
        providers.clear();
        notifyAttributeReady();
    }

    /**
     * 属性准备好后 调用这个方法通知
     *
     * @param attributeProvider
     */
    @Override
    public void onAttributeReady(IAttributesProvider attributeProvider) {
        notifyAttributeReady();
    }
}
