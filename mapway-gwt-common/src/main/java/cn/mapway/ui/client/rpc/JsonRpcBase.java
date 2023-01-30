package cn.mapway.ui.client.rpc;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import elemental2.core.Global;
import elemental2.dom.XMLHttpRequest;
import elemental2.promise.Promise;
import jsinterop.base.Js;

import java.util.HashMap;
import java.util.Map;

import static elemental2.dom.XMLHttpRequest.DONE;

/**
 * Http Json invoker base function collections
 */
public class JsonRpcBase {

    public static final String HEAD_CONTENT_TYPE = "Content-Type";
    public static final String HEAD_ACCEPT_TYPE = "Accept-Type";
    public static final String JSON_CONTENT_TYPE = "application/json";
    Map<String, String> fixedHeaders = new HashMap<>();
    String basePath = "";

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return this.basePath;
    }

    /**
     * 向所有的请求添加Header pair
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        if (name != null && name.length() > 0) {
            fixedHeaders.put(name, value);
        }
    }

    /**
     * 删除请求头
     *
     * @param name
     */
    public void removeHeader(String name) {
        if (name != null && name.length() > 0) {
            fixedHeaders.remove(name);
        }
    }

    /**
     * 请求http接口
     *
     * @param url
     * @param method
     * @param body
     * @param <T>
     * @return
     */
    protected <T> Promise<T> httpRequest(String url, String method, String body,
                                         Map<String, String> headers
    ) {
        return new Promise<T>(
                (accept, reject) -> {
                    XMLHttpRequest request = new XMLHttpRequest();
                    request.open(method, url, true);
                    if (fixedHeaders.size() > 0) {
                        for (Map.Entry<String, String> item : fixedHeaders.entrySet()) {
                            request.setRequestHeader(item.getKey(), item.getValue());
                        }
                    }
                    if (headers != null && headers.size() > 0) {
                        for (Map.Entry<String, String> item : headers.entrySet()) {
                            request.setRequestHeader(item.getKey(), item.getValue());
                        }
                    }
                    request.onreadystatechange = (status) -> {
                        if (request.readyState == DONE) {
                            if (request.status == 200) {
                                T result = Js.uncheckedCast(Global.JSON.parse(request.responseText));
                                accept.onInvoke(result);
                            } else if (request.status == 0) {
                                HttpError error = new HttpError();
                                error.code = request.status;
                                error.message = "网络故障";
                                reject.onInvoke(error);
                            } else {
                                HttpError error = new HttpError();
                                error.code = request.status;
                                error.message = request.responseText;
                                reject.onInvoke(error);
                            }
                        }
                        return true;
                    };
                    request.onerror = (event) -> {
                        HttpError error = new HttpError();
                        error.code = -1;
                        error.message = "传输错误";
                        reject.onInvoke(error);
                        return true;
                    };
                    request.send(body);
                }
        );
    }

    private static RegExp pattern = RegExp.compile("\\{(.+?)\\}");


    /**
     * 用参数填充URLPATH
     * @param path
     * @param visitor
     * @return
     */
    public String patternFill(String path, PathItemVisitor visitor) {
        StringBuilder builder = new StringBuilder();
        RegExp pattern = RegExp.compile("\\{(.+?)\\}", "g");
        pattern.setLastIndex(0);
        MatchResult matcher = pattern.exec(path);
        if (visitor != null) {
            int i = 0;
            while (matcher != null) {
                String replacement = visitor.onItem(matcher.getGroup(1));
                builder.append(path.substring(i, matcher.getIndex()));
                if (replacement == null) {
                    builder.append("null");
                } else {
                    builder.append(replacement);
                    i = pattern.getLastIndex();
                }
                matcher = pattern.exec(path);
            }
            builder.append(path.substring(i));
            return builder.toString();
        } else {
            return path;
        }
    }

//    public static void main(String[] args) {
//        String path="/test/${id}/${name}/abc";
//        JsonRpcBase p=new JsonRpcBase();
//        String patternFill = p.patternFill(path, name -> {
//            if (name.equals("id")) {
//                return "123";
//            } else if (name.equals("name")) {
//                return "zhangjianshe";
//            }else{
//                return "";
//            }
//        });
//
//        System.out.println(patternFill);
//    }
}
