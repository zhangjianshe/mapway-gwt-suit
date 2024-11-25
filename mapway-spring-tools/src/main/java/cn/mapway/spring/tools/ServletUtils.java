package cn.mapway.spring.tools;

import cn.mapway.ui.shared.CommonConstant;
import org.nutz.castor.Castors;
import org.nutz.lang.Strings;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 客户端工具类
 *
 * @author cangling
 */
public class ServletUtils {
    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        String parameter =getParameter(name);
        if(Strings.isBlank(parameter))
        {
            return defaultValue;
        }
        return Castors.me().castTo(defaultValue, String.class);
    }


    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        return getParameterToInt(name,0);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        String parameter =getParameter(name);
        if(Strings.isBlank(parameter))
        {
            return defaultValue;
        }
        return Castors.me().castTo(defaultValue, Integer.class);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession(true);
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readToken()
    {
        return readHeader("Authorization");
    }

    public static String readHeader(String headerKey) {
        String header = getRequest().getHeader(headerKey);

        if (Strings.isNotBlank(header) && header.startsWith(CommonConstant.TOKEN_PREFIX)) {
            return header.replace(CommonConstant.TOKEN_PREFIX, "");
        } else {
            return "";
        }
    }
}
