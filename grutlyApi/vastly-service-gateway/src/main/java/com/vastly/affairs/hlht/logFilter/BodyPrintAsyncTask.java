package com.vastly.affairs.hlht.logFilter;

import com.vastly.affairs.util.MinioUtils;
import com.vastly.affaris.hlht.mongoDB.service.VastlyGatewayLogService;
import com.vastly.ymh.core.affairs.entity.LogDbFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * * 超大报文接口body 异步输出
 * 
 * @author ymh
 *
 */
@Component
public class BodyPrintAsyncTask {
	private static final Log log = LogFactory.getLog(BodyPrintAsyncTask.class);

	@Autowired
	private VastlyGatewayLogService vastlyGatewayLogService;

	@Autowired
	private MinioUtils minioUtils;

	/**
	 * 异步报文输出打印
	 * @param logDTO
	 * @param type
	 */
	@Async
	public void YjdmAsyncTaskBuss(LogFilter logDTO, String type) {
		//todo 处理请求
		if(logDTO.getRequestBodyBit() != null && logDTO.getRequestBodyBit().length>0){
			logDTO.setRequestBody(LogHelper.reqBodyLog(minioUtils,logDTO.getRequestBodyBit(), logDTO.getRequestMediaType(),logDTO.getRequestHttpHeaders()));
			logDTO.setResponseBodySize(logDTO.getRequestBodyBit().length);
			logDTO.setRequestBodyBit(null);
		}

		//如果是异常结果，不进行处理
		//todo 处理响应报文
		if(!logDTO.getLogType().equals(LogFilter.TYPE.EXCEPTION)){
			if(logDTO.getResponseBodyBit() != null && logDTO.getResponseBodyBit().length>0){
				logDTO.setResponseBody(LogHelper.respBodyLog(minioUtils,logDTO.getResponseBodyBit(), logDTO.getResponseMediaType(),logDTO.getResponseHttpHeaders()));
				logDTO.setResponseBodySize(logDTO.getResponseBodyBit().length);
				logDTO.setResponseBodyBit(null);
			}
		}

		if(LogFilter.LEVEL.INFO.equals(logDTO.getLevel())){
			log.info(type+logDTO.toString());
		}else if(LogFilter.LEVEL.ERROR.equals(logDTO.getLevel())){
			log.error(type+logDTO.toString());
		}else if(LogFilter.LEVEL.WARN.equals(logDTO.getLevel())){
			log.warn(type+logDTO.toString());
		}else  if(LogFilter.LEVEL.TRACE.equals(logDTO.getLevel())){
			log.trace(type+logDTO.toString());
		}else{
			log.debug(type+logDTO.toString());
		}
		LogDbFilter logdb = new LogDbFilter();
		BeanUtils.copyProperties(logDTO,logdb);
		vastlyGatewayLogService.logSave(logdb);
	}

	


}
