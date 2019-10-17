package cn.shaines.filesystem.service;

import cn.shaines.filesystem.entity.Log;
import cn.shaines.filesystem.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author houyu
 * @createTime 2019/3/9 21:49
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository LogRepository;

    @Override
    public Log save(Log log) {
        return LogRepository.save(log);
    }

    @Override
    public Page<Log> findAll(Pageable pageable) {
        return LogRepository.findAll(pageable);
    }

    @Override
    public Page<Log> findAllByUriIsContainingOrParamsIsContaining(String uri, String params, Pageable pageable) {
        return LogRepository.findAllByUriIsContainingOrParamsIsContaining(uri, params, pageable);
    }

}
