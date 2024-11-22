package com.windsurf.common.redis.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Component
public class RedisLockUtil {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取锁
     *
     * @param lockKey 锁实例key
     * @return 锁信息
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 加锁
     *
     * @param lockKey 锁实例key
     * @return 是否成功
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, 0, -1);
    }

    /**
     * 加锁
     *
     * @param lockKey   锁实例key
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return 是否成功
     */
    public boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 加锁
     *
     * @param lockKey 锁实例key
     */
    public void lock(String lockKey) {
        lock(lockKey, -1);
    }

    /**
     * 加锁
     *
     * @param lockKey   锁实例key
     * @param leaseTime 上锁后自动释放锁时间
     */
    public void lock(String lockKey, int leaseTime) {
        RLock lock = getLock(lockKey);
        if (leaseTime > 0) {
            lock.lock(leaseTime, TimeUnit.SECONDS);
        } else {
            lock.lock();
        }
    }

    /**
     * 解锁
     *
     * @param lockKey 锁实例key
     */
    public void unlock(String lockKey) {
        RLock lock = getLock(lockKey);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 解锁
     *
     * @param lock 锁信息
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
