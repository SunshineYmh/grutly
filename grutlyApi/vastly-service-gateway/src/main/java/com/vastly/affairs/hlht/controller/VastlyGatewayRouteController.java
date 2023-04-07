package com.vastly.affairs.hlht.controller;


import com.vastly.affairs.hlht.handler.GatewayServiceHandler;
import com.vastly.ymh.core.affairs.entity.GatewayRoute;
import com.vastly.ymh.core.affairs.entity.LogFilter;
import com.vastly.ymh.hlht.config.dto.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 路由管理
 */
@RestController
@RequestMapping("/Vastly/route")
@Slf4j
public class VastlyGatewayRouteController {

    @Autowired
    private GatewayServiceHandler gatewayServiceHandler;

    /**
     * 路由刷新
     * @return
     * @
     */
    @RequestMapping(value = "/loadRoute", method = RequestMethod.GET)
    @ResponseBody
    public DataResult loadRoute()  {
        return gatewayServiceHandler.loadRouteConfig();
    }

    @RequestMapping(value = "/routeQuery", method = RequestMethod.POST)
    @ResponseBody
    public DataResult routeQuery(@RequestBody GatewayRoute gatewayRoute)  {
        return gatewayServiceHandler.routeQuery(gatewayRoute);
    }

    /**
     * 新增路由
     * @param gatewayRoute
     * @return
     * @
     */
    @RequestMapping(value = "/saveRoute", method = RequestMethod.POST)
    @ResponseBody
    public DataResult saveRoute(@RequestBody GatewayRoute gatewayRoute)  {
        log.info("新增保存路由："+gatewayRoute.toString());
        return gatewayServiceHandler.saveRoute(gatewayRoute);
    }

    /**
     * 修改路由
     * @param gatewayRoute
     * @return
     * @
     */
    @RequestMapping(value = "/updateRoute", method = RequestMethod.POST)
    @ResponseBody
    public DataResult updateRoute(@RequestBody GatewayRoute gatewayRoute)  {
        log.info("修改路由："+gatewayRoute.toString());
        return gatewayServiceHandler.updateRoute(gatewayRoute);
    }

    /**
     * 新增路由
     * @param routeId
     * @return
     * @
     */
    @RequestMapping(value = "/deleteRoute", method = RequestMethod.GET)
    @ResponseBody
    public DataResult deleteRoute(@RequestParam String routeId){
        log.info("删除路由："+routeId);
        return gatewayServiceHandler.deleteRoute(routeId);
    }
}
