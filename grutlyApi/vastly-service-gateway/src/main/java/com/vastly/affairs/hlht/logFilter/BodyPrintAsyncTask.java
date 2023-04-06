package com.vastly.affairs.hlht.logFilter;

import com.vastly.affaris.hlht.mongoDB.service.VastlyGatewayLogService;
import com.vastly.ymh.core.affairs.entity.LogFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
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
		vastlyGatewayLogService.logSave(logDTO);
	}

	


}
