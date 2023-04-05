package com.vastly.affairs.hlht.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * * 用户访问限流
 */
public class UriKeyResolver implements KeyResolver {
    Logger logger = LoggerFactory.getLogger(UriKeyResolver .class);
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        logger.info("服务限流开始=》》》》");
        /*ServerHttpRequest request = exchange.getRequest();
        String SendNode = "";
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        for(Map.Entry<String, String> entry:headers .entrySet()){
            if("sendnode".equals(entry.getKey().toLowerCase())){
                SendNode = entry.getValue();
            }
        }
        logger.info("服务限流SendNode：{}",SendNode);
        if(StringUtils.isEmpty(SendNode)){
            return Mono.empty();
        }
        return  Mono.just(SendNode);*/
        return  Mono.just(exchange.getRequest().getURI().getPath());
    }


}
