package icu.funkye.redis.lock.starter.aspect;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import icu.funkye.redis.lock.starter.config.annotation.RedisLock;
import icu.funkye.redis.lock.starter.service.IRedisLockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * -动态拦截分布式锁
 *
 * @author chenjianbin
 * @version 1.0.0
 */
@DependsOn({"redisLockService"})
@Aspect
@Component
public class RedisClusterLockAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterLockAspect.class);

    @Autowired
    private IRedisLockService<String, String> redisLockService;

    @Pointcut("@annotation(icu.funkye.redis.lock.starter.config.annotation.RedisLock)")
    public void annotationPoinCut() {}

    @Around("annotationPoinCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        RedisLock annotation = signature.getMethod().getAnnotation(RedisLock.class);
        String key = annotation.key();
        if (key == null || key.length() <= 0) {
            key = joinPoint.getTarget().getClass().getName() + signature.getName();
        }
        Long startTime = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        Integer spin = annotation.spin();
        while (!lock(key, uuid, annotation)) {
            if (spin > 0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("########## 尝试自旋获取锁:{},剩余次数:{} ##########", key, spin);
                }
                if (lock(key, uuid, annotation)) {
                    break;
                }
                spin--;
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("########## 尝试获取锁:{} ##########", key);
                }
                Thread.sleep(annotation.retry());
            }
            if (System.currentTimeMillis() - startTime > annotation.timeoutMills()) {
                throw new RuntimeException("尝试获得分布式锁超时..........");
            }
        }
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("########## {}得到锁:{} ##########",
                    spin < annotation.spin() ? spin > 0 ? "自旋获取" : "重量级获取" : "", key);
            }
            return joinPoint.proceed();
        } catch (Throwable e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("出现异常:{}", e.getMessage());
            }
            throw e;
        } finally {
            String owner = redisLockService.get(key);
            if (uuid.equals(owner)) {
                redisLockService.delete(key);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("########## 释放锁:{},总耗时:{}ms,{} ##########", key,
                        (System.currentTimeMillis() - startTime));
                }
            }
        }
    }

    public boolean lock(String key, String uuid, RedisLock redisLock) {
        return redisLockService.setIfAbsent(key, uuid, Duration.ofMillis(redisLock.lockTimeout()));
    }
}
