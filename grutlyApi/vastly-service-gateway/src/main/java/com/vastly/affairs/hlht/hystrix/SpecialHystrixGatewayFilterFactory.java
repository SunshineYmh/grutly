package com.vastly.affairs.hlht.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.containsEncodedParts;

@Slf4j
@Component
public class SpecialHystrixGatewayFilterFactory extends AbstractGatewayFilterFactory<SpecialHystrixGatewayFilterFactory.Config> {
    private static final String NAME = "SpecialHystrix";

    private final ObjectProvider<DispatcherHandler> dispatcherHandler;

//    @Autowired
//    DspServiceRedis dspServiceRedis;
    private static long timeout = 30;

    public SpecialHystrixGatewayFilterFactory(ObjectProvider<DispatcherHandler> dispatcherHandler) {
        super(Config.class);
        this.dispatcherHandler = dispatcherHandler;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(NAME_KEY);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(request.getHeaders());
            // todo  根据服务id获取服务信息
            Map<String,String> requestHeaders = headers.toSingleValueMap();
            String serviceId = requestHeaders.containsKey("serviceId")?requestHeaders.get("serviceId"):"";//需要访问的服务编码
            //ServiceResource dspService = dspServiceRedis.getRedisServiceBean(serviceId);
            Integer timeout = 30;
            if(timeout > 0){
               // timeout = dspService.getTimeout()*1000;
                timeout = timeout * 1000;
            }else{
                timeout = 30*1000;
            }
            String path = request.getPath().pathWithinApplication().value();
//            Map<String, Integer> timeoutMap = config.getTimeout();
//            Integer timeout = null;
//            if (timeoutMap != null) {
//                //对rest接口通配符url进行转换 暂只配置url 末尾为数字的的接口---
//                path = config.wildCard(path);
//                timeout = timeoutMap.get(path);
//            }
//
           MyRouteHystrixCommand command;
           log.info("Hystrix 熔断策略超时时间["+timeout+"],path["+path+"]");
            URI FallbackUri  = null;
            try {
                 FallbackUri  = new URI("forward:/fallback");
            } catch (URISyntaxException e) {
                //config.getFallbackUri()
                e.printStackTrace();
            }
            if (timeout == null) {
                //没有定义时间的接口将使用配置的default时间
                command = new MyRouteHystrixCommand(FallbackUri, exchange, chain, path);
            } else {
                //有配置时间的接口将使用配置的时间
               command = new MyRouteHystrixCommand(FallbackUri, exchange, chain, timeout, path);
            }

            return Mono.create(s -> {
                Subscription sub = command.toObservable().subscribe(s::success, s::error, s::success);
                s.onCancel(sub::unsubscribe);
            }).onErrorResume((Function<Throwable, Mono<Void>>) throwable -> {
                if (throwable instanceof HystrixRuntimeException) {
                    HystrixRuntimeException e = (HystrixRuntimeException) throwable;
                    HystrixRuntimeException.FailureType failureType = e.getFailureType();
                    switch (failureType) {
                        case TIMEOUT:
                            return Mono.error(new TimeoutException());
                        case COMMAND_EXCEPTION: {
                            Throwable cause = e.getCause();
                            if (cause instanceof ResponseStatusException || AnnotatedElementUtils
                                    .findMergedAnnotation(cause.getClass(), ResponseStatus.class) != null) {
                                return Mono.error(cause);
                            }
                        }
                        default:
                            break;
                    }
                }
                return Mono.error(throwable);
            }).then();
        };
    }

    @Override
    public String name() {
        return NAME;
    }

    private class MyRouteHystrixCommand extends HystrixObservableCommand<Void> {

        private final URI fallbackUri;
        private final ServerWebExchange exchange;
        private final GatewayFilterChain chain;

        public MyRouteHystrixCommand(URI fallbackUri, ServerWebExchange exchange, GatewayFilterChain chain,
                                     String key) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(key)));
            this.fallbackUri = fallbackUri;
            this.exchange = exchange;
            this.chain = chain;

        }

        public MyRouteHystrixCommand(URI fallbackUri, ServerWebExchange exchange, GatewayFilterChain chain,
                                     int timeout,
                                     String key) {
            //***出现通配符的情况**//
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(key))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout)));
            this.fallbackUri = fallbackUri;
            this.exchange = exchange;
            this.chain = chain;

        }

        @Override
        protected Observable<Void> construct() {
            return RxReactiveStreams.toObservable(this.chain.filter(exchange));
        }

        @Override
        protected Observable<Void> resumeWithFallback() {
            if (null == fallbackUri) {
                return super.resumeWithFallback();
            }
            URI uri = exchange.getRequest().getURI();
            boolean encoded = containsEncodedParts(uri);
            URI requestUrl = UriComponentsBuilder.fromUri(uri)
                    .host(null)
                    .port(null)
                    .uri(this.fallbackUri)
                    .build(encoded)
                    .toUri();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);

            ServerHttpRequest request = this.exchange.getRequest().mutate().uri(requestUrl).build();
            ServerWebExchange mutated = exchange.mutate().request(request).build();
            DispatcherHandler dispatcherHandler = SpecialHystrixGatewayFilterFactory.this.dispatcherHandler.getIfAvailable();
            return RxReactiveStreams.toObservable(dispatcherHandler.handle(mutated));
        }
    }

    public static class Config {

        private String id;
        private URI fallbackUri;
        /**
         * url -> timeout ms
         */
        private Map<String, Integer> timeout;

        public String getId() {
            return id;
        }

        public Config setId(String id) {
            this.id = id;
            return this;
        }

        public URI getFallbackUri() {
            return fallbackUri;
        }

        public Config setFallbackUri(URI fallbackUri) {
            if (fallbackUri != null && !"forward".equals(fallbackUri.getScheme())) {
                throw new IllegalArgumentException("Hystrix Filter currently only supports 'forward' URIs, found " + fallbackUri);
            }
            this.fallbackUri = fallbackUri;
            return this;
        }

        public Map<String, Integer> getTimeout() {
            return timeout;
        }

        public Config setTimeout(Map<String, Integer> timeout) {
            //YAML解析的时候MAP的KEY不支持'/'，这里只能用'-'替代
            Map<String, Integer> tempTimeout = new HashMap<>(timeout.size());
            for (String key : timeout.keySet()) {
                Integer value = timeout.get(key);

                key = key.replace("-", "/");
                if (!key.startsWith("/")) {
                    key = "/" + key;
                }
                /** 末尾有动态传参 **/
                if (key.endsWith("/")) {
                    key = key + "**";
                }
                tempTimeout.put(key, value);
            }
            this.timeout = tempTimeout;
            return this;
        }


        public String wildCard(String path){
            String replace = path;
            String[] split = path.split("/");
            if (split.length>0) {
                String wildcard = split[split.length - 1];
                boolean numeric = isNumeric(wildcard);
                if (numeric) {
                    replace = path.replace(wildcard, "**");
                }
            }
            return replace;
        }
        private boolean isNumeric(String str) {
            String bigStr;
            try {
                bigStr = new BigDecimal(str).toString();
            } catch (Exception e) {
                return false;//异常 说明包含非数字。
            }
            return true;
        }
    }
}

