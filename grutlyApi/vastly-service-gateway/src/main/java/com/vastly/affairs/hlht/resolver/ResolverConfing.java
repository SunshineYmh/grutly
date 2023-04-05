package com.vastly.affairs.hlht.resolver;

import com.vastly.affairs.hlht.exception.vastlyExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * 限流操作
 */
@Configuration
public class ResolverConfing {

    public final static String ACCESS_TOKEN = "access_token";

    /**
     * * 服务接口根据用户进行限流
     * @return
     */
    @Bean
    public KeyResolver UriKeyResolver(){

        return new UriKeyResolver();
    }






    /**
     * IP限流 (通过exchange对象可以获取到请求信息，这边用了HostName)
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * 用户限流 (通过exchange对象可以获取到请求信息，获取当前请求的用户 TOKEN)
     */
    @Bean
    public KeyResolver userKeyResolver() {
        //使用这种方式限流，请求Header中必须携带X-Access-Token参数
        return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst(ACCESS_TOKEN));
    }

    /**
     * 接口限流 (获取请求地址的uri作为限流key)
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }



    /**
     * 自定义异常处理[@@]注册Bean时依赖的Bean，会从容器中直接获取，所以直接注入即可
     */
    @Primary
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                                             ServerCodecConfigurer serverCodecConfigurer) {
        vastlyExceptionHandler jsonExceptionHandler = new vastlyExceptionHandler();
        jsonExceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        jsonExceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        jsonExceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return jsonExceptionHandler;
    }

}
