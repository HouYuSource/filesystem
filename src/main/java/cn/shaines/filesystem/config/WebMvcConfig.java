package cn.shaines.filesystem.config;

import cn.shaines.filesystem.interceptor.BaseInterceptor;
import cn.shaines.filesystem.interceptor.ChainInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置类
 * @author houyu
 * @createTime 2019/3/10 17:56
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private BaseInterceptor baseInterceptor;
    @Autowired
    private ChainInterceptor chainInterceptor;

    private String[] baseExcludePaths = {"/resources/**", "/static/**", "/error/**"};

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加基础拦截器(日志)
        registry.addInterceptor(baseInterceptor).addPathPatterns("/**").excludePathPatterns(baseExcludePaths);
        // 添加链路拦截器(防盗链)
        registry.addInterceptor(chainInterceptor).addPathPatterns("/file/**");
    }

    /**
     * 配置静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

}
