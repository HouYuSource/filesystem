package cn.shaines.filesystem.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 来访实体
 *
 * @author houyu
 * @createTime 2019/3/9 21:54
 */
@Entity
public class Visitobject {

    // 主键
    @Id
    // @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // 访问ip
    @Column(length=20)
    private String ip;

    // 访问时间
    private Date date;

    //请求uri
    @Column(length=256)
    private String uri;

    // 请求参数
    @Column(length=512)
    private String params;

    // 结果
    @Column(length=256)
    private String result;

    // 临时字段,不入库
    @Transient
    private String dateString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public String toString() {
        return "Visitobject{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", date=" + date +
                ", uri='" + uri + '\'' +
                ", params='" + params + '\'' +
                ", result='" + result + '\'' +
                ", dateString='" + dateString + '\'' +
                '}';
    }
}
