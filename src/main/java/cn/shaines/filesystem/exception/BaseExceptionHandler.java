package cn.shaines.filesystem.exception;

import cn.shaines.filesystem.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 统一异常处理类
 */
@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e){
        if (e instanceof BusinessException){
            // 业务异常直接反馈给用户
            return Result.newInstance(Result.Status.ERROR, e.getMessage(), null);
        }
        // 未知异常返回统一报错信息
        return Result.newInstance(Result.Status.SERVER_ERROR, "系统发生未知错误，请联系管理员", null);
    }
}
