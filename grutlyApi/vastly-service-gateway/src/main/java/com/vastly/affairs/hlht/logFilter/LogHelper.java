package com.vastly.affairs.hlht.logFilter;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vastly.affairs.hlht.communtion.SpringContextUtil;
import com.vastly.affairs.hlht.communtion.CacheManager;
import com.vastly.affairs.hlht.constant.ContentType;
import com.vastly.affairs.hlht.constant.HeaderConstant;
import com.vastly.affairs.util.*;
import com.vastly.ymh.core.affairs.entity.LogFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class LogHelper {




    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final static Map<String , String> FILE_CONTENT_TYPE = ContentType.getAllFile();


//    public LogFilter() {
//        this(LogFilter.TYPE.REQUEST);
//    }
//
//    public LogFilter(LogFilter.TYPE logType) {
//        this.logType = logType;
//        this.hostName = IpUtils.getHostName();
//        this.timeStamp = ZonedDateTime.now(ZoneOffset.of("+08:00")).toString();
//        this.serverIp = IpUtils.getLocalIp();
//    }



    /**
     *字符串进行反转义处理
     * @param str
     * @return
     */
    public static String StringEscapeUnescape(String str){
        if(StringUtils.isEmpty(str)){
            return str;
        }
        return StringEscapeUtils.unescapeJava(str);
    }

    /**
     * *  获取文件的大小
     * @param size
     * @return 返回类型：单位：mb
     */
    public static String getNetFileSizeDescriptionMB(long size){
        String fileSizeStr = "0";
        double fileSize = new BigDecimal( (size / (1024.0 * 1024.0))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Math.round(fileSize) - fileSize == 0) {
            fileSizeStr = (String.valueOf((long) fileSize));
        }else {
            fileSizeStr = (String.valueOf(fileSize));
        }
        if(StringUtils.isEmpty(fileSizeStr) ||"0.0".equals(fileSizeStr) ||"0.00".equals(fileSizeStr)){
            fileSizeStr = "0";
        }
        return fileSizeStr;
    }

    public static String getNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        }
        else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            }
            else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    /**
     * Log转JSON
     * @param dto Log
     * @return JSON字符串
     */
    public static String toJsonString(@NonNull LogFilter dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Log转换JSON异常", e);
            return null;
        }
    }

    /**
     * 根据MediaType获取字符集，如果获取不到，则默认返回<tt>UTF_8</tt>
     * @param mediaType MediaType
     * @return Charset
     */
    public static Charset getMediaTypeCharset(@Nullable MediaType mediaType) {
        if (Objects.nonNull(mediaType) && mediaType.getCharset() != null) {
            return mediaType.getCharset();
        } else {
            return StandardCharsets.UTF_8;
        }
    }

    public static String getMediaTypeContentType(@Nullable MediaType mediaType) {
        if (Objects.nonNull(mediaType) && mediaType.getType() != null) {
            return mediaType.toString();
        } else {
            return "text/plain";
        }
    }

    public static String getMediaTypeContentBoundaryType(@Nullable MediaType mediaType) {
        if (Objects.nonNull(mediaType) && mediaType.getType() != null) {
            String type =  mediaType.toString();
            if(type.contains("boundary=")){
                String[] types = type.split(";",-1);
                return types[1].replace("boundary=","").trim();
            }
        }
        return null;
    }

    /**
     * 记录日志（后期可扩展为通过MQ将日志发送到ELK系统）
     * @param logDTO Log
     * @return Mono.empty()
     */
    public static Mono<Void> doRecord(BodyPrintAsyncTask bodyPrintAsyncTask,LogFilter logDTO,String type) {
        bodyPrintAsyncTask.YjdmAsyncTaskBuss(logDTO,type);
        return Mono.empty();
    }

    public static String reqBodyLog(MinioUtils minioUtils, byte[] RequestBody, MediaType mediaType, HttpHeaders headers){
        String requestBody = "";
        Charset charset =   getMediaTypeCharset(mediaType);
        //处理请求的文件数据文件上传
        if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType)) {
            String wrapperName =  LogHelper.getMediaTypeContentBoundaryType(mediaType);
           return FormDataAnalysisUtil.getMultipartFormData(minioUtils,RequestBody, wrapperName).toString();
        }else if(MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)){
            String parms = new String(RequestBody, charset);
            String[] parmss = parms.split("&",-1);
            JSONObject jsonpar = new JSONObject();
            for(String par : parmss){
                String[] pars = par.split("=",-1);
                try {
                    jsonpar.put(URLDecoder.decode(pars[0],charset.toString()),URLDecoder.decode(pars[1],charset.toString()));
                }catch (Exception e){
                }
            }
            requestBody = jsonpar.toJSONString();
        }else if(isUploadFile(mediaType)){
            return getFileData(minioUtils,RequestBody,mediaType,headers);
        }else{
            requestBody = new String(RequestBody, charset);
        }
        return requestBody;
    }


    public static String respBodyLog(MinioUtils minioUtils,byte[] RequestBody,MediaType mediaType,HttpHeaders headers){
        //处理响应，判断是否是文件
        if(isUploadFile(mediaType)){
            return getFileData(minioUtils,RequestBody,mediaType,headers);
        }else{
            return new String(RequestBody, Charset.forName("UTF-8"));
        }
    }

    public static String uploadObject(MinioUtils minioUtils,byte[] data,String fileName,String contentType){
        InputStream in = new ByteArrayInputStream(data);
        String filePath = DateUtils.getCurrentTime()+"/"+fileName;
        Boolean b = minioUtils.uploadObject( in, filePath,  data.length, contentType);
        if(b){
            return filePath;
        }else{
            return null;
        }
    }


    public static String getFileData(MinioUtils minioUtils,byte[] RequestBody,MediaType mediaType,HttpHeaders headers){
        Charset charset =   getMediaTypeCharset(mediaType);
        //获取文件名
        String filename = "";
        String  ContentDisposition =  headers.getFirst("Content-Disposition");
        if(ContentDisposition != null && ContentDisposition.contains("filename=")){
            String[] files = ContentDisposition.split(";");
            for(String file : files){
                if(file.contains("filename=")){
                    filename = file.replace("filename=","").trim();
                }
            }
        }
        String mediaTypeStr = mediaType.toString();
        String fileType = ".txt";
        if(StringUtils.isNotEmpty(mediaTypeStr)){
            for(Map.Entry<String, String> vo : FILE_CONTENT_TYPE.entrySet()){
                String v = vo.getValue();
                if(mediaTypeStr.contains(v)){
                    fileType = vo.getKey();
                    break;
                }
            }
        }
        if(StringUtils.isEmpty(filename)){
            filename = UUID.randomUUID().toString().replace("-", "")+fileType;
        }else{
            try {
                filename = URLDecoder.decode(filename,charset.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 获取文件后缀名
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            if(StringUtils.isNotEmpty(ext)){
                fileType = ext;
            }
        }
        JSONObject file = new JSONObject();
        file.put("filename",filename);
        file.put("fileSize",RequestBody.length);
        file.put("type",fileType);
        file.put("ContentType",mediaTypeStr);
        file.put("charset",charset);
        String filePath = uploadObject( minioUtils,RequestBody,filename,mediaTypeStr);
        file.put("filePath",filePath);
        return file.toString();
    }

    //        String filePath = "C:\\Users\\ymh\\Desktop\\test\\"+filename;
//        try {
//            FileUtils.writeByteArrayToFile(filePath,RequestBody);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    /**
     * 判断是否是上传文件
     * @param mediaType MediaType
     * @return Boolean
     */
    public static boolean isUploadFile(@Nullable MediaType mediaType) {
        if (Objects.isNull(mediaType)) {
            return false;
        }
        String mediaTypeStr = mediaType.toString();
        // 处理类似multipart/form-data; boundary=<calculated when request is sent>的情况
        mediaTypeStr = mediaTypeStr.split(";")[0];
        log.info(">mediaTypeStr>>>>>"+mediaTypeStr);

        return  MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType)||
                mediaTypeStr.equals(MediaType.IMAGE_GIF.toString())
                || mediaTypeStr.equals(MediaType.IMAGE_JPEG.toString())
                || mediaTypeStr.equals(MediaType.IMAGE_PNG.toString())
                || mediaTypeStr.equals(MediaType.APPLICATION_PDF.toString())
                || FILE_CONTENT_TYPE.containsValue(mediaTypeStr);
    }


    /**
     * 从HttpHeaders获取请求开始时间
     * <p>
     *     要求请求头中必须要有参数{@link HeaderConstant#START_TIME_KEY}，否则将返回当前时间戳
     * </p>
     * @param headers HttpHeaders请求头
     * @return 开始时间时间戳（Mills）
     */
    public static long getStartTime(HttpHeaders headers) {
        String startTimeStr = headers.getFirst(HeaderConstant.START_TIME_KEY);
        return StringUtils.isNotBlank(startTimeStr) ? Long.parseLong(startTimeStr) : System.currentTimeMillis();
    }

    public static String getREQUEST_ID(HttpHeaders headers) {
        String REQUEST_ID = headers.getFirst(HeaderConstant.REQUEST_ID);
        return StringUtils.isNotBlank(REQUEST_ID) ? REQUEST_ID : "";
    }

    /**
     * 根据HttpHeaders请求头获取请求执行时间
     * <p>
     *     要求请求头中必须要有参数{@link HeaderConstant#START_TIME_KEY}
     * </p>
     * @param headers HttpHeaders请求头
     * @return 请求执行时间
     */
    public static long getHandleTime(HttpHeaders headers) {
        String startTimeStr = headers.getFirst(HeaderConstant.START_TIME_KEY);
        long startTime = StringUtils.isNotBlank(startTimeStr) ? Long.parseLong(startTimeStr) : System.currentTimeMillis();
        return System.currentTimeMillis() - startTime;
    }
    //单位秒
    public static String getHandleSECONDSTime(long executeMILLISECONDSTime) {
        String executeTime = "0";
        double executeTimeMath = new BigDecimal((float)(executeMILLISECONDSTime)/1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Math.round(executeTimeMath) - executeTimeMath == 0) {
            executeTime = String.valueOf((long) executeTimeMath);
        }else {
            executeTime = String.valueOf(executeTimeMath);
        }
        if(StringUtils.isEmpty(executeTime) ||"0.0".equals(executeTime) ||"0.00".equals(executeTime)){
            executeTime = "0";
        }
        return executeTime;
    }

    /**
     * 读取请求体内容
     * @param request ServerHttpRequest
     * @return 请求体
     */
    public static String readRequestBody(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        MediaType mediaType = headers.getContentType();
        String method = request.getMethodValue().toUpperCase();
        if (Objects.nonNull(mediaType) && mediaType.equals(MediaType.MULTIPART_FORM_DATA)) {
            return "上传文件";
        } else {
            if (method.equals("GET")) {
                if (!request.getQueryParams().isEmpty()) {
                    return request.getQueryParams().toString();
                }
                return null;
            } else {
                AtomicReference<String> bodyString = new AtomicReference<>();
                request.getBody().subscribe(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    bodyString.set(new String(bytes, getMediaTypeCharset(mediaType)));
                });
                return bodyString.get();
            }
        }
    }




    public static String getFileType(@Nullable MediaType mediaType){
        String fileType = ".txt";
        if (Objects.isNull(mediaType)) {
            return ".txt";
        }
        String mediaTypeStr = mediaType.toString();
        for (Map.Entry<String, String> e : FILE_CONTENT_TYPE.entrySet()) {
            String value = e.getValue();
            String key = e.getKey();
            if(value.equals(mediaTypeStr.trim())){
                fileType = key;
            }
        }
        return fileType;
    }

    private static Route getGatewayRoute(ServerWebExchange exchange) {
        return exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
    }



        /**
     * 响应日志设置
     * @return
     */
    public static LogFilter respBuildExchangeLog(ServerWebExchange exchange, String responseBody,int code,String errorMessage,String exceptionMessage ) {
        ServerHttpRequest request = exchange.getRequest();
        Route route = getGatewayRoute(exchange);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(request.getHeaders());
        //记录日志
        final LogFilter logDTO = new LogFilter();
        logDTO.setLogType(LogFilter.TYPE.EXCEPTION);
        logDTO.setLevel(LogFilter.LEVEL.ERROR);
        logDTO.setHostName(IpUtils.getHostName());
        logDTO.setServerIp(IpUtils.getLocalIp());
        logDTO.setRequestIp(IpUtils.getClientIp(request));
        long time = getStartTime( headers);
        logDTO.setTimeStamp(DateUtils.conversionTime(time));
        logDTO.setStartDate(time);
        logDTO.setRouteId(route==null?"":route.getId());

        logDTO.setId(getREQUEST_ID( headers));
        // 原始请求体
        URI requri = request.getURI();
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

        //响应
        logDTO.setResponseContentType(MediaType.APPLICATION_JSON.toString());
        logDTO.setResponseBody(responseBody);
        logDTO.setResponseBodySize(responseBody.getBytes(Charset.forName("UTF-8")).length);
        logDTO.setStatus(code);
        logDTO.setErrorMessage(errorMessage);
        logDTO.setExceptionMessage(exceptionMessage);

        long responseDate = System.currentTimeMillis();
        logDTO.setEndDate(responseDate);
        // 计算执行时间
        long executeTime = (responseDate - logDTO.getStartDate());
        logDTO.setExecuteTime(executeTime);
        return logDTO;
    }


}


//
//    /**
//     * 请求日志设置
//     * @param request
//     * @param logDTO
//     * @param requestBody
//     * @param httpHeaders
//     * @return
//     */
//    public static LogFilter reqBuildLog(ServerHttpRequest request, LogFilter logDTO, AtomicReference<String> requestBody,
//                                        HttpHeaders httpHeaders) {
//        HttpHeaders headers = request.getHeaders();
//        String requestId = headers.getFirst(HeaderConstant.REQUEST_ID);
//        String method = request.getMethodValue().toUpperCase();
//        URI requestUri = request.getURI();
//        String uriQuery = requestUri.getQuery();
//        String path = requestUri.getPath();
//        String url = path + (StringUtils.isNotBlank(uriQuery) ? "?" + uriQuery : "");
//        String schema = requestUri.getScheme();
//        String requestContentType = headers.getContentType().toString();
//
//        logDTO.setRequestContentType(requestContentType);
//        logDTO.setLevel(LogFilter.LEVEL.INFO);
//        logDTO.setRequestUri(url);
//        logDTO.setRequestMethod(method);
//        logDTO.setSchema(schema);
//        logDTO.setRequestId(requestId);
//        logDTO.setRequestIp(IpUtils.getClientIp(request));
//        logDTO.setRequestHeaders(httpHeaders.toSingleValueMap());
//        logDTO.setRequestBody(requestBody.get());
//
//        cacheManager.put(requestId, JSONObject.toJSONString(logDTO));
//        return logDTO;
//    }
//
//

