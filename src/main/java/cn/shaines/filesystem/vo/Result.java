package cn.shaines.filesystem.vo;

/**
 * 返回结果集
 * @author houyu
 * @createTime 2019/3/10 1:06
 */
public class Result {

    // -------------------------------------------------------------------------------------------------- //

    public static final Result SUCCESS = new Result().setStatus(Status.SUCCESS).setMsg("操作成功").setData(null);

    public static final Result ERROR = new Result().setStatus(Status.ERROR).setMsg("操作失败").setData(null);

    public static Result success(String msg, Object data){
        return new Result().setStatus(Status.SUCCESS).setMsg(msg).setData(data);
    }

    public static Result error(String msg, Object data){
        return new Result().setStatus(Status.ERROR).setMsg(msg).setData(data);
    }

    public static Result newInstance(int status, String msg, Object data){
        return new Result().setStatus(status).setMsg(msg).setData(data);
    }

    // -------------------------------------------------------------------------------------------------- //

    private int status;

    private String msg;

    private Object data;

    public int getStatus() {
        return status;
    }

    public Result setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public class Status{
        public static final int SUCCESS         = 200;
        public static final int ERROR           = 400;
        public static final int SERVER_ERROR    = 500;
    }
}
