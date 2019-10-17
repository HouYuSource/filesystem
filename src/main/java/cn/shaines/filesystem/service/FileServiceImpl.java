package cn.shaines.filesystem.service;

import cn.shaines.filesystem.entity.File;
import cn.shaines.filesystem.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author houyu
 * @createTime 2019/3/9 21:49
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public File save(File file) {
        return fileRepository.save(file);
    }

    @Override
    public void deleteById(String id) {
        fileRepository.deleteById(id);
    }

    @Override
    public File findByName(String name) {
        return fileRepository.findByName(name);
    }

    @Override
    public int deleteAllByNameIn(String[] names) {
        return fileRepository.deleteAllByNameIn(names);
    }

    @Override
    public Page<File> findAll(Pageable pageable) {
        return fileRepository.findAll(pageable);
    }

    @Override
    public Page<File> findAllByNameIsContaining(String name, Pageable pageable) {
        return fileRepository.findAllByNameIsContaining(name, pageable);
    }
}
