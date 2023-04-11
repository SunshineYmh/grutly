package com.vastly.hlht;



import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
public class restTemplateConfig {

    //rest客户端读超时设置
    @Value("${restTemplate.ReadTimeout}")
    private int restReadTimeout;
    //rest客户端连接超时设置
    @Value("${restTemplate.ConnectTimeout}")
    private int restConnTimeout;
    // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
    @Value("${restTemplate.restConnectionRequestTimeout}")
    private int restConnectionRequestTimeout;
    //设置总连接数
    @Value("${restTemplate.resPollMaxTotal}")
    private int resPollMaxTotal;
    //设置同路由的并发数
    @Value("${restTemplate.resPollMaxPerRoute}")
    private int resPollMaxPerRoute;
    @Value("${restTemplate.restTimeToLive}")
    private int restTimeToLive;


    @Bean(name = "restTemplate")
    public RestTemplate restTemplates() {

       /* SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(this.restReadTimeout);
        requestFactory.setConnectTimeout(this.restConnTimeout);*/

        // 长连接保持60秒
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(this.restTimeToLive, TimeUnit.SECONDS);
        // 设置总连接数总连接数
        pollingConnectionManager.setMaxTotal(this.resPollMaxTotal);
        // 同路由的并发数 设置同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(this.resPollMaxPerRoute);


        /* 设置超时时间 */
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(this.restConnTimeout)
                /* 设置连接超时时间，单位毫秒 6000s */
                .setSocketTimeout(this.restReadTimeout)
                /* 请求获取数据的超时时间，单位毫秒 6000s */
                .setConnectionRequestTimeout(this.restConnectionRequestTimeout)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(pollingConnectionManager)
                //// 重试次数，默认是3次，没有开启
                //.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true))
                // 保持长连接配置，需要在头添加Keep-Alive
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                // https 请求
                //.setSSLSocketFactory(trustAllHttpsCertificates())
                .build();
        HttpComponentsClientHttpRequestFactory factory = new  HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        //@param connectionRequestTimeout请求连接的超时值（以毫秒为单位）
//        setConnectionRequestTimeout从连接池中获取可用连接超时：设置从connect Manager获取Connection 超时时间，单位毫秒。
//        HttpClient中的要用连接时尝试从连接池中获取，若是在等待了一定的时间后还没有获取到可用连接（比如连接池中没有空闲连接了）则会抛出获取连接超时异常。
        factory.setConnectionRequestTimeout(this.restConnectionRequestTimeout);
        // 连接超时连接目标超时connectionTimeout，单位毫秒。
        // 指的是连接目标url的连接超时时间，即客服端发送请求到与目标url建立起连接的最大时间。如果在该时间范围内还没有建立起连接，则就抛出connectionTimeOut异常。
        // 如测试的时候，将url改为一个不存在的url：“http://test.com” ，超时时间3000ms过后，系统报出异常：
        // org.apache.commons.httpclient.ConnectTimeoutException:The host did not accept the connection within timeout of 3000 ms
        factory.setConnectTimeout(this.restConnTimeout);
        // 数据读取超时时间，即SocketTimeout
        //等待响应超时（读取数据超时）socketTimeout ，单位毫秒。
        //连接上一个url后，获取response的返回等待时间 ，即在与目标url建立连接后，等待放回response的最大时间，在规定时间内没有返回响应的话就抛出SocketTimeout。
        //测试时，将socketTimeout 设置很短，会报等待响应超时。
        factory.setReadTimeout(this.restReadTimeout);

        List<HttpMessageConverter<?>> l = new ArrayList<HttpMessageConverter<?>>();
        l.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        l.add(new FormHttpMessageConverter());
        l.add(new ByteArrayHttpMessageConverter());
        //l.add(new MappingJackson2XmlHttpMessageConverter());
        l.add(new MappingJackson2HttpMessageConverter());
        RestTemplate rt = new RestTemplate(l);
        rt.setRequestFactory(factory);
        rt.setErrorHandler(new DefaultResponseErrorHandler());
        return rt;
    }


}
