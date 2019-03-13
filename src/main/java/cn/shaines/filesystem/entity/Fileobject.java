package cn.shaines.filesystem.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author houyu
 * @createTime 2019/3/9 21:42
 */
@Entity
public class Fileobject {

    @Id
    @Column(length=30)
    private String id;

    // 文件名称
    @Column(length=256)
    private String name;

    // 文件类型
    @Column(length=100)
    private String type;

    // 文件大小
    private long size;

    // 上传时间
    private Date date;

    @Column(length=256)
    private String mapping;

    // 临时字段,不入库
    @Transient
    private String dateString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public String toString() {
        return "Fileobject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", date=" + date +
                ", mapping='" + mapping + '\'' +
                ", dateString='" + dateString + '\'' +
                '}';
    }
}
