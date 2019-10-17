package cn.shaines.filesystem.repository;

import cn.shaines.filesystem.entity.File;
import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

/**
 * File存储库.
 *
 * @author houyu
 * @createTime 2019/3/9 21:47
 */
public interface FileRepository extends JpaRepository<File, String> {

    File findByName(String name);

    @Modifying
    @Transactional
    int deleteAllByIdIn(String[] ids);

    @Modifying
    @Transactional
    int deleteAllByNameIn(String[] names);

    Page<File> findAllByNameIsContaining(String name, Pageable pageable);
}
