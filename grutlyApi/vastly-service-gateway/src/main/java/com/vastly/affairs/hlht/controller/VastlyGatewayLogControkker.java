package com.vastly.affairs.hlht.controller;

import com.vastly.affaris.hlht.mongoDB.service.VastlyGatewayLogService;
import com.vastly.ymh.core.affairs.entity.LogFilter;
import com.vastly.ymh.hlht.config.dto.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Vastly/test")
@Slf4j
public class VastlyGatewayLogControkker {

    @Autowired
    private VastlyGatewayLogService vastlyGatewayLogService;

    @RequestMapping(value = "/one", method = RequestMethod.POST)
    @ResponseBody
    public DataResult one(@RequestBody LogFilter logf) throws InterruptedException {
        log.info(logf.toString());
        return vastlyGatewayLogService.getLogQuery(logf);
    }
}
