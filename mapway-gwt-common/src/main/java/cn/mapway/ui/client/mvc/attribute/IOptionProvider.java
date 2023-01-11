package cn.mapway.ui.client.mvc.attribute;

import java.util.List;

/**
 * IOptionProvider
 *
 * @author zhang
 */
public interface IOptionProvider {
    List<Option> getOptions();

    void setCallback(IOptionProviderCallback callback);
}
