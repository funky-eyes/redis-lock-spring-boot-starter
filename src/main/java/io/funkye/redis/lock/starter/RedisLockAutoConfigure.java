package io.funkye.redis.lock.starter;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import io.funkye.redis.lock.starter.config.JedisLockProperties;
import redis.clients.jedis.Jedis;

@ComponentScan(basePackages = {"io.funkye.redis.lock.starter.config", "io.funkye.redis.lock.starter.service",
    "io.funkye.redis.lock.starter.aspect"})
@EnableConfigurationProperties({JedisLockProperties.class})
@ConditionalOnClass(Jedis.class)
@Configuration
public class RedisLockAutoConfigure {

    @Autowired
    private JedisLockProperties prop;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockAutoConfigure.class);

    @PostConstruct
    public void load() {
        LOGGER.info("分布式事务锁初始化中........................");
    }

    @Bean(name = "jedisLockConnectionFactory")
    public JedisConnectionFactory getConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration
            .setHostName(null == prop.getHost() || prop.getHost().length() <= 0 ? "127.0.0.1" : prop.getHost());
        redisStandaloneConfiguration.setPort(prop.getPort() <= 0 ? 6379 : prop.getPort());
        redisStandaloneConfiguration.setDatabase(prop.getDataBase() <= 0 ? 0 : prop.getDataBase());
        if (prop.getPassword() != null && prop.getPassword().length() > 0) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(prop.getPassword()));
        }
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration =
            JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofMillis(prop.getTimeOut() > 0 ? prop.getTimeOut() : 60000));// connection
                                                                                                                      // timeout
        JedisConnectionFactory factory =
            new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
        LOGGER.info("分布式事务锁初始化完成:{}........................", prop);
        return factory;
    }

    @DependsOn({"jedisLockConnectionFactory"})
    @Bean
    public RedisTemplate<String, Object>
        redisLockTemplate(@Qualifier("jedisLockConnectionFactory") JedisConnectionFactory jedisLockConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisLockConnectionFactory);
        redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
