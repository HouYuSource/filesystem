package cn.shaines.filesystem.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: loading-blog
 * @description: 线程工具类
 * @author: houyu
 * @create: 2018-12-25 00:47
 */
public class ThreadPoolUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);

    /** 线程刷新时间Map */
    private ConcurrentMap<String, Long>         threadRefreshTimeMap;
    /** 线程对应的操作对象 */
    private Map<String, Future<?>>              threadFutureMap;
    /** 缓存线程池 */
    private ExecutorService                     cachedThreadPool;
    /** 缓存线程池最后刷新时间 */
    private volatile long                       poolRefreshTime;
    /** 监听线程是否需要监听,一开始默认是需要监听 */
    private volatile boolean                    needMonitorWorkerFlag = true;
    /** 监听池 */
    private ScheduledExecutorService            monitorExecutorPool;
    /** 线程超时自动关闭(60秒) */
    private final long                          threadTimeOver = 60000L;
    /** 池子超时自动关闭(60秒) */
    private final long                          poolTimeOver = 60000L;

    /**
     * 提交线程
     */
    public Future<?> submit(Runnable task){
        // 初始化:运行监听线程
        this.runMonitorWorkerTask();
        // 创建线程的唯一id
        String id = UUID.randomUUID().toString();
        // 最新刷新时间
        long timeMillis = System.currentTimeMillis();
        // 更新缓存池刷新时间
        this.poolRefreshTime = timeMillis;
        // 提交线程
        Future<?> future = cachedThreadPool.submit(task);
        // 存储提交的线程刷新时间
        threadRefreshTimeMap.put(id, timeMillis);
        // 存储提交的线程执行对象
        threadFutureMap.put(id, future);
        logger.debug("提交执行线程{}", id);
        return future;
    }

    /**
     * 关闭线程池
     */
    public void shutdown(){
        this.monitorExecutorPool.shutdown();
        this.cachedThreadPool.shutdown();
        logger.debug("关闭线程池");
    }

    /**
     * 创建监控线程
     */
    private Runnable monitorWorker = () -> {
        try {
            logger.debug("监控线程轮询开始, 发现线程池有线程数量:{}", threadRefreshTimeMap.size());
            List<String> removeIdList = new ArrayList<>();
            String threadId;
            for (Map.Entry<String, Long> entry : threadRefreshTimeMap.entrySet()) {
                threadId = entry.getKey();
                if (threadFutureMap.get(threadId).isDone()){
                    // 线程已完成工作,正在待命,newCachedThreadPool机制:如果60秒内无任务,会干掉该线程
                    logger.debug("发现闲时线程,(该线程已完成任务,无需要监听)线程id:{}", threadId);
                    removeIdList.add(threadId);
                }else if (System.currentTimeMillis() - entry.getValue() > threadTimeOver) {
                    // 线程未完成任务,有可能阻塞了,这里我们需要手动干掉该线程,否则有可能是造成资源的浪费
                    logger.debug("发现超时线程,(该线程超时还没完成任务,需要关闭线程)线程id:{}" + threadId);
                    threadFutureMap.get(threadId).cancel(true);
                    removeIdList.add(threadId);
                }
            }
            for (String id : removeIdList) {
                threadFutureMap.remove(id);
                threadRefreshTimeMap.remove(id);
            }
            logger.debug("--监控线程结束, 线程池还剩下线程数量:{}", threadRefreshTimeMap.size());
            if (threadRefreshTimeMap.size() != 0){
                poolRefreshTime = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - poolRefreshTime > poolTimeOver){
                logger.debug("终止监听池子");
                this.shutdown();
                // 需要监听标志设置为true, 下次运行时开启监听
                needMonitorWorkerFlag = true;
            }
        } catch (Exception e) {
            logger.warn("监听线程池线程出现异常", e);
        }
    };

    /**
     * 开启监控线程
     */
    public void runMonitorWorkerTask() {
        if (needMonitorWorkerFlag){
            synchronized (ThreadPoolUtil.class){
                if(!needMonitorWorkerFlag) {
                    return;
                }

                class DefaultThreadFactory implements ThreadFactory {
                    private final AtomicInteger poolNumber = new AtomicInteger(1);
                    private final ThreadGroup group;
                    private final AtomicInteger threadNumber = new AtomicInteger(1);
                    private final String namePrefix;

                    DefaultThreadFactory() {
                        SecurityManager s = System.getSecurityManager();
                        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
                        namePrefix = "business-unified-pool-" + poolNumber.getAndIncrement() + "-thread-";
                    }

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
                        if (t.isDaemon()) {
                            t.setDaemon(false);
                        }
                        if (t.getPriority() != Thread.NORM_PRIORITY) {
                            t.setPriority(Thread.NORM_PRIORITY);
                        }
                        return t;
                    }
                }

                threadFutureMap         = new HashMap<>(16);
                threadRefreshTimeMap    = new ConcurrentHashMap<>(16);
                // cachedThreadPool        = Executors.newCachedThreadPool();
                // cachedThreadPool        = new ThreadPoolExecutor(0, 300, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
                // ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("custom-thread-pool-util-%d").build();
                //
                cachedThreadPool        = new ThreadPoolExecutor(50, 100, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new DefaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                /* monitorExecutorPool     = Executors.newScheduledThreadPool(1); */
                monitorExecutorPool     = new ScheduledThreadPoolExecutor(1);
                poolRefreshTime         = System.currentTimeMillis();
                monitorExecutorPool.scheduleAtFixedRate(monitorWorker, 0, 1, TimeUnit.SECONDS);
                needMonitorWorkerFlag = false;
            }
        }
    }

    /* ---------------------------------------单例模式---------------------------------------*/

    private ThreadPoolUtil() {}

    private interface SingletonHolder {
        ThreadPoolUtil INSTANCE = new ThreadPoolUtil();
    }

    public static ThreadPoolUtil get() {
        return SingletonHolder.INSTANCE;
    }
    /* ---------------------------------------单例模式---------------------------------------*/

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 200; i++) {
            int finalI = i;
            ThreadPoolUtil.get().submit(() -> {
                System.out.println(finalI);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

// // 测试
// //private final Long                          threadTimeOver    = 3000L;       // 线程超时自动关闭(3秒)线程最多工作3秒, 3秒未完成工作的强制停止
// ThreadPoolUtil threadPoolUtil = ThreadPoolUtil.get();
// threadPoolUtil.runMonitorWorkerTask();
// List<Long> timeList = Arrays.asList(4000L, 2000L, 30000L, 4000L, 5000L, 6000L, 7000L, 8000L, 9000L);
// for(int i = 0; i < 1; i++){
//     threadPoolUtil.submit(() -> {
//         System.err.println(2000 + ":线程开始工作了");
//         try {
//             Thread.sleep(2000);
//         } catch (InterruptedException e) {
//             System.err.println("线程被打破!(强制通知工作)");
//         }
//         System.err.println(2000 + ":---线程结束工作了");
//     });
//     Thread.sleep(3000);
// }
//
// Thread.sleep(15000);
// threadPoolUtil.submit(() -> {
//     System.err.println(5000 + ":线程开始工作了");
//     try {
//         Thread.sleep(5000);
//     } catch (InterruptedException e) {
//         System.err.println("线程被打破!(强制通知工作)");
//     }
//     System.err.println(5000 + ":---线程结束工作了");
// });




//        // 阻塞队列线程池的使用: ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
//        /** 阻塞队列, 最大值是200, 添加超过这个最大值就报错 */
//        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(200);
//        /** 线程池, 核心运行最大线程数量是2, 最大的线程池数量是4, 存活时间5分钟 */
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 5, TimeUnit.MINUTES, queue);
//        for (int i = 1; i < 201; i++) {
//            executor.execute(new MyRun(i));
//        }
//        executor.shutdown();