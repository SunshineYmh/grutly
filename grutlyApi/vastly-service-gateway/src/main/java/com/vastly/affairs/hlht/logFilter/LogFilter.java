package com.vastly.affairs.hlht.logFilter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


/**
 * 日志实体类，方便后续接入ELK
 */
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Document("vastly_gateway_log")
@Data
public class LogFilter {

    @Id// 必须指定id列
    private String id;//请求系统流水号

    private TYPE logType;
    private LEVEL level;

    private String sessionId;//会话id
    @Field
    private String routeId;//路由id
    private long startDate;//请求系统开始时间
    private long endDate;//请求系统结束时间
    @Field
    private String timeStamp;
    private String requestIp;//请求者ip
    private String requestUri;
    private String requestPath;
    private String fwmc;//服务名称
    private MediaType requestMediaType;
    private MediaType responseMediaType;
    private HttpHeaders requestHttpHeaders;
    private HttpHeaders responseHttpHeaders;
    private String requestContentType;
    private String responseContentType;
    private String  requestCharset;//字符集
    private String  responseCharset;//字符集
    private String requestHeaders;//请求头
    private String responseHeaders;//响应头
    private String requestMethod;//
    private String schema;// 请求协议
    private String hostName;
    private String serverIp;
    private long executeTime; //执行时间，单位秒
    private String requestQueryParams;
    private String requestBody;
    private String responseBody;
    @Transient
    private byte[] requestBodyBit;
    @Transient
    private byte[] responseBodyBit;
    private long requestBodySize;
    private long responseBodySize;
    private String account;

    private Integer status;
    private String errorMessage;
    private String exceptionMessage;




    /**
     * 日志级别枚举类
     */
    public static enum LEVEL {
        OFF,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
        ALL;
        private LEVEL() {
        }
    }

    /**
     * 日志类型枚举
     */
    public static enum TYPE {
        REQUEST,
        RESPONSE,
        OUT,
        EXCEPTION;
        private TYPE() {
        }
    }


}
