package com.vastly.affairs;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
public class MinioClientConfig {

    @Value("${minio.endpoint}")
    private  String endpoint;
    @Value("${minio.accessKey}")
    private  String accessKey;
    @Value("${minio.secretKey}")
    private  String secretKey;

    /**
     * 注入minio 客户端
     *
     * @return
     */
    @Bean
    public MinioClient minioClient() {
        log.info("endpoint={}", endpoint);
        return MinioClient.builder()
                .endpoint(HttpUrl.parse(endpoint))
                .credentials(accessKey, secretKey)
                .build();
    }
}
