package cn.mapway.util.console;

import java.util.ArrayList;
import java.util.List;

public class Ansi {
    List<AnsiColor> prefix=new ArrayList<AnsiColor>();
    public Ansi set(AnsiColor color)
    {
        prefix.add(color);
        return this;
    }

    public static Ansi from(AnsiColor color)
    {
        Ansi ansi=new Ansi();
        if(color!=null)
        {
            return ansi.set(color);
        }
        return ansi;
    }
    public String val(String object)
    {
        return val(object,true);
    }

    public String val(String object,boolean reset)
    {
        StringBuilder sb = new StringBuilder();
        for(AnsiColor color : prefix)
        {
            sb.append(color.getCode());
        }
        if(object!=null)
        {
            sb.append(object);
        }
        if(reset)
        {
            sb.append(AnsiColor.STYLE_RESET.getCode());
        }
        return sb.toString();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(AnsiColor color : prefix)
        {
            sb.append(color.getCode());
        }
        return sb.toString();
    }
}
