package com.vastly.affairs.hlht.logFilter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vastly.affairs.util.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * * 超大报文接口body 异步输出
 * 
 * @author ymh
 *
 */
@Component
public class BodyPrintAsyncTask {
	private static final Log log = LogFactory.getLog(BodyPrintAsyncTask.class);

	/**
	 * 异步报文输出打印
	 * @param logDTO
	 * @param type
	 */
	@Async
	public void YjdmAsyncTaskBuss(LogFilter logDTO, String type) {
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
	}

	


}
