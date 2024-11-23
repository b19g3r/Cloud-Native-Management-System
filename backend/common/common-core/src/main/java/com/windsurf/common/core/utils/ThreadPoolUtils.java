package com.windsurf.common.core.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工具类
 */
@Slf4j
public class ThreadPoolUtils {
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    /**
     * 默认线程池
     */
    private static final ExecutorService DEFAULT_EXECUTOR = createThreadPool(
            "default-pool-%d",
            Runtime.getRuntime().availableProcessors() * 2,
            200,
            10000
    );

    /**
     * 创建线程池
     *
     * @param nameFormat 线程名格式
     * @param coreSize  核心线程数
     * @param maxSize   最大线程数
     * @param queueSize 队列大小
     * @return 线程池
     */
    public static ExecutorService createThreadPool(String nameFormat, int coreSize, int maxSize, int queueSize) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat(nameFormat)
                .setUncaughtExceptionHandler((thread, throwable) ->
                        log.error("Thread {} threw exception: ", thread.getName(), throwable))
                .build();

        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * 获取或创建线程池
     *
     * @param poolName  线程池名称
     * @param coreSize  核心线程数
     * @param maxSize   最大线程数
     * @param queueSize 队列大小
     * @return 线程池
     */
    public static ExecutorService getOrCreateThreadPool(String poolName, int coreSize, int maxSize, int queueSize) {
        return THREAD_POOLS.computeIfAbsent(poolName,
                key -> createThreadPool(key + "-%d", coreSize, maxSize, queueSize));
    }

    /**
     * 获取默认线程池
     */
    public static ExecutorService getDefaultExecutor() {
        return DEFAULT_EXECUTOR;
    }

    /**
     * 关闭线程池
     *
     * @param executor     线程池
     * @param timeout      超时时间
     * @param timeUnit     时间单位
     * @param isNowClose   是否立即关闭
     */
    public static void shutdown(ExecutorService executor, long timeout, TimeUnit timeUnit, boolean isNowClose) {
        if (executor == null || executor.isShutdown()) {
            return;
        }

        try {
            if (isNowClose) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
            }

            if (!executor.awaitTermination(timeout, timeUnit)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 关闭所有线程池
     */
    public static void shutdownAll() {
        log.info("Shutting down all thread pools...");
        THREAD_POOLS.forEach((name, executor) -> {
            log.info("Shutting down thread pool: {}", name);
            shutdown(executor, 5, TimeUnit.SECONDS, false);
        });
        shutdown(DEFAULT_EXECUTOR, 5, TimeUnit.SECONDS, false);
    }
}
