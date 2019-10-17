package cn.shaines.filesystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

/**
 * @description:
 * @author: houyu
 * @create: 2018-12-06 16:16
 */
public class MvcUtil {


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
     * 解析 request对象中的参数, 支持GET, POST(FORM|JSON) ...
     * 说明,如果是数组,会转成   ,  分割的字符串
     * @param request
     * @return
     * @throws IOException
     */
    public Map<String, String> parseParam(HttpServletRequest request) throws IOException {
        Map<String, String> paramMap = new HashMap<>(8);
        if (String.valueOf(request.getContentType()).contains("application/json")){
            String json = IOUtil.toString(request.getInputStream(), Charset.defaultCharset());
            JSONObject jsonObject = JSON.parseObject(json);
            if (jsonObject != null) {
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    Object valueObj = entry.getValue();
                    if (valueObj instanceof JSONArray) {
                        JSONArray valueArray = (JSONArray) valueObj;
                        paramMap.put(entry.getKey(), CoreUtil.join(valueArray.toArray(), ","));
                    }else {
                        paramMap.put(entry.getKey(), String.valueOf(valueObj));
                    }
                }
            }
        }else {
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                paramMap.put(entry.getKey(), CoreUtil.join(entry.getValue(), ","));
            }
        }
        return paramMap;
    }

    /**
     * 返回数据
     * @throws IOException
     */
    public static void returnData(HttpServletResponse response, String data) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter writer = response.getWriter();
        writer.write(data == null ? "" : data);
        writer.flush();
        writer.close();
    }

    /**
     * 下载数据
     */
    public static void downloadData(HttpServletResponse response, byte[] data, String fileName) {
        try{
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            handleData(response, data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 查看数据
     */
    public static void viewData(HttpServletResponse response, byte[] data, String fileName) {
        try{
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "fileName=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            handleData(response, data);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /** 处理数据 */
    private static void handleData(HttpServletResponse response, byte[] data) throws IOException{
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, data.length + "");
        response.setHeader(HttpHeaders.CONNECTION, "close");
        try(ServletOutputStream outputStream = response.getOutputStream()) {
            // 使用jdk1.7 try resource自动关闭流
            outputStream.write(data);
            outputStream.flush();
        }
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