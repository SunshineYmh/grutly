package com.vastly.hlht.communtion;

import com.vastly.hlht.constant.HeaderConstant;
import com.vastly.hlht.logFilter.LogHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * * 请求业务处理
 */
@Component
public class HttpRequestCommuntion {
    Logger log = LoggerFactory.getLogger(HttpRequestCommuntion.class);

    @Resource
    VastlyAsyncBussTask  vastlyAsyncBussTask;

    public static  String lineSeparator = System.getProperty("line.separator");
    public static  String filePathSplit = File.separator;
    //生成请求报文
    public static  String Reques_DAT_filePath = System.getProperty("user.dir")+filePathSplit+"data"+filePathSplit;

    /**
     * 设置请求参数
     * @param request  请求
     * @param requestId  请求服务id
     * @param startTime  请求开始时间
     * @return
     */
    public Consumer<HttpHeaders>  setHttpRequestHeaders(ServerHttpRequest request, AtomicReference<String> requestId,
                                             long startTime){
        Consumer<HttpHeaders> httpHeadersConsumer = httpHeaders -> {
            String headerRequestId = request.getHeaders().getFirst(HeaderConstant.REQUEST_ID);
            if (StringUtils.isBlank(headerRequestId)) {
                httpHeaders.set(HeaderConstant.REQUEST_ID, requestId.get());
            } else {
                requestId.set(headerRequestId);
            }
            //设置开始时间
            httpHeaders.set(HeaderConstant.START_TIME_KEY, String.valueOf(startTime));
            httpHeaders.putAll(request.getHeaders());
            //设置业务逻辑请求头
            Map<String,String> setHeaders =  new HashMap<String,String>();
            setHeaders.put("COondend","测试用的");
            if(setHeaders != null && setHeaders.size()>0){
                for(Map.Entry<String, String> headerEntry :setHeaders.entrySet()){
                    httpHeaders.set(headerEntry.getKey(), LogHelper.StringEscapeUnescape(String.valueOf(headerEntry.getValue())));
                }
            }
        };

        return httpHeadersConsumer;
    }

    /**
     * *  请求报文修改处理
     * @return
     */
//    public String ServerBodyBussTask(byte[] bytes, MediaType mediaType,String uuid,String logType){
//        String newReqBody = "";
//        log.info("mediaType==="+mediaType);
//        if (Objects.nonNull(mediaType) && LogHelper.isUploadFile(mediaType)) {
//            JSONObject masg = new JSONObject();
//            if(bytes.length>0){
//                String date = UtilTool.formatDateTime11();
//                String fileType = LogHelper.getFileType(mediaType);
//                String fileName = logType+"_"+date+"_"+uuid+fileType;
//                String filePath = Reques_DAT_filePath+date+filePathSplit+fileName;
//                masg.put("filesize" ,LogHelper.getNetFileSizeDescription(bytes.length));
//                masg.put("mediaType",mediaType.toString());
//                masg.put("fileName",fileName);
//                masg.put("filePath",filePath);
//                masg.put("logType",logType);
//                log.info("==>>>>"+masg.toJSONString());
//                vastlyAsyncBussTask.saveFileTask(bytes,filePath);
//            }else{
//                masg.put("filesize" ,0);
//                masg.put("mediaType",mediaType.toString());
//                masg.put("fileName","");
//                masg.put("filePath","");
//                masg.put("logType","");
//            }
//            newReqBody = masg.toJSONString();
//        }else{
//            if("request".equals(logType)){
//                newReqBody = new String(bytes, LogHelper.getMediaTypeCharset(mediaType));
//               /* log.info("读取到请求报文："+newReqBody);
//                newReqBody = "{\n" +
//                        "    \"url\":\"http://localhost:8098/sss.cvs\"\n" +
//                        "}";*/
//            }else if("response".equals(logType)){
//                newReqBody = new String(bytes, LogHelper.getMediaTypeCharset(mediaType));
//                /*log.info("读取到响应报文："+newReqBody);
//                newReqBody = "{\n" +
//                        "    \"body\":\"我是响应报文\"\n" +
//                        "}";*/
//            }
//
//        }
//        return newReqBody;
//    }

    /**
     * *  请求/响应报文修改处理
     * @return
     */
    public byte[]  ServerBodyBussTask(byte[]  bytes, MediaType mediaType,String uuid,String logType){

        return bytes;
    }






}
