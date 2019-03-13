package cn.shaines.filesystem.filter;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * @author houyu
 * @createTime 2019/3/11 21:31
 */
public class CustomCacheHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 存储requestBody byte[]
     */
    private final byte[] body;

    public CustomCacheHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        byte[] body = new byte[0];
        try {
            body = StreamUtils.copyToByteArray(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.body = body;
    }

    @Override
    public BufferedReader getReader() {
        ServletInputStream inputStream = getInputStream();
        return Objects.isNull(inputStream) ? null : new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public ServletInputStream getInputStream() {
        if (ObjectUtils.isEmpty(body)) {
            return null;
        }
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            @SuppressWarnings("EmptyMethod")
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = htmlEscape(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return htmlEscape(value);
    }

    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String) {
            htmlEscape((String) value);
        }
        return value;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return htmlEscape(value);
    }

    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }
        return htmlEscape(value);
    }

    /**
     * 使用spring HtmlUtils 转义html标签达到预防xss攻击效果
     *
     * @param str
     * @see org.springframework.web.util.HtmlUtils#htmlEscape
     */
    protected String htmlEscape(String str) {
        return HtmlUtils.htmlEscape(str);
    }

}
