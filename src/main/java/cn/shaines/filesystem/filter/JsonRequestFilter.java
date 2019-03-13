package cn.shaines.filesystem.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author houyu
 * @createTime 2019/3/11 21:42
 */
@Component
@WebFilter(filterName = "crownFilter", urlPatterns = "/*")
public class JsonRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (servletRequest.getContentType() != null && servletRequest.getContentType().contains("application/json")){
            // 如果请求类型是json.那就使用封装的CustomCacheHttpServletRequestWrapper
            filterChain.doFilter(new CustomCacheHttpServletRequestWrapper((HttpServletRequest)servletRequest), servletResponse);
        }else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
