package io.funkye.redis.lock.starter.aspect;

import java.time.Duration;

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

import io.funkye.redis.lock.starter.config.annotation.RedisLock;
import io.funkye.redis.lock.starter.service.IRedisLockService;

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

    @Pointcut("@annotation(io.funkye.redis.lock.starter.config.annotation.RedisLock)")
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
        while (true) {
            if (redisLockService.setIfAbsent(key, "0", Duration.ofMillis(annotation.lockTimeout()))) {
                LOGGER.info("########## 得到锁:{} ##########", key);
                break;
            }
            if (System.currentTimeMillis() - startTime > annotation.timeoutMills()) {
                throw new RuntimeException("尝试获得分布式锁超时..........");
            }
            LOGGER.info("########## 尝试获取锁:{} ##########", key);
            Thread.sleep(annotation.retry());
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            LOGGER.error("出现异常:{}", e.getMessage());
            throw e;
        } finally {
            redisLockService.delete(key);
            LOGGER.info("########## 释放锁:{},总耗时:{}ms,{} ##########", key, (System.currentTimeMillis() - startTime));
        }

    }
}
