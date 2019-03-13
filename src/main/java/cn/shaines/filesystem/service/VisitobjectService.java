package cn.shaines.filesystem.service;

import cn.shaines.filesystem.entity.Visitobject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitobjectService {

    /**
     * 保存
     * @return
     */
    Visitobject save(Visitobject visitobject);

    /**
     * 删除
     * @return
     */
    void deleteById(String id);

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    public Page<Visitobject> findAll(Pageable pageable);

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    Page<Visitobject> findAllByUriIsContainingOrParamsIsContaining(String uri, String params, Pageable pageable);

}
