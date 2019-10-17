package cn.shaines.filesystem.helper;

import java.io.IOException;

/**
 * @description 文件存储接口
 * @date 2019-10-17 20:43:26
 * @author houyu for.houyu@foxmail.com
 */
public interface FileHelper {

    boolean save(String key, byte[] body) throws IOException;

    byte[] findByKey(String key) throws IOException;

    int deleteAllByKeys(String[] keys) throws IOException;

}
