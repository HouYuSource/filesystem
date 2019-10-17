package cn.shaines.filesystem.service;

import cn.shaines.filesystem.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @description
 * @date 2019-10-17 20:26:20
 * @author houyu for.houyu@foxmail.com
 */
public interface FileService {

    /**
     * 保存文件
     * @return
     */
    File save(File file);

    /**
     * 删除文件
     * @return
     */
    void deleteById(String id);

    /**
     * 根据name获取文件
     * @return
     */
    File findByName(String name);

    /**
     * 根据names 批量删除
     * @param names
     * @return
     */
    int deleteAllByNameIn(String[] names);

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    Page<File> findAll(Pageable pageable);

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    Page<File> findAllByNameIsContaining(String name, Pageable pageable);

}
