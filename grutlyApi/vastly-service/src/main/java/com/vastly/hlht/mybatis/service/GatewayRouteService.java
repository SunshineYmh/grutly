package com.vastly.hlht.mybatis.service;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.vastly.hlht.core.affairs.entity.GatewayRoute;
import com.vastly.hlht.hlht.config.dto.DataResult;
import com.vastly.hlht.mybatis.dao.GatewayRouteServiceDao;
import lombok.extern.slf4j.Slf4j;


import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * * 路由信息表
 * @author ymh
 *
 */
@DS("master")
@Slf4j
@Service
public class GatewayRouteService {



    @Resource
    GatewayRouteServiceDao gatewayRouteServiceDao;


    /**
     * *路由信息表
     * HfbTqwtdbBean
     */
    public DataResult loadRouteConfig() {
        try {
            List<GatewayRoute> beanList = gatewayRouteServiceDao.loadRouteConfig();
            if(beanList.size()>0) {
               return DataResult.success(beanList,beanList.size());
            }else {
                return DataResult.error(404,"查询无数据！");
            }
        } catch (Exception e) {
            String trace = "路由信息查询失败:" + e.getMessage();
            return DataResult.error(trace);
        }
    }


    public DataResult routeQuery(GatewayRoute bean) {
        try {
            List<GatewayRoute> beanList = gatewayRouteServiceDao.routeQuery(bean);
            if(beanList.size()>0) {
                return DataResult.success(beanList,beanList.size());
            }else {
                return DataResult.error(404,"查询无数据！");
            }
        } catch (Exception e) {
            String trace = "路由信息查询失败:" + e.getMessage();
            return DataResult.error(trace);
        }
    }


    /**
     * *路由信息新增
     * HfbTqwtdbBean
     */
    public DataResult addRouteConfig(GatewayRoute bean) {
        try {
            int ret = gatewayRouteServiceDao.addRouteConfig(bean);
            if(ret == 1) {
                return DataResult.success();
            }else {
                return DataResult.error("路由信息新增失败！");
            }
        } catch (Exception e) {
            String trace = "路由信息新增失败:" + e.getMessage();
            return DataResult.error(trace);
        }
    }


    /**
     * *路由信息修改
     * HfbTqwtdbBean
     */
    public DataResult updateRouteConfig(GatewayRoute bean) {
        try {
            int ret = gatewayRouteServiceDao.updateRouteConfig(bean);
            if(ret >= 1) {
                return DataResult.success();
            }else {
                return DataResult.error("路由信息修改失败！");
            }
        } catch (Exception e) {
            String trace = "路由信息修改失败:" + e.getMessage();
            return DataResult.error(trace);
        }
    }


    /**
     * *路由信息删除
     * HfbTqwtdbBean
     */
    public DataResult deletePulsRoute(List<GatewayRoute> bean) {
        try {
            int ret = gatewayRouteServiceDao.deletePulsRoute(bean);
            if(ret >= 1) {
                return DataResult.success();
            }else {
                return DataResult.error("路由信息删除失败！");
            }
        } catch (Exception e) {
            String trace = "路由信息删除失败:" + e.getMessage();
            return DataResult.error(trace);
        }
    }

    public DataResult deleteRouteConfig(GatewayRoute bean) {
        try {
            int ret = gatewayRouteServiceDao.deleteRouteConfig(bean);
            if(ret >= 1) {
                return DataResult.success();
            }else {
                return DataResult.error("路由信息删除失败！");
            }
        } catch (Exception e) {
            String trace = "路由信息删除失败:" + e.getMessage();
            return DataResult.error(trace);
        }
    }
}
