package com.vastly.affairs.hlht.communtion;

import com.vastly.affairs.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 业务异步执行类
 *
 */
@Component
public class VastlyAsyncBussTask {
    Logger log = LoggerFactory.getLogger(VastlyAsyncBussTask.class);

    /**
     * 文件存储
     * @param bytes 存储数据
     * @param filePath  存储文件路径
     */
    @Async
    public void saveFileTask(byte[] bytes,String filePath){
        boolean b = FileUtils.writeFileByBytes(filePath, bytes);
        if(b){
            log.info("存储文件完成");
        }else{
            log.info("存储文件失败");
        }
    }
}
