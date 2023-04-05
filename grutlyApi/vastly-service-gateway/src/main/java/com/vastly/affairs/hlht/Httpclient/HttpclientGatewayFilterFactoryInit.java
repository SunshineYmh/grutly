package com.vastly.affairs.hlht.Httpclient;


import io.netty.util.internal.StringUtil;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HttpclientGatewayFilterFactoryInit
 * @Description: TODO
 * @Author yangminghao
 * @Date 2022/4/1 
 **/
public class HttpclientGatewayFilterFactoryInit {

    public static Map<String, HttpClient> HttpClientMap  = new HashMap<String, HttpClient>();



    public static HttpClient getHttpClient(HttpClientProperties properties,String hostname){
        if(StringUtil.isNullOrEmpty(hostname)){
            if(HttpClientMap.containsKey("defHttpClient")){
                return HttpClientMap.get("defHttpClient");
            }else{
                HttpClient httpClient =  new HttpclientGatewayFilterFactory().gatewayHttpClient(properties,null);
                HttpClientMap.put("defHttpClient",httpClient);
                return httpClient;
            }
        }else{
            if(HttpClientMap.containsKey(hostname)){
                return HttpClientMap.get(hostname);
            }else{
                if("ae65d5ede3b24305aecc2ca780f9565e".equals(hostname)){
                    HttpclientGatewayFilterFactory http =  new HttpclientGatewayFilterFactory(true,false,"atwasoft2020",
                            "classpath:client.p12","PKCS12");
                    HttpClient httpClient =  http.gatewayHttpClient(properties, null);
                    HttpClientMap.put(hostname,httpClient);
                }else if("1003230102101".equals(hostname)){
                    HttpclientGatewayFilterFactory http2 =  new HttpclientGatewayFilterFactory(true,true,"clientatwasoft",
                            "classpath:77929293.p12","PKCS12");
                    HttpClient httpClient2 =  http2.gatewayHttpClient(properties, null);
                    HttpClientMap.put(hostname,httpClient2);
                    return httpClient2;
                }else if("9a73d162a04540d19e5505acbcc52379".equals(hostname)){
                    HttpclientGatewayFilterFactory http2 =  new HttpclientGatewayFilterFactory(true,true,"clientatwasoft",
                            "classpath:77929293.p12","PKCS12");
                    HttpClient httpClient2 =  http2.gatewayHttpClient(properties, null);
                    HttpClientMap.put(hostname,httpClient2);
                    return httpClient2;
                }else{
                    HttpClient httpClient =  new HttpclientGatewayFilterFactory().gatewayHttpClient(properties,null);
                    HttpClientMap.put("defHttpClient",httpClient);
                    return httpClient;
                }
            }
        }
        HttpClient httpClient =  new HttpclientGatewayFilterFactory().gatewayHttpClient(properties,null);
        HttpClientMap.put("defHttpClient",httpClient);
        return httpClient;
    }
}
