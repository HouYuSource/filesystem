package cn.shaines.filesystem.helper;

import cn.shaines.filesystem.util.QiNiuUtil;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * 七牛云文件帮助类
 * @author houyu
 * @createTime 2019/10/17 20:50
 */
@Component
public class QiNiuFileHelper implements FileHelper {

    @Override
    public boolean save(String key, byte[] body) throws IOException {
        QiNiuUtil.upload(key, body);
        return true;
    }

    @Override
    public byte[] findByKey(String key) throws IOException {
        return QiNiuUtil.findByKey(key);
    }

    @Override
    public int deleteAllByKeys(String[] keys) throws IOException {
        return QiNiuUtil.delete(keys);
    }
}
