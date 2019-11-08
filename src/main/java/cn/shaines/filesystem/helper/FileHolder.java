package cn.shaines.filesystem.helper;

import cn.shaines.filesystem.util.ThreadPoolUtil;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 文件持有者 (采用观察者模式)
 *
 * 同步发布消息处理消息
 *
 * @author houyu
 * @createTime 2019/11/8 14:37
 */
// @Slf4j
@Component
public class FileHolder implements FileHelper {

    private static final Logger log = LoggerFactory.getLogger(FileHolder.class);

    @Value("${handle.file.helper}")
    private String fileHelpers;
    @Autowired
    private ApplicationContext applicationContext;

    private List<FileHelper> fileHelperList = new CopyOnWriteArrayList<>();

    /**
     * 使用 @PostConstruct 完成对FileHolder的fileHelperList初始化
     */
    @PostConstruct
    public void initMethod(){
        Set<String> names = Stream.of(fileHelpers.split(",")).map(String::trim).collect(Collectors.toSet());
        for(String beanName : names) {
            FileHelper bean = (FileHelper) applicationContext.getBean(beanName);
            addFileHelper(bean);
        }
    }

    private void addFileHelper(FileHelper fileHelper) {
        fileHelperList.add(fileHelper);
    }

    @Override
    public boolean save(String key, byte[] body) {
        /**
         * 这里先不考虑中途没有同步更新的情况, 如果需要保证这些, 请在 catch 处理, 然后手动回滚的操作
         */
        runAsyncTask(v -> {
            try {
                v.save(key, body);
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public byte[] findByKey(String key) {
        int size = fileHelperList.size();
        for(int i = size - 1; i >= 0; i--) {
            int selectIndex = ThreadLocalRandom.current().nextInt(0, size);
            try {
                // 如果不报错, 那就直接返回即可, 如果报错, 那就尝试下一次的
                return fileHelperList.get(selectIndex).findByKey(key);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    @Override
    public int deleteAllByKeys(String[] keys) {
        /**
         * 这里先不考虑中途没有同步更新的情况, 如果需要保证这些, 请在 catch 处理, 然后手动回滚的操作
         */
        runAsyncTask(v -> {
            try {
                v.deleteAllByKeys(keys);
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        return 0;
    }

    public void runAsyncTask(Consumer<FileHelper> consumer) {
        CountDownLatch countDownLatch = new CountDownLatch(fileHelperList.size());
        for(FileHelper helper : fileHelperList) {
            ThreadPoolUtil.get().submit(() -> {
                try {
                    // () -> { 内部是同步, 总体for的外部是异步, 结合使用CountDownLatch 达到的效果就是多个任务同步执行, 任务与任务直接异步执行. }
                    consumer.accept(helper);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            // 这里任务等待15秒, 如果15秒没有完成就不等待了 (看具体业务)
            countDownLatch.await(15, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
