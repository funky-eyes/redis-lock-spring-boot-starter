# redis-lock-spring-boot-starter

#### 介绍
redis实现分布式锁

#### 软件架构

项目需引入如下依赖方可使用:

```java
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-autoconfigure</artifactId>
			<version>2.1.8.RELEASE</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>redis.clients</groupId>
			<artifactId>jedis</artifactId>
			<version>2.9.1</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
			<version>2.1.8.RELEASE</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
			<version>2.1.8.RELEASE</version>
			<scope>provided</scope>
		</dependency>
	</dependencies>
```

#### 使用说明

1. ​    @RedisLock(key = "分布式锁的key值(目前没有默认值,默认值准备预设为方法名)",timeoutMills=超时时间默认60秒,可自定义)

2. 将以上注解加入需要使用分布式锁的地方

3. ```yaml
   redis.lock.server.host=127.0.0.1 #默认
   redis.lock.server.port=6379 #默认
   redis.lock.server.password= #默认""
   redis.lock.server.database= #默认0
   ```
