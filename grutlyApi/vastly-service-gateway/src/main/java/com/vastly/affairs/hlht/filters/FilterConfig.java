package com.vastly.affairs.hlht.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @ClassName FilterConfig
 * @Description: TODO 全局过滤器
 * @Author yangminghao
 * @Date 2022/1/28 
 **/
@Configuration
public class FilterConfig {


    @Bean
    @Order(0)
    public GlobalFilter HttpRequestFilter()
    {
        return new HttpRequestFilter();
    }

//    @Bean
//    @Order(11)
//    public GlobalFilter HttpResponseFilter()
//    {
//        return new HttpResponseFilter();
//    }

//    @Bean
//    @Order(2)
//    public GlobalFilter NettyRoutingFilter(){return new NettyRoutingFilter();}

}
