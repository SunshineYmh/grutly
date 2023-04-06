package com.vastly.affairs.hlht.filters;

import com.alibaba.fastjson.JSONObject;
import com.vastly.affairs.hlht.communtion.SpringContextUtil;
import com.vastly.affairs.hlht.communtion.CacheManager;
import com.vastly.affairs.hlht.communtion.HttpRequestCommuntion;
import com.vastly.affairs.hlht.exception.vastlyExceptionMessage;
import com.vastly.affairs.hlht.logFilter.BodyPrintAsyncTask;
import com.vastly.affairs.util.DateUtils;
import com.vastly.ymh.core.affairs.entity.LogFilter;
import com.vastly.affairs.hlht.logFilter.LogHelper;
import com.vastly.affairs.util.FormDataAnalysisUtil;
import com.vastly.affairs.util.GeneratedKey;
import com.vastly.affairs.util.IpUtils;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;



/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

/**
 * Ordered 负责filter的顺序，数字越小越优先，越靠前。
 * <p>
 * GatewayFilter：
 * 需要通过spring.cloud.routes.filters 配置在具体路由下，
 * 只作用在当前路由上或通过spring.cloud.default-filters配置在全局，作用在所有路由上。
 * 需要用代码的形式，配置一个RouteLocator，里面写路由的配置信息。
 * <p>
 * GlobalFilter：
 * 全局过滤器，不需要在配置文件中配置，作用在所有的路由上，最终通过GatewayFilterAdapter包装成GatewayFilterChain可识别的过滤器，
 * 它为请求业务以及路由的URI转换为真实业务服务的请求地址的核心过滤器，不需要配置，系统初始化时加载，并作用在每个路由上。
 * 代码配置需要声明一个GlobalFilter对象。
 * <p>
 * <p>
 * 对一个应用来说，GatewayFilter和GlobalFilter是等价的，order也会按照顺序进行拦截。所以两个order不要写一样！
 *
 * @author ymh
 */
//@Component
@Slf4j
public class HttpRequestFilter implements GlobalFilter, Ordered {

    @Autowired
    private GeneratedKey generatedKey;
    //请求业务逻辑处理区域
    @Autowired
    private HttpRequestCommuntion httpRequestCommuntion;

    @Autowired
    private BodyPrintAsyncTask bodyPrintAsyncTask;

    //   值越小，优先级越高
//    int HIGHEST_PRECEDENCE = -2147483648;
//    int LOWEST_PRECEDENCE = 2147483647;
    @Override
    public int getOrder() {

        return Ordered.HIGHEST_PRECEDENCE;
        //return -300;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (generatedKey == null) {
            generatedKey = (GeneratedKey) SpringContextUtil.getBean("generatedKey", GeneratedKey.class);
        }
        if (bodyPrintAsyncTask == null) {
            bodyPrintAsyncTask = (BodyPrintAsyncTask) SpringContextUtil.getBean("bodyPrintAsyncTask", BodyPrintAsyncTask.class);
        }
        if (httpRequestCommuntion == null) {
            httpRequestCommuntion = (HttpRequestCommuntion) SpringContextUtil.getBean("httpRequestCommuntion", HttpRequestCommuntion.class);
        }
        //初始化请求信息
        Date date = new Date();
        long startTime = date.getTime(); //System.currentTimeMillis();
        String uuid = generatedKey.generatorKey();
        log.info("=================================请求连接开始【" + uuid + "】=======================================");
        Route route = getGatewayRoute(exchange);
        AtomicReference<String> requestId = new AtomicReference<>(uuid);
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        //记录日志
        final LogFilter logDTO = new LogFilter();
        logDTO.setLogType(LogFilter.TYPE.REQUEST);
        logDTO.setLevel(LogFilter.LEVEL.INFO);
        logDTO.setHostName(IpUtils.getHostName());
        logDTO.setServerIp(IpUtils.getLocalIp());
        logDTO.setRequestIp(IpUtils.getClientIp(request));
        logDTO.setTimeStamp(DateUtils.dateTimeToString(date));
        logDTO.setStartDate(startTime);
        logDTO.setRouteId(route.getId());

        logDTO.setId(requestId.get());
        // 原始请求体
        URI requri = request.getURI();
        // 请求路径
//        String requestPath = request.getPath().pathWithinApplication().value();
//        System.out.println("-->>11>>"+requestPath);
        logDTO.setRequestUri(requri.toString());
        logDTO.setRequestPath(requri.getPath());
        //获取请求方式
        String method = request.getMethodValue();
        logDTO.setRequestMethod(method);
        //获取请求数据格式类型
        MediaType mediaType = headers.getContentType();
        logDTO.setRequestCharset(LogHelper.getMediaTypeCharset(mediaType).toString());
        logDTO.setRequestContentType(LogHelper.getMediaTypeContentType(mediaType));

        logDTO.setSchema(requri.getScheme());

        // 请求头处理
        Consumer<HttpHeaders> httpHeadersConsumer = httpRequestCommuntion.setHttpRequestHeaders(request, requestId, startTime);


        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeadersConsumer).build();
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();

        return writeBodyLog(build, chain, logDTO);
    }

    private Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }

    /**
     * 请求数据 处理，解决数据自能消费一次的问题
     * @param exchange
     * @param chain
     * @param logDTO
     * @return
     */
    private Mono<Void> writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, LogFilter logDTO) {
        return exchange.getSession().flatMap(webSession -> {
            logDTO.setSessionId(webSession.getId());
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(request.getHeaders());
            MediaType mediaType = request.getHeaders().getContentType();
            //获取请求的地址上的参数
            MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
            if(queryParams.size()>0){
                logDTO.setRequestQueryParams(JSONObject.toJSONString(queryParams.toSingleValueMap()));
            }
            if ( headers.getContentLength() > 0) {
                return DataBufferUtils.join(exchange.getRequest().getBody())
                        .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            // 释放资源
                            DataBufferUtils.release(dataBuffer);

                            byte[] newReqBody ;
                            //处理 form -data 文件数据
                            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType)) {
                                newReqBody = bytes;
                                String wrapperName =  LogHelper.getMediaTypeContentBoundaryType(mediaType);
                                if(StringUtils.isNotEmpty(wrapperName)){
                                    JSONObject jsondata = FormDataAnalysisUtil.getMultipartFormData(bytes, wrapperName);
                                    logDTO.setRequestBody(LogHelper.reqBodyLog(jsondata.toString().getBytes(Charset.forName("UTF-8")), mediaType));
                                    logDTO.setResponseBodySize(newReqBody.length);
                                }
                            }else{
                                //todo 请求报文修改
                                newReqBody = httpRequestCommuntion.ServerBodyBussTask(bytes, mediaType, logDTO.getId(), "request");
                                headers.remove(HttpHeaders.CONTENT_LENGTH);
                                logDTO.setRequestBody(LogHelper.reqBodyLog(newReqBody, mediaType));
                                logDTO.setResponseBodySize(newReqBody.length);
                            }
                            // 重写原始请求
                            ServerHttpRequestDecorator requestDecorator = serverHttpRequestDecorator(newReqBody, exchange);
                            headers.putAll(requestDecorator.getHeaders());
                            logDTO.setRequestHeaders(JSONObject.toJSONString(headers.toSingleValueMap()));

                            // 设置响应
                            ServerHttpResponseDecorator decoratedResponse = getServerHttpResponseDecorator(exchange, logDTO);
                            return chain.filter(exchange.mutate()
                                    .request(requestDecorator).response(decoratedResponse)
                                    .build()).then(Mono.fromRunnable(() -> {
                                                        LogHelper.doRecord(bodyPrintAsyncTask,logDTO,"HttpRequestFilter 业务数据 》》》：");
                                                    }));
                        });
            } else {
                ServerHttpResponseDecorator decoratedResponse = getServerHttpResponseDecorator(exchange, logDTO);
                return chain.filter(exchange.mutate().response(decoratedResponse).build())
                        .then(Mono.fromRunnable(() -> {
                            LogHelper.doRecord(bodyPrintAsyncTask,logDTO,"HttpRequestFilter 业务数据 >>>：");
                        }));
            }
        });
    }



    /**
     * // 重写原始请求
     *
     * @param newReqBody
     * @param exchange
     * @return
     */
    public ServerHttpRequestDecorator serverHttpRequestDecorator(byte[] newReqBody, ServerWebExchange exchange) {

        // 重写原始请求
        ServerHttpRequestDecorator requestDecorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
            //byte[] newBody = newReqBody.getBytes(StandardCharsets.UTF_8);
            @Override
            public Flux<DataBuffer> getBody() {
                NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                DataBuffer bodyDataBuffer = nettyDataBufferFactory.wrap(newReqBody);
                return Flux.just(bodyDataBuffer);
            }

            @Override
            public MultiValueMap<String, String> getQueryParams() {
                return UriComponentsBuilder.fromUri(exchange.getRequest().getURI()).build().getQueryParams();
            }

            @Override
            public HttpHeaders getHeaders() {
                long contentLength = newReqBody.length;
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }
        };
        return requestDecorator;
    }

    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();




    /**
     * 表单数据处理
     * @param exchange
     * @param chain
     * @param logDTO
     * @return
     */
    private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, LogFilter logDTO) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Map<String,String> reqdata = queryParams.toSingleValueMap();
        JSONObject respjson = new JSONObject();
        reqdata.forEach((k,v)->{
            respjson.put(k,v);
        });
        logDTO.setRequestBody(respjson.toString());
        //获取响应体
        ServerHttpResponseDecorator decoratedResponse = getServerHttpResponseDecorator(exchange, logDTO);
        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .then(Mono.fromRunnable(() -> {
                    LogHelper.doRecord(bodyPrintAsyncTask,logDTO,"HttpRequestFilter writeBasicLog 业务数据 >>>：");
                }));
    }

    /**
     * 响应数据处理
     * @param exchange
     * @param logDTO
     * @return
     */
    private ServerHttpResponseDecorator getServerHttpResponseDecorator(ServerWebExchange exchange,LogFilter logDTO) {
        // 获取response的返回数据
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        // 封装返回体
        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                HttpStatus httpStatus  = originalResponse.getStatusCode();
                HttpHeaders responseHeaders = originalResponse.getHeaders();
                MediaType mediaType = originalResponse.getHeaders().getContentType();
                logDTO.setResponseHeaders(JSONObject.toJSONString(responseHeaders.toSingleValueMap()));
                logDTO.setResponseContentType(LogHelper.getMediaTypeContentType(mediaType));
                logDTO.setResponseCharset(LogHelper.getMediaTypeCharset(mediaType).toString());
                logDTO.setStatus(httpStatus.value());
                log.info(logDTO.getId()+" :响应信息处理》》》》》》》》》》》》》》》");
                long responseDate = System.currentTimeMillis();
                logDTO.setEndDate(responseDate);
                // 计算执行时间
                long executeTime = (responseDate - logDTO.getStartDate());
                logDTO.setExecuteTime(executeTime);
                if ( body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        // 合并多个流集合，解决返回体分段传输
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffers);
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(join);

                        // 响应报文特殊处理
                        byte[] newRespBody = httpRequestCommuntion.ServerBodyBussTask(content, mediaType, logDTO.getId(), "response");
                        logDTO.setResponseBody(LogHelper.respBodyLog(newRespBody, mediaType,responseHeaders));
                        logDTO.setResponseBodySize(newRespBody.length);

                        return bufferFactory.wrap(newRespBody);
                    }));
                }else{
                    // if body is not a flux. never got there.
                    return super.writeWith(body);
                }
            }
        };
    }





    /**
     * 校验失败网关拒绝，返回Result
     *
     * @param
     */
    private Mono<Void> noPower(ServerWebExchange serverWebExchange, int code, String msg) {


        Map<String, Object> mapmssage = vastlyExceptionMessage.response(code, msg, null);
        String responseBody = JSONObject.toJSONString(mapmssage.get("body"));
        AtomicReference<String> respBody = new AtomicReference<>();
        respBody.set(responseBody);
        if (code == 201) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.CREATED);
        }
        if (code == 401) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        } else {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        DataBuffer buffer = serverWebExchange.getResponse().bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        ServerHttpResponse response = serverWebExchange.getResponse();
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        if (code == 201) {
            response.setStatusCode(HttpStatus.CREATED);
        }
        if (code == 401) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders requestHeaders = response.getHeaders();
        //LogFilter logDTO = LogHelper.buildLog(headers, responseBody, response.getStatusCode(), serverWebExchange.getRequest());
//        LogFilter logDTO = LogHelper.respBuildLog(requestHeaders, responseBody,
//                response.getStatusCode(), serverWebExchange.getRequest(), response);
//        LogHelper.doLog(logDTO);
        return response.writeWith(Mono.just(buffer));
    }


}


//todo 获取服务信息列表，进行服务信息校验
// 设置转发地址
// 设置转发地址
//            String uri = "https://bank.sjgjj.cn:19090/loan_share/public/xfd/gjjgrxfd.service";
//            URI finalRequri = new URI(uri);
//            //URI finalRequri = requri;
//            //log.info(finalRequri.getPath()+"请求地址》》》》"+finalRequri.toString());
//            //todo 设置请求头信息
//
//            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeadersConsumer)
//                    .uri(finalRequri).build();
//            exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
//                    serverHttpRequest.getURI());
//            ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
//            headers.putAll(serverHttpRequest.getHeaders());


// 重新设置请求地址
//                                ServerHttpRequest serverHttpRequestsend = requestDecorator.mutate()
//                                        .uri(finalRequri1).build();
//                                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
//                                        requestDecorator.getURI());
//                                System.out.println("-->>>33>>"+serverHttpRequestsend.getURI().toString());

