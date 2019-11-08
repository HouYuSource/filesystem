package cn.shaines.filesystem.interceptor;

import cn.shaines.filesystem.entity.Log;
import cn.shaines.filesystem.service.LogService;
import cn.shaines.filesystem.util.IdWorker;
import cn.shaines.filesystem.util.MvcUtil;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author houyu
 * @createTime 2019/3/9 22:58
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private LogService logService;

    /**
     * controller 执行之后，且页面渲染之前调用
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 向客户端返项目的相对路径
        request.setAttribute("ctx", request.getContextPath());
        request.setAttribute("uri", request.getRequestURI());
    }

    /**
     * 页面渲染之后调用，一般用于资源清理操作
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        String requestURI = request.getRequestURI();
        if(requestURI.contains("/log/")) {
            return;
        }

        Log log = new Log();
        log.setId(idWorker.nextId() + "");
        log.setIp(MvcUtil.get().getIpAddress(request));
        log.setDate(new Date());
        log.setUri(request.getRequestURI());
        log.setParams(MvcUtil.get().parseParam(request).toString());
        log.setResult(ex == null ? "请求成功" : "请求失败:" + ex.getMessage());

        logService.save(log);
    }
}
