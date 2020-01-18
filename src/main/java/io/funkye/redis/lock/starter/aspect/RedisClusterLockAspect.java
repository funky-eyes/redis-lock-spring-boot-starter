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
    public void arround(ProceedingJoinPoint joinPoint) throws InterruptedException {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        RedisLock annotation = signature.getMethod().getAnnotation(RedisLock.class);
        if (annotation.key() == null || annotation.key().length() <= 0) {
            throw new RuntimeException("分布式锁key值不允许为:" + annotation.key());
        }
        Long startTime = System.currentTimeMillis();
        LOGGER.info("########## key:{},开始分布式上锁 ##########", annotation.key());
        while (true) {
            if (redisLockService.setIfAbsent(annotation.key(), "0", Duration.ofMillis(60000))) {
                LOGGER.info("########## 得到锁 ##########");
                break;
            }
            if (System.currentTimeMillis() - startTime > annotation.timeoutMills()) {
                throw new RuntimeException("尝试获得分布式锁超时..........");
            }
            LOGGER.info("########## 尝试获取锁 ##########");
            Thread.sleep(200);
        }
        try {
            Object o = joinPoint.proceed();
        } catch (Throwable e) {
            LOGGER.error("出现异常:{}", e.getMessage());
        } finally {
            redisLockService.delete(annotation.key());
            LOGGER.info("########## 释放锁:{},总耗时:{}ms ##########", annotation.key(),
                System.currentTimeMillis() - startTime);
        }
    }
}
