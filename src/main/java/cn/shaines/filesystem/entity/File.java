package cn.shaines.filesystem.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @author houyu
 * @createTime 2019/3/9 21:42
 */
@Entity
public class File {

    @Id
    @Column(length = 30)
    private String id;
    /** 文件名称 */
    @Column(length = 256)
    private String name;
    /** 文件类型 */
    @Column(length = 100)
    private String type;
    /** 文件大小 */
    private Integer size;
    /** 上传时间 */
    private Date date;
    /** 映射 */
    @Column(length = 256)
    private String mapping;

    /** 文件内容 */
    @Transient
    private transient byte[] body;

    public String getId() {
        return id;
    }

    public File setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public File setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public File setType(String type) {
        this.type = type;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public File setSize(Integer size) {
        this.size = size;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public File setDate(Date date) {
        this.date = date;
        return this;
    }

    public String getMapping() {
        return mapping;
    }

    public File setMapping(String mapping) {
        this.mapping = mapping;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public File setBody(byte[] body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("File{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", size=").append(size);
        sb.append(", date=").append(date);
        sb.append(", mapping='").append(mapping).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
