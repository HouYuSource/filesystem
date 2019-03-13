package cn.shaines.filesystem.exception;

/**
 * @author houyu
 * @createTime 2019/3/11 15:46
 */
public class BusinessException extends Exception {

    public BusinessException(Exception e) {
        super(e);
    }

    public BusinessException(String msg, Exception e) {
        super(msg, e);
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
