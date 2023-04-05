package com.vastly.affairs.hlht.handler;

import com.alibaba.fastjson.JSON;
import com.vastly.affairs.hlht.redis.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *将定义好的路由表信息通过此类读写到redis中
 */
@Component
@Order(1001)
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {
 
    //public static final String  GATEWAY_ROUTES = "hlht:sysaffairs:gateway:routes";
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


 
    //请注意，此方法很重要，从redis取路由信息的方法，官方核心包要用，核心路由功能都是从redis取的
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        redisTemplate.opsForHash().values(RedisConfig.GATEWAY_ROUTES).stream().forEach(routeDefinition -> {
            routeDefinitions.add(JSON.parseObject(routeDefinition.toString(), RouteDefinition.class));
        });
        return Flux.fromIterable(routeDefinitions);
    }
 
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route
                .flatMap(routeDefinition -> {
                    redisTemplate.opsForHash().put(RedisConfig.GATEWAY_ROUTES, routeDefinition.getId(),
                            JSON.toJSONString(routeDefinition));
                    return Mono.empty();
                });
    }
 
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            if (redisTemplate.opsForHash().hasKey(RedisConfig.GATEWAY_ROUTES, id)) {
                redisTemplate.opsForHash().delete(RedisConfig.GATEWAY_ROUTES, id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("路由文件没有找到: " + routeId)));
        });
    }
 
}