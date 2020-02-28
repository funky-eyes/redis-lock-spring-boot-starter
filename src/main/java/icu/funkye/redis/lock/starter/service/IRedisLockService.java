package icu.funkye.redis.lock.starter.service;

import java.time.Duration;

/**
 * redis分布式锁实现
 * 
 * @author funkye
 * @version 1.0.0
 */
public interface IRedisLockService<K, V> {

    /**
     * -分布式锁实现,只有锁的key不存在才会返回true
     */
    public Boolean setIfAbsent(K key, V value, Duration timeout);

    void set(K key, V value, Duration timeout);

    Boolean delete(K key);

    V get(K key);
}
