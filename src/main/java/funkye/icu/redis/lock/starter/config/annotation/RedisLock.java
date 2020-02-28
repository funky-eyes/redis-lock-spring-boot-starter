package funkye.icu.redis.lock.starter.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    /**
     * -锁值,默认为类全路径名+方法名
     */
    String key() default "";

    /**
     * -单位毫米,默认60秒后直接跳出
     */
    int timeoutMills() default 60000;

    /**
     * -尝试获取锁频率
     */
    int retry() default 50;

    /**
     * -锁过期时间
     */
    int lockTimeout() default 60000;
}
