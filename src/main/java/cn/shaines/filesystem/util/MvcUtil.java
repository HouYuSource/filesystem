package cn.shaines.filesystem.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: data-interface
 * @description:
 * @author: houyu
 * @create: 2018-12-06 16:16
 */
public class MvcUtil {

    private CommonUtil commonUtil = CommonUtil.get();

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @return
     */
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return (ip == null || ip.length() == 0 ) ? request.getRemoteAddr() : ip;
    }


    /**
     * 返回数据
     * @throws IOException
     */
    public void responseReturnData(HttpServletResponse response, String data) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter writer = response.getWriter();
        writer.write(data == null ? "" : data);
        writer.flush();
        writer.close();
        writer = null;
    }

    public String getTokenFromRequest(HttpServletRequest request, String key) {
        String token = request.getHeader(key);                                  // Authorization
        if (token == null || token.length() == 0) {                             // 请求头没有token
            token = request.getParameter(key);                                  // 尝试去请求体(POST form or url param)获取token
            if (token == null || token.length() == 0) {
                Cookie[] cookies = request.getCookies();
                if (null != cookies) {
                    for (Cookie cookie : cookies) {
                        if (key.equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
//                if ((token == null || token.length() == 0)) { // 尝试去json实体中获取token,这里会报错,因为流被读取了,流关闭,解决方案是Filter中包装一层HttpServletRequestWrapper的实例对象内置一个byte[]
//                    try {
//                        token = new ObjectMapper().readValue(request.getInputStream(), HashMap.class).get(key).toString();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        token = "";
//                    }
//                }
            }
        }
        return token;
    }

    /**
     * 解析 request对象中的参数, 支持GET, POST(FORM|JSON) ...
     * 说明,如果是数组,会转成   ,  分割的字符串
     * @param request
     * @return
     * @throws IOException
     */
    public Map<String, String> parseParam(HttpServletRequest request) throws IOException {
        Map<String, String> paramMap = paramMap = new HashMap<>(8);
        if (request.getMethod().equalsIgnoreCase("POST") && request.getContentType().contains("application/json")){
            String json = IoUtil.toString(request.getInputStream(), "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(json);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                Object valueObj = entry.getValue();
                if (valueObj instanceof JSONArray) {
                    JSONArray valueArray = (JSONArray) valueObj;
                    paramMap.put(entry.getKey(), commonUtil.join(valueArray.toArray(), ","));
                }else {
                    paramMap.put(entry.getKey(), String.valueOf(valueObj));
                }
            }
        }else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                paramMap.put(entry.getKey(), commonUtil.join(entry.getValue(), ","));
            }
        }
        return paramMap;
    }

    /* ---------------------------------------单例模式---------------------------------------*/
    private static class SingletonHolder {
        private static final MvcUtil INSTANCE = new MvcUtil();
    }

    public static MvcUtil get() {
        return SingletonHolder.INSTANCE;
    }
    /* ---------------------------------------单例模式---------------------------------------*/

}
