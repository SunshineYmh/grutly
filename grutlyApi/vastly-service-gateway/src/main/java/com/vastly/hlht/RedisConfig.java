package com.vastly.hlht;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redishost;
    @Value("${spring.redis.port}")
    private Integer redisport;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.readtimeout}")
    private Integer redisreadtimeout;
    @Value("${spring.redis.timeout}")
    private Integer redisconntimeout;

    @Value("${spring.redis.database}")
    private Integer database;

    @Value("${spring.redis.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.redis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private Integer minIdle;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWait;


    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redishost);
        redisStandaloneConfiguration.setPort(redisport);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));


        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisClientConfigurationBuilder =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration
                        .builder();
        jedisClientConfigurationBuilder.poolConfig(jedisPoolConfig());
        JedisClientConfiguration jedisClientConfiguration = jedisClientConfigurationBuilder.build();
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    // 连接池配置
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大连接数
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxIdle(maxIdle);
        //最小空闲连接数
        jedisPoolConfig.setMinIdle(minIdle);
        //当池内没有可用的连接时，最大等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        // 在获取连接的时候检查有效性, 默认false
        jedisPoolConfig.setTestOnBorrow(true);
        //------其他属性根据需要自行添加-------------
        return jedisPoolConfig;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置序列化Key的实例对象
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 设置序列化Value的实例对象
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}