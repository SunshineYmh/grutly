//package com.vastly.affairs.hlht.handler;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.vastly.affairs.hlht.communtion.CacheManager;
//import com.vastly.ymh.core.affairs.entity.LogFilter;
//import com.vastly.affairs.hlht.logFilter.LogHelper;
//import io.netty.buffer.UnpooledByteBufAllocator;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.NettyDataBufferFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
//import reactor.core.publisher.Flux;
//import reactor.core.scheduler.Schedulers;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.util.Objects;
//
///**
// * 对ServerHttpRequest进行二次封装，解决requestBody只能读取一次的问题
// * @author: Li Fengdi
// * @date: 2020-03-17 18:02
// */
//@Slf4j
//public class CacheServerHttpRequestDecorator extends ServerHttpRequestDecorator {
//    private DataBuffer bodyDataBuffer;
//    private int getBufferTime = 0;
//    private byte[] bytes;
//    private LogFilter logDTO;
//
//    private CacheManager cacheManager;
//
//    public CacheServerHttpRequestDecorator(ServerHttpRequest delegate, LogFilter logDTO, CacheManager cacheManager) {
//        super(delegate);
//        this.logDTO = logDTO;
//        this.cacheManager = cacheManager;
//    }
//
//    @Override
//    public Flux<DataBuffer> getBody() {
//        if (getBufferTime == 0) {
//            getBufferTime++;
//            Flux<DataBuffer> flux = super.getBody();
//            return flux.publishOn(Schedulers.single())
//                    .map(this::cache)
//                    .doOnComplete(() -> trace(getDelegate()));
//
//        } else {
//            return Flux.just(getBodyMore());
//        }
//
//    }
//
//
//    private DataBuffer getBodyMore() {
//        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
//        bodyDataBuffer = nettyDataBufferFactory.wrap(bytes);
//        return bodyDataBuffer;
//    }
//
//    private DataBuffer cache(DataBuffer buffer) {
//        try {
//            InputStream dataBuffer = buffer.asInputStream();
//            bytes = IOUtils.toByteArray(dataBuffer);
//            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
//            bodyDataBuffer = nettyDataBufferFactory.wrap(bytes);
//            return bodyDataBuffer;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private void trace(ServerHttpRequest request) {
//        URI requestUri = request.getURI();
//        String uriQuery = requestUri.getQuery();
//        String url = requestUri.getPath() + (StringUtils.isNotBlank(uriQuery) ? "?" + uriQuery : "");
//        HttpHeaders headers = request.getHeaders();
//        MediaType mediaType = headers.getContentType();
//        String schema = requestUri.getScheme();
//        String method = request.getMethodValue().toUpperCase();
//        if ((!"http".equals(schema) && !"https".equals(schema))) {
//            return;
//        }
//        String reqBody = null;
//        if (Objects.nonNull(mediaType) && LogHelper.isUploadFile(mediaType)) {
//            reqBody = "上传文件";
//        } else {
//            if (method.equals("GET")) {
//                if (StringUtils.isNotBlank(uriQuery)) {
//                    reqBody = uriQuery;
//                }
//            } else if (headers.getContentLength() > 0) {
//                reqBody = LogHelper.readRequestBody(request);
//            }
//        }
//        HttpHeaders headerss = new HttpHeaders();
//        headerss.putAll(request.getHeaders());
//        logDTO.setRequestHeaders(headerss.toSingleValueMap());
//        logDTO.setRequestBody(reqBody.getBytes());
//        cacheManager.put(logDTO.getRequestId(), JSONObject.toJSONString(logDTO));
//    }
//
//}
//
