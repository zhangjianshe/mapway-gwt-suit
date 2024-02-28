package cn.mapway.spring.processor.module;

import org.nutz.json.Json;

public class StringTools {

    /**
     * 将 字符串 按照 separator 分割
     * 根据最后一个 separator 分割成2部分
     * @param source
     * @param separator
     * @return
     */
    public static String[] splitLast(String source,char separator){
        if(source==null) return new String[]{"",""};
        int pos = source.lastIndexOf(separator);
        if (pos>0)
        {
            return new String[]{source.substring(0,pos),source.substring(pos+1)};
        }
        else{
            return new String[]{"",source};
        }
    }

    public static void main(String[] args) {
        String test = "cn.mapway.test.hello.client.IFramw";
        System.out.println(Json.toJson(splitLast(test,'.')));
    }
}
