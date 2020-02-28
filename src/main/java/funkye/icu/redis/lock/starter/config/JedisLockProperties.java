package funkye.icu.redis.lock.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = JedisLockProperties.JEDIS_PREFIX)
public class JedisLockProperties {
    public static final String JEDIS_PREFIX = "redis.lock.server";

    private String host;

    private int port;

    private String password;

    private int maxTotal;

    private int maxIdle;

    private int maxWaitMillis;

    private int dataBase;

    private int timeOut;

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public int getDataBase() {
        return dataBase;
    }

    public void setDataBase(int dataBase) {
        this.dataBase = dataBase;
    }

    public static String getJedisPrefix() {
        return JEDIS_PREFIX;
    }

    @Override
    public String toString() {
        return "JedisProperties [host=" + host + ", port=" + port + ", password=" + password + ", maxTotal=" + maxTotal
            + ", maxIdle=" + maxIdle + ", maxWaitMillis=" + maxWaitMillis + ", dataBase=" + dataBase + ", timeOut="
            + timeOut + "]";
    }

}
