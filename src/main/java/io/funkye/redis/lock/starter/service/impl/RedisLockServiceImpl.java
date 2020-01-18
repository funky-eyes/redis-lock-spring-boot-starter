package io.funkye.redis.lock.starter.service.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import io.funkye.redis.lock.starter.service.IRedisLockService;

/**
 * -redis服务实现
 *
 * @author Zhang Kangkang
 * @version 1.0.0
 */
@DependsOn({"redisLockTemplate"})
@Service("redisLockService")
public class RedisLockServiceImpl<K, V> implements IRedisLockService<K, V> {

    @Autowired
    private RedisTemplate<K, V> redisLockTemplate;

    @Override
    public void set(K key, V value, Duration timeout) {
        redisLockTemplate.opsForValue().set(key, value, timeout);
    }

    @Override
    public Boolean delete(K key) {
        return redisLockTemplate.delete(key);
    }

    @Override
    public V get(K key) {
        return redisLockTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean setIfAbsent(K key, V value, Duration timeout) {
        return redisLockTemplate.opsForValue().setIfAbsent(key, value, timeout);
    }

}
