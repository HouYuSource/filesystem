package cn.shaines.filesystem.service;

import cn.shaines.filesystem.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @description
 * @date 2019-10-17 20:27:23
 * @author houyu for.houyu@foxmail.com
 */
public interface LogService {

    /**
     * 保存
     */
    Log save(Log log);

    /**
     * 分页查询
     * @param pageable
     */
    Page<Log> findAll(Pageable pageable);

    /**
     * 分页查询
     * @param pageable
     */
    Page<Log> findAllByUriIsContainingOrParamsIsContaining(String uri, String params, Pageable pageable);

}
