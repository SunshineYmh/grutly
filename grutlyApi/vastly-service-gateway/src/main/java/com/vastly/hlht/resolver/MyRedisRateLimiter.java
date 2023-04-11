package com.vastly.hlht.resolver;

import com.alibaba.fastjson.JSON;
import com.vastly.hlht.redis.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * 创建我们自己的redis速率限制器MyRedisRateLimiter，这里也是进行限流的一个主要的方法，其中会有个redisScript的注入，这里主要是要使用上面所提到的lua的脚本
 */
@Slf4j
@Component
public class MyRedisRateLimiter {


    private final String keyNamespace="gateway:test:limit:";
    @Resource
    private  RedisTemplate<String, Long> redisTemplate;

//    private final RedisScript<List<Long>> redisScript;

    private DefaultRedisScript<List> redisScript;


    /**
    * *redisScript 该RedisScript对象将会自动注入进来，该对象使用的正是上面介绍的request-rate-limiter.lua脚本
    */
//    public MyRedisRateLimiter(RedisTemplate redisTemplate,
//                              RedisScript<List<Long>> redisScript){
//        this.redisTemplate = redisTemplate;
//        this.redisScript = redisScript;
//    }

    @PostConstruct
    public void init(){
        redisScript = new DefaultRedisScript<List>();
        redisScript.setResultType(List.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(("limit.lua"))));
    }

    public boolean isAllowed(String key, int replenishRate, int burstCapacity){
        List<String> keys = Arrays.asList(keyNamespace+key+"tokens", keyNamespace+key+"timestamp");
        try {
            List<Long> response = this.redisTemplate.execute(this.redisScript, keys,
                    replenishRate + "",
                    burstCapacity + "",
                    Instant.now().getEpochSecond() + "",
                    1 + "");
            if(response.get(0) ==0){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return true;
        }

    }

    public RouteDefinition getRouteDefinition(String routeId){
        if (redisTemplate.opsForHash().hasKey(RedisConfig.GATEWAY_ROUTES, routeId)) {
            String routeDefinitionJson = (String) redisTemplate.opsForHash().get(RedisConfig.GATEWAY_ROUTES, routeId);
            return JSON.parseObject(routeDefinitionJson, RouteDefinition.class);
        }
        return null;
    }
}

/**
 --redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)
 --该参数是令牌桶填充速率
 local rate = tonumber(ARGV[1])

 --该参数是令牌桶容量
 local capacity = tonumber(ARGV[2])

 --该参数是访问的时间戳
 local now = tonumber(ARGV[3])

 --该参数每次取出的令牌数量
 local requested = tonumber(ARGV[4])

 local fill_time = capacity/rate
 local ttl = math.floor(fill_time*2)

 --redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
 --redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
 --redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
 --redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
 --redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
 --redis.log(redis.LOG_WARNING, "ttl " .. ttl)

 local last_tokens = tonumber(redis.call("get", tokens_key))
 if last_tokens == nil then
 last_tokens = capacity
 end
 --redis.log(redis.LOG_WARNING, "last_tokens " .. last_tokens)

 local last_refreshed = tonumber(redis.call("get", timestamp_key))
 if last_refreshed == nil then
 last_refreshed = 0
 end
 --redis.log(redis.LOG_WARNING, "last_refreshed " .. last_refreshed)

 local delta = math.max(0, now-last_refreshed)
 local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
 local allowed = filled_tokens >= requested
 local new_tokens = filled_tokens
 local allowed_num = 0
 if allowed then
 new_tokens = filled_tokens - requested
 allowed_num = 1
 end

 --redis.log(redis.LOG_WARNING, "delta " .. delta)
 --redis.log(redis.LOG_WARNING, "filled_tokens " .. filled_tokens)
 --redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)
 --redis.log(redis.LOG_WARNING, "new_tokens " .. new_tokens)

 if ttl > 0 then
 redis.call("setex", tokens_key, ttl, new_tokens)
 redis.call("setex", timestamp_key, ttl, now)
 end

 -- return { allowed_num, new_tokens, capacity, filled_tokens, requested, new_tokens }
 --返回的有两个参数，allowed_num表示该算法算出来允许访问的次数，如果该值大于0，就表示可以允许这次访问通过
 --
 return { allowed_num, new_tokens }
 */