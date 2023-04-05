package com.vastly.affairs.hlht.logFilter;


import com.alibaba.fastjson.JSONObject;
import com.vastly.affairs.util.FormDataAnalysisUtil;
import com.vastly.affairs.util.IpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * 日志实体类，方便后续接入ELK
 */
@Configuration
@AllArgsConstructor
@Data
public class LogFilter {

    private TYPE logType;
    private LEVEL level;

    private String sessionId;//会话id
    private String requestId;//请求系统流水号
    private String routeId;//路由id
    private long requestDate;//请求系统开始时间
    private long responseDate;//请求系统结束时间
    private String requestIp;//请求者ip
    private String requestUri;
    private String requestPath;
    private String fwmc;//服务名称
    private String requestContentType;
    private String responseContentType;
    private String  requestCharset;//字符集
    private String  responseCharset;//字符集
    private String requestHeaders;//请求头
    private String responseHeaders;//响应头
    private String requestMethod;//
    private String schema;// 请求协议
    private String hostName;
    private String timeStamp;
    private String serverIp;
    private long executeTime; //执行时间，单位秒
    private String requestQueryParams;
    private String requestBody;
    private String responseBody;
    private long requestBodySize;
    private long responseBodySize;
    private String account;

    private Integer status;
    private String errorMessage;
    private String exceptionMessage;




    String lineeparator = System.getProperty("line.separator");



    public LogFilter() {
        this(TYPE.REQUEST);
    }

    public LogFilter(TYPE logType) {
        this.logType = logType;
        this.hostName = IpUtils.getHostName();
        this.timeStamp = ZonedDateTime.now(ZoneOffset.of("+08:00")).toString();
        this.serverIp = IpUtils.getLocalIp();
    }

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
        OUT;
        private TYPE() {
        }
    }


}
