package cn.shaines.filesystem.repository;

import cn.shaines.filesystem.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Log存储库.
 *
 * @author houyu
 * @createTime 2019/3/9 21:47
 */
public interface LogRepository extends JpaRepository<Log, String> {


    Page<Log> findAllByUriIsContainingOrParamsIsContaining(String uri, String params, Pageable pageable);

}
