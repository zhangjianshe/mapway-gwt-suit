package cn.mapway.ui.client.event;

import com.google.gwt.core.client.Callback;

/**
 * confirm user input
 */
public abstract class Confirm implements Callback<Object, Object> {
    @Override
    public void onFailure(Object reason) {
    }
}
