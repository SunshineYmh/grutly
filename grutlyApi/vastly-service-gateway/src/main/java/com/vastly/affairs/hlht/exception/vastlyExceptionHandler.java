package com.vastly.affairs.hlht.exception;


import com.alibaba.fastjson.JSONObject;
import com.vastly.affairs.hlht.communtion.SpringContextUtil;
import com.vastly.affairs.hlht.logFilter.BodyPrintAsyncTask;
import com.vastly.affairs.hlht.logFilter.HttpRequestLogExchange;
import com.vastly.affairs.hlht.logFilter.LogFilter;
import com.vastly.affairs.hlht.logFilter.LogHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description: 捕获及处理异常
 * @Author:
 */
public class vastlyExceptionHandler implements ErrorWebExceptionHandler {
    private static final Log log = LogFactory.getLog(vastlyExceptionHandler.class);

    @Autowired
    private BodyPrintAsyncTask bodyPrintAsyncTask;


    /**
     * MessageReader
     */
    private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();

    /**
     * MessageWriter
     */
    private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();

    /**
     * ViewResolvers
     */
    private List<ViewResolver> viewResolvers = Collections.emptyList();

    /**
     * 存储处理异常后的信息
     */
    private ThreadLocal<Map<String, Object>> exceptionHandlerResult = new ThreadLocal<>();

    /**
     * 参考AbstractErrorWebExceptionHandler
     */
    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
        Assert.notNull(messageReaders, "'messageReaders' must not be null");
        this.messageReaders = messageReaders;
    }

    /**
     * 参考AbstractErrorWebExceptionHandler
     */
    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    /**
     * 参考AbstractErrorWebExceptionHandler
     */
    public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
        Assert.notNull(messageWriters, "'messageWriters' must not be null");
        this.messageWriters = messageWriters;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (bodyPrintAsyncTask == null) {
            bodyPrintAsyncTask = (BodyPrintAsyncTask) SpringContextUtil.getBean("bodyPrintAsyncTask", BodyPrintAsyncTask.class);
        }
        log.error("服务异常了："+ex.getCause()+";"+ex.getClass().getName()+";;"+ex.getClass().getTypeName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ex.printStackTrace(new PrintStream(baos));
        String exceptionMessage = baos.toString();
        //ex.printStackTrace();
        // 按照异常类型进行处理
        HttpStatus httpStatus;
        int code = 500;
        String errorMessage = "";
        if (ex instanceof NotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            code = HttpStatus.NOT_FOUND.value();
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            httpStatus = responseStatusException.getStatus();
            code = responseStatusException.getStatus().value();
        }else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        if(ex.getClass().getTypeName().contains("HystrixRuntimeException")){
            log.error("熔断服务异常处理》》》》");
            //服务熔断处理
            httpStatus = HttpStatus.GATEWAY_TIMEOUT;
            code = HttpStatus.GATEWAY_TIMEOUT.value();
            errorMessage = "服务熔断降级，服务暂时不可用";
        }else if(ex.getClass().getTypeName().contains("TimeoutException")){
            log.error("服务请求超时了》》》》");
            //服务熔断处理
            httpStatus = HttpStatus.GATEWAY_TIMEOUT;
            code = HttpStatus.GATEWAY_TIMEOUT.value();
            errorMessage = "服务已超时，请稍后再试";
        }
        //封装响应体,此body可修改为自己的jsonBody
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();

        HttpHeaders responseHeaders = originalResponse.getHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> mapmssage = vastlyExceptionMessage.responseBuildMessage(code, request, ex,errorMessage,httpStatus);
        String responseBody = JSONObject.toJSONString(mapmssage.get("body"));


        LogFilter logDTO = LogHelper.respBuildExchangeLog( exchange,  responseBody, code, (String) mapmssage.get("errorMessage"),exceptionMessage);

        //参考AbstractErrorWebExceptionHandler
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        DataBuffer buffer = originalResponse.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        //return httpResponse.writeWith(Mono.just(buffer));
        return originalResponse.writeWith(Flux.just(buffer)).then(Mono.fromRunnable(() -> {
            LogHelper.doRecord(bodyPrintAsyncTask, logDTO,"服务异常 ###：");
        }));

//        exceptionHandlerResult.set(mapmssage);
//        ServerRequest newRequest = ServerRequest.create(exchange, this.messageReaders);
//        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse).route(newRequest)
//                .switchIfEmpty(Mono.error(ex))
//                .flatMap((handler) -> handler.handle(newRequest))
//                .flatMap((response) -> write(exchange, response)).then(Mono.fromRunnable(() -> {
//                    LogHelper.doRecord(bodyPrintAsyncTask, logDTO,"服务异常 ###：");
//                }));

    }


    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    /**
     * 参考DefaultErrorWebExceptionHandler
     */
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> result = exceptionHandlerResult.get();
        return ServerResponse.status((HttpStatus) result.get("httpStatus"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(result.get("body")));
    }

    /**
     * 参考AbstractErrorWebExceptionHandler
     */
    private Mono<? extends Void> write(ServerWebExchange exchange,
                                       ServerResponse response) {
        exchange.getResponse().getHeaders()
                .setContentType(response.headers().getContentType());
        return response.writeTo(exchange, new ResponseContext());
    }

    /**
     * 参考AbstractErrorWebExceptionHandler
     */
    private class ResponseContext implements ServerResponse.Context {

        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return vastlyExceptionHandler.this.messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return vastlyExceptionHandler.this.viewResolvers;
        }

    }



}
