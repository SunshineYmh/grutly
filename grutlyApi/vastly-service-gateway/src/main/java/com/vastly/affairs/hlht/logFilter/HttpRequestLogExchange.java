package com.vastly.affairs.hlht.logFilter;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestLogExchange {

    /**
     * 请求数据 处理，处理异常后的请求数据
     * @param exchange
     * @param logDTO
     * @return
     */
    public static LogFilter ServerHttpRequestwriteBodyLog(ServerWebExchange exchange,  LogFilter logDTO) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(request.getHeaders());
            logDTO.setRequestHeaders(JSONObject.toJSONString(headers.toSingleValueMap()));
            MediaType mediaType = request.getHeaders().getContentType();
            logDTO.setRequestHeaders(JSONObject.toJSONString(headers.toSingleValueMap()));
            logDTO.setRequestContentType(LogHelper.getMediaTypeContentType(mediaType));
            logDTO.setRequestCharset(LogHelper.getMediaTypeCharset(mediaType).toString());
            logDTO.setRequestMediaType(mediaType);
            logDTO.setRequestHttpHeaders(headers);
            //获取请求的地址上的参数
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            if(queryParams.size()>0){
                logDTO.setRequestQueryParams(JSONObject.toJSONString(queryParams.toSingleValueMap()));
            }
            if ( headers.getContentLength() > 0) {
                Flux<DataBuffer> body =   exchange.getRequest().getBody();
                body.subscribe(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // 释放资源
                    DataBufferUtils.release(dataBuffer);
                    logDTO.setRequestBodyBit(bytes);
                    logDTO.setRequestBodySize(bytes.length);
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return logDTO;
    }
}
