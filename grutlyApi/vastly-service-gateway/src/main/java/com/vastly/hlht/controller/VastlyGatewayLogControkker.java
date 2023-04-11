package com.vastly.hlht.controller;

import com.vastly.hlht.mongoDB.service.VastlyGatewayLogService;
import com.vastly.hlht.core.affairs.entity.LogDbFilter;
import com.vastly.hlht.hlht.config.dto.DataResult;
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
    public DataResult one(@RequestBody LogDbFilter logf) throws InterruptedException {
        log.info(logf.toString());
        return vastlyGatewayLogService.getLogQuery(logf);
    }
}
