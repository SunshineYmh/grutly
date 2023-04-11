package com.vastly.hlht.Httpclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName httpclient
 * @Description: TODO
 * @Author yangminghao
 * @Date 2022/4/1 
 **/
@Slf4j
public class HttpclientGatewayFilterFactory  {

    private boolean useInsecureTrustManager = true;
    private String keyStorePassword;
    private String KeyStoreFile;
    private String keyStoreType = "PKCS12";
    private boolean isHttps;

    public HttpclientGatewayFilterFactory(){
        this.isHttps = false;
    }
    public HttpclientGatewayFilterFactory(boolean isHttps,boolean useInsecureTrustManager,String keyStorePassword,String KeyStoreFile,String keyStoreType){
        this.useInsecureTrustManager = useInsecureTrustManager;
        this.keyStorePassword = keyStorePassword;
        this.KeyStoreFile = KeyStoreFile;
        this.keyStoreType = keyStoreType;
        this.isHttps = isHttps;
    }

    public HttpClient gatewayHttpClient(HttpClientProperties properties, List<HttpClientCustomizer> customizers) {
        System.out.println("---->我进来了===================");
//        properties.getSsl().setUseInsecureTrustManager(true);
//        properties.getSsl().setKeyPassword("atwasoft2020");
//        properties.getSsl().setKeyStore("classpath:client.p12");
//        properties.getSsl().setKeyStorePassword("atwasoft2020");
//        properties.getSsl().setKeyStoreType("PKCS12");

//        properties.getSsl().setKeyPassword("clientatwasoft");
//        properties.getSsl().setKeyStore("classpath:77929293.p12");
//        properties.getSsl().setKeyStorePassword("clientatwasoft");
//        properties.getSsl().setKeyStoreType("PKCS12");
        if(this.isHttps){
            properties.getSsl().setUseInsecureTrustManager(this.useInsecureTrustManager);
            properties.getSsl().setKeyPassword(this.keyStorePassword);
            properties.getSsl().setKeyStore(this.KeyStoreFile);
            properties.getSsl().setKeyStorePassword(this.keyStorePassword);
            properties.getSsl().setKeyStoreType(this.keyStoreType);
        }
        ConnectionProvider connectionProvider = buildConnectionProvider(properties);
        HttpClient httpClient = HttpClient.create(connectionProvider).httpResponseDecoder((spec) -> {
            if (properties.getMaxHeaderSize() != null) {
                spec.maxHeaderSize((int)properties.getMaxHeaderSize().toBytes());
            }

            if (properties.getMaxInitialLineLength() != null) {
                spec.maxInitialLineLength((int)properties.getMaxInitialLineLength().toBytes());
            }

            return spec;
        }).tcpConfiguration((tcpClient) -> {
            if (properties.getConnectTimeout() != null) {
                tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
            }

            HttpClientProperties.Proxy proxy = properties.getProxy();
            if (StringUtils.hasText(proxy.getHost())) {
                tcpClient = tcpClient.proxy((proxySpec) -> {
                    ProxyProvider.Builder builder = proxySpec.type(ProxyProvider.Proxy.HTTP).host(proxy.getHost());
                    PropertyMapper map = PropertyMapper.get();
                    proxy.getClass();
                    map.from(proxy::getPort).whenNonNull().to(builder::port);
                    proxy.getClass();
                    map.from(proxy::getUsername).whenHasText().to(builder::username);
                    proxy.getClass();
                    map.from(proxy::getPassword).whenHasText().to((password) -> {
                        builder.password((s) -> {
                            return password;
                        });
                    });
                    proxy.getClass();
                    map.from(proxy::getNonProxyHostsPattern).whenHasText().to(builder::nonProxyHosts);
                });
            }

            return tcpClient;
        });
        HttpClientProperties.Ssl ssl = properties.getSsl();
        if (ssl.getKeyStore() != null && ssl.getKeyStore().length() > 0 || ssl.getTrustedX509CertificatesForTrustManager().length > 0 || ssl.isUseInsecureTrustManager()) {
            httpClient = httpClient.secure((sslContextSpec) -> {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
                X509Certificate[] trustedX509Certificates = ssl.getTrustedX509CertificatesForTrustManager();
                if (trustedX509Certificates.length > 0) {
                    sslContextBuilder = sslContextBuilder.trustManager(trustedX509Certificates);
                } else if (ssl.isUseInsecureTrustManager()) {
                    sslContextBuilder = sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                }

                try {
                    sslContextBuilder = sslContextBuilder.keyManager(ssl.getKeyManagerFactory());
                } catch (Exception var6) {
                    log.error(var6.getMessage());
                }

                sslContextSpec.sslContext(sslContextBuilder).defaultConfiguration(ssl.getDefaultConfigurationType()).handshakeTimeout(ssl.getHandshakeTimeout()).closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout()).closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
            });
        }

        if (properties.isWiretap()) {
            httpClient = httpClient.wiretap(true);
        }

        if (properties.isCompression()) {
            httpClient = httpClient.compress(true);
        }

        if (!CollectionUtils.isEmpty(customizers)) {
            customizers.sort(AnnotationAwareOrderComparator.INSTANCE);

            HttpClientCustomizer customizer;
            for(Iterator var6 = customizers.iterator(); var6.hasNext(); httpClient = customizer.customize(httpClient)) {
                customizer = (HttpClientCustomizer)var6.next();
            }
        }

        return httpClient;
    }


    public static ConnectionProvider buildConnectionProvider(HttpClientProperties properties) {
        HttpClientProperties.Pool pool = properties.getPool();
        ConnectionProvider connectionProvider;
        if (pool.getType() == HttpClientProperties.Pool.PoolType.DISABLED) {
            connectionProvider = ConnectionProvider.newConnection();
        } else {
            ConnectionProvider.Builder builder = ConnectionProvider.builder(pool.getName());
            if (pool.getType() == HttpClientProperties.Pool.PoolType.FIXED) {
                ((ConnectionProvider.Builder)((ConnectionProvider.Builder)builder.maxConnections(pool.getMaxConnections())).pendingAcquireMaxCount(-1)).pendingAcquireTimeout(Duration.ofMillis(pool.getAcquireTimeout()));
            } else {
                ((ConnectionProvider.Builder)((ConnectionProvider.Builder)builder.maxConnections(2147483647)).pendingAcquireTimeout(Duration.ofMillis(0L))).pendingAcquireMaxCount(-1);
            }

            if (pool.getMaxIdleTime() != null) {
                builder.maxIdleTime(pool.getMaxIdleTime());
            }

            if (pool.getMaxLifeTime() != null) {
                builder.maxLifeTime(pool.getMaxLifeTime());
            }

            builder.evictInBackground(pool.getEvictionInterval());
            connectionProvider = builder.build();
        }

        return connectionProvider;
    }
}
