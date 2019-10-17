package cn.shaines.filesystem.interceptor;

import cn.shaines.filesystem.annotation.ChainRequired;
import cn.shaines.filesystem.exception.BusinessException;
import cn.shaines.filesystem.service.LogService;
import cn.shaines.filesystem.util.IdWorker;
import cn.shaines.filesystem.util.MvcUtil;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author houyu
 * @createTime 2019/3/9 22:58
 */
@Component
public class ChainInterceptor implements HandlerInterceptor {


    @Value("${hostname}")
    private String hostname;

    private MvcUtil mvcUtil = MvcUtil.get();

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private LogService logService;

    /**
     * controller 执行之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 盗链处理
        String referer = request.getHeader("Referer");
        if (ChainHandle(method, referer)) { return true; }

        return true;
    }

    /**
     * 处理道理
     */
    private boolean ChainHandle(Method method, String referer) throws BusinessException {
        // 判断方法是否需要检查盗链问题
        ChainRequired chainRequired = method.getAnnotation(ChainRequired.class);

        if (chainRequired == null){ return true; }

        // 如果有 @ChainRequired 注解，需要检查
        if (chainRequired.value().equals(ChainRequired.Type.CHECK)) {
            // 检查
            if (referer == null) {
                throw new BusinessException("不可以盗链");
                // return false;
            } else if (referer.contains(hostname) || referer.contains("shaines.cn")) {
                // shaines.cn  设置为白名单
                return true;
            } else {
                throw new BusinessException("不可以盗链");
                // return false;
            }
        }
        return false;
    }

}
