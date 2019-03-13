package cn.shaines.filesystem.repository;

import cn.shaines.filesystem.entity.Visitobject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Visitobject存储库.
 *
 * @author houyu
 * @createTime 2019/3/9 21:47
 */
public interface VisitobjectRepository extends JpaRepository<Visitobject, String> {


    Page<Visitobject> findAllByUriIsContainingOrParamsIsContaining(String uri, String params, Pageable pageable);

}
