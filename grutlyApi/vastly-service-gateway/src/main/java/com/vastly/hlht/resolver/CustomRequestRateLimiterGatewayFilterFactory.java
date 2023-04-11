package com.vastly.hlht.resolver;

import com.alibaba.fastjson.JSONObject;
import com.vastly.hlht.exception.vastlyExceptionMessage;
import com.vastly.hlht.logFilter.BodyPrintAsyncTask;
import com.vastly.hlht.logFilter.LogFilter;
import com.vastly.hlht.logFilter.LogHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(-2000)
public class CustomRequestRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory {
    //extends RequestRateLimiterGatewayFilterFactory
    private static final Log log = LogFactory.getLog(CustomRequestRateLimiterGatewayFilterFactory.class);
    @Autowired
    private MyRedisRateLimiter myRedisRateLimiter;

    @Autowired
    private BodyPrintAsyncTask bodyPrintAsyncTask;





    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            // 令牌桶每秒填充平均速率-ReplenishRate
            // 令牌桶总容量-BurstCapacity
            if (pathRateLimiter(exchange) ) {
                return chain.filter(exchange);
            }
            ServerHttpResponse httpResponse = exchange.getResponse();
            httpResponse.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            if (!httpResponse.getHeaders().containsKey("Content-Type")) {
                httpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            }
            Map<String, Object> mapmssage = vastlyExceptionMessage.response(429, "访问已限流，请稍候再请求",null);
            String retult = JSONObject.toJSONString(mapmssage.get("body"));
            LogFilter logDTO = LogHelper.respBuildExchangeLog( exchange,  retult, 429 ,"访问已限流，请稍候再请求","");


            DataBuffer buffer = httpResponse.bufferFactory().wrap(retult.getBytes(StandardCharsets.UTF_8));
            //return httpResponse.writeWith(Mono.just(buffer));
            return httpResponse.writeWith(Flux.just(buffer)).then(Mono.fromRunnable(() -> {
                                            LogHelper.doRecord(bodyPrintAsyncTask, logDTO,"CustomRequestRateLimiter 限流信息 ### ：");
                                        }));
        };
    }

    private Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }


    //ip限流
    private Boolean pathRateLimiter(ServerWebExchange exchange){
        //获取到调用客户端的IP地址
        String path = exchange.getRequest().getPath().value();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        log.info("限流用户："+route.getId()+";"+path);
        RouteDefinition routeDefinition = myRedisRateLimiter.getRouteDefinition(route.getId());
        if(routeDefinition == null){
            return true;
        }
        List<FilterDefinition> filterDefinitions = routeDefinition.getFilters();
        int replenishRate = 0;
        int burstCapacity = 0;
        Map<String, String> args = new HashMap<>();
        for(FilterDefinition filters : filterDefinitions){
            if("CustomRequestRateLimiter".equals(filters.getName())){
                args =  filters.getArgs();

            };
        };
        if(args != null &&  args.size()>0){
            if(args.containsKey("redis-rate-limiter.replenishRate")){
                replenishRate = Integer.parseInt(args.get("redis-rate-limiter.replenishRate"));
            }
            if(args.containsKey("redis-rate-limiter.burstCapacity")){
                burstCapacity = Integer.parseInt(args.get("redis-rate-limiter.burstCapacity"));
            }
        }else{
            return true;
        }
        log.info("限流策略："+args);
        if(replenishRate>0 && burstCapacity>0){
            //从数据库中获取到该IP地址对应的限流参数
            return myRedisRateLimiter.isAllowed("path:"+path+":",
                    replenishRate, //令牌桶每秒填充平均速率
                    burstCapacity); //令牌桶总容量
        }else{
            return true;
        }
    }



}





//    public GatewayFilter apply2(Config config) {
////        KeyResolver resolver = (config.getKeyResolver() == null) ? defaultKeyResolver : config.getKeyResolver();
////        RateLimiter<Object> limiter = (config.getRateLimiter()==null)?defaultRateLimiter:config.getRateLimiter();
//        KeyResolver resolver =   config.getKeyResolver();
//        RateLimiter<Object> limiter =  config.getRateLimiter();
//        return (exchange, chain) -> {
//            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
//            return resolver.resolve(exchange).flatMap(key ->
//                    // TODO: if key is empty?
//                    limiter.isAllowed(route.getId(), key).flatMap(response -> {
//                        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
//                            exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
//                        }
//                        log.info("限流用户："+route.getId()+";"+key);
//                        if("admin".equals(key)){
//                            return chain.filter(exchange);
//                        }
//                        // 允许访问
//                        if (response.isAllowed()) {
//                            return chain.filter(exchange);
//                        }
//
//                        HttpStatus status = config.getStatusCode();
//
//                        ServerHttpResponse httpResponse = exchange.getResponse();
//                        httpResponse.setStatusCode(config.getStatusCode());
//                        //httpResponse.getHeaders().add(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
//                        if (!httpResponse.getHeaders().containsKey("Content-Type")) {
//                            httpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//                        }
//                        HttpHeaders responseHeaders = httpResponse.getHeaders();
//                        Map<String, Object> mapmssage = vastlyExceptionMessage.response(429, "访问已限流，请稍候再请求",status);
//                        String retult = JSONObject.toJSONString(mapmssage.get("body"));
//                        HttpStatus httpStatus = httpResponse.getStatusCode();
//                        ServerHttpRequest request = exchange.getRequest();
//                        LogFilter logDTO = LogHelper.respBuildLog(responseHeaders,JSONObject.toJSONString(mapmssage),  httpStatus, request,httpResponse);
//                        //log.error("访问已限流，请稍候再请求【"+logDTO.toString()+"】" );
//                        LogHelper.doLog(logDTO);
//                        DataBuffer buffer = httpResponse.bufferFactory().wrap(retult.getBytes(StandardCharsets.UTF_8));
//                        //return httpResponse.writeWith(Mono.just(buffer));
//                        return httpResponse.writeWith(Flux.just(buffer));
//
//                        //return HttpResponseUtils.writeUnauth(exchange.getResponse(), "网关转发客户端，执行验证异常：访问已限流，请稍候再请求");
//                        //return httpResponse.setComplete();
//                    }));
//        };
//    }