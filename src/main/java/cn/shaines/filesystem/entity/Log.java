package cn.shaines.filesystem.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 来访实体
 *
 * @author houyu
 * @createTime 2019/3/9 21:54
 */
@Entity
public class Log {

    /** 主键 */
    @Id
    private String id;
    /** 访问ip */
    @Column(length=20)
    private String ip;
    /** 访问时间 */
    private Date date;
    /** 请求uri */
    @Column(length=256)
    private String uri;
    /** 请求参数 */
    @Column(length=512)
    private String params;
    /** 结果 */
    @Column(length=256)
    private String result;

    public String getId() {
        return id;
    }

    public Log setId(String id) {
        this.id = id;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Log setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Log setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public Log setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getParams() {
        return params;
    }

    public Log setParams(String params) {
        this.params = params;
        return this;
    }

    public String getResult() {
        return result;
    }

    public Log setResult(String result) {
        this.result = result;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Log{");
        sb.append("id='").append(id).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", date=").append(date);
        sb.append(", uri='").append(uri).append('\'');
        sb.append(", params='").append(params).append('\'');
        sb.append(", result='").append(result).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
