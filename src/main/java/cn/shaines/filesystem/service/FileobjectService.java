package cn.shaines.filesystem.service;

import cn.shaines.filesystem.entity.Fileobject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FileobjectService {

    /**
     * 保存文件
     * @return
     */
    Fileobject save(Fileobject file);

    /**
     * 删除文件
     * @return
     */
    void deleteById(String id);

    /**
     * 根据id获取文件
     * @return
     */
    Optional<Fileobject> findById(String id);

    /**
     * 根据name获取文件
     * @return
     */
    Fileobject findByName(String name);

    /**
     * 分页查询，按上传时间降序
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<Fileobject> findByIdToPage(int pageIndex, int pageSize);

    /**
     * 根据ids 批量删除
     * @param ids
     * @return
     */
    int deleteAllByIdIn(String[] ids);


    /**
     * 根据names 批量删除
     * @param 根据names
     * @return
     */
    int deleteAllByNameIn(String[] 根据names);


    /**
     * 分页查询
     * @param pageable
     * @return
     */
    public Page<Fileobject> findAll(Pageable pageable);


    /**
     * 分页查询
     * @param pageable
     * @return
     */
    public Page<Fileobject> findAllByNameIsContaining(String name, Pageable pageable);

}
