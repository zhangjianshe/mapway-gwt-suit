package cn.mapway.ui.server.mvc;

import cn.mapway.ui.client.mvc.decorator.impl.DefaultWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.IWindowDecorator;
import cn.mapway.ui.client.mvc.decorator.IWindowDecoratorFactory;
import cn.mapway.ui.client.mvc.decorator.WindowDecoratorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Temp2
 *
 * @author zhang
 */
public class Temp2 implements IWindowDecoratorFactory {
    List<WindowDecoratorInfo> decorators;

    public Temp2()
    {
        decorators=new ArrayList<>();
        // _INIT_DECORATORS__
    }
    @Override
    public IWindowDecorator create(String decorator) {
        if(decorator==null || decorator.length()==0)
        {
            return new DefaultWindowDecorator();
        }
        // __CREATE_DECORATORS__

        return new DefaultWindowDecorator();
    }

    @Override
    public List<WindowDecoratorInfo> getDecorators() {
        return decorators;
    }
}
