package com.vastly.affaris.hlht.mongoDB.service;

import com.vastly.ymh.core.affairs.entity.LogFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VastlyGatewayLogService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void logSave(LogFilter log){
        mongoTemplate.save(log);
    }
}
