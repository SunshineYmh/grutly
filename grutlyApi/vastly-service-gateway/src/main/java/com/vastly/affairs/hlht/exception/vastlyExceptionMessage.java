package com.vastly.affairs.hlht.exception;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.HashMap;
import java.util.Map;

public class vastlyExceptionMessage {

    public static Map<String, Object> responseBuildMessage(int status,ServerHttpRequest request, Throwable ex,String errorMessage,
                                             HttpStatus httpStatus){
       return  response(status, buildMessage(request, ex,errorMessage),httpStatus);
    }



    /**
     * 构建异常信息
     * @param request
     * @param ex
     * @return
     */
    public static String buildMessage(ServerHttpRequest request, Throwable ex,String errorMessage) {
        StringBuilder message = new StringBuilder("Failed to handle request [");
        message.append(request.getMethod());
        message.append(" ");
        message.append(request.getURI());
        message.append("]");
        if (ex != null) {
            message.append(": ");
            message.append(ex.getMessage());
            if(StringUtils.isNotEmpty(errorMessage)){
                message.append("("+errorMessage+")");
            }
        }
        return message.toString();
    }

    /**
     * 构建返回的JSON数据格式
     * @param status        状态码
     * @param errorMessage  异常信息
     * @return
     */
    public static Map<String, Object> response(int status, String errorMessage, HttpStatus httpStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("httpStatus", httpStatus);
        map.put("status", status);
        map.put("errorMessage", errorMessage);
        Map<String, Object> mapbody = new HashMap<>(3);
        mapbody.put("code", status);
        mapbody.put("msg", errorMessage);
        mapbody.put("body", null);
        map.put("body", mapbody);
        return map;
    }
}
