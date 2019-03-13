package cn.shaines.filesystem.interceptor;

import cn.shaines.filesystem.entity.Visitobject;
import cn.shaines.filesystem.service.VisitobjectService;
import cn.shaines.filesystem.util.IdWorker;
import cn.shaines.filesystem.util.MvcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author houyu
 * @createTime 2019/3/9 22:58
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

    private MvcUtil mvcUtil = MvcUtil.get();

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private VisitobjectService visitobjectService;

    /**
     * controller 执行之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        // if(!(object instanceof HandlerMethod)){
        //     return true;
        // }
        // HandlerMethod handlerMethod=(HandlerMethod)object;
        // Method method=handlerMethod.getMethod();

        // 判断接口是否需要登录
        // LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
        // 如果有 @LoginRequired 注解，需要认证
        // if (methodAnnotation != null) {
        return true;
    }

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
        if(requestURI.contains("/visit/")) {
            return;
        }

        // 处理全局异常,放到exception.BaseExceptionHandler处理了
        //if(ex != null){
        //    Result error = Result.newInstance(Result.Status.SERVER_ERROR, ex.getMessage(), null);
        //    String msg = JSONObject.toJSONString(error, SerializerFeature.WriteMapNullValue);
        //    mvcUtil.responseReturnData(response, msg);
        //}

        Map<String, String> parseParam = mvcUtil.parseParam(request);

        Date date = new Date();

        Visitobject visitobject = new Visitobject();
        visitobject.setId(idWorker.nextId());
        visitobject.setIp(mvcUtil.getIpAddress(request));
        visitobject.setDate(date);
        visitobject.setUri(request.getRequestURI());
        visitobject.setParams(parseParam.toString());
        visitobject.setResult(ex == null ? "请求成功" : "请求失败:" + ex.getMessage());

        visitobjectService.save(visitobject);
    }


}
