package com.vastly.ymh.mybatis.affairs.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.vastly.ymh.core.affairs.entity.GatewayRoute;
import com.vastly.ymh.hlht.config.dto.DataResult;
import com.vastly.ymh.hlht.config.dto.ResultJsonUtil;
import com.vastly.ymh.hlht.config.dto.UtilTools;
import com.vastly.ymh.mybatis.affairs.dao.GatewayRouteServiceDao;
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
        DataResult result = new DataResult();
        try {
            List<GatewayRoute> beanList = gatewayRouteServiceDao.loadRouteConfig();
            if(beanList.size()>0) {
                result.setSuccess(true);
                result.setData(beanList);
                result.setTotalcount(beanList.size());
            }else {
                result.setSuccess(false);
                result.setMsg("查询无数据");
            }
        } catch (Exception e) {
            String trace = "路由信息查询失败:" + e.getMessage();
            log.error(trace);
            result.setSuccess(false);
            result.setMsg("查询失败！");
        }
        return result;
    }


    public DataResult loadRouteQuery(GatewayRoute bean) {
        DataResult result = new DataResult();
        try {
            List<GatewayRoute> beanList = gatewayRouteServiceDao.loadRouteQuery(bean);
            if(beanList.size()>0) {
                result.setSuccess(true);
                result.setData(beanList);
                result.setTotalcount(beanList.size());
            }else {
                result.setSuccess(true);
                result.setMsg("查询无数据");
                result.setData(new JSONArray());
            }
        } catch (Exception e) {
            String trace = "路由信息查询失败:" + e.getMessage();
            log.error(trace);
            result.setSuccess(false);
            result.setMsg("查询失败！");
        }
        return result;
    }


    /**
     * *路由信息新增
     * HfbTqwtdbBean
     */
    public DataResult addRouteConfig(GatewayRoute bean) {
        bean.setId(UtilTools.getUuid());
        DataResult result = new DataResult();
        try {
            int ret = gatewayRouteServiceDao.addRouteConfig(bean);
            if(ret == 1) {
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
                result.setMsg("路由信息新增失败！");
            }
        } catch (Exception e) {
            String trace = "路由信息新增失败:" + e.getMessage();
            log.error(trace);
            result.setSuccess(false);
            result.setMsg("路由信息新增失败！");
        }
        return result;
    }


    /**
     * *路由信息修改
     * HfbTqwtdbBean
     */
    public DataResult updateRouteConfig(GatewayRoute bean) {
        DataResult result = new DataResult();
        try {
            int ret = gatewayRouteServiceDao.updateRouteConfig(bean);
            if(ret == 1) {
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            String trace = "路由信息新增失败:" + e.getMessage();
            log.error(trace);
            result.setSuccess(false);
            result.setMsg("查询失败！");
        }
        return result;
    }


    /**
     * *路由信息删除
     * HfbTqwtdbBean
     */
    public DataResult deletePulsRoute(List<GatewayRoute> bean) {
        DataResult result = new DataResult();
        try {
            int ret = gatewayRouteServiceDao.deletePulsRoute(bean);
            if(ret >= 1) {
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            String trace = "路由信息新增失败:" + e.getMessage();
            log.error(trace);
            result.setSuccess(false);
            result.setMsg("查询失败！");
        }
        return result;
    }

    public DataResult deleteRouteConfig(GatewayRoute bean) {
        DataResult result = new DataResult();
        try {
            int ret = gatewayRouteServiceDao.deleteRouteConfig(bean);
            if(ret == 1) {
                result.setSuccess(true);
            }else {
                result.setSuccess(false);
            }
        } catch (Exception e) {
            String trace = "路由信息新增失败:" + e.getMessage();
            log.error(trace);
            result.setSuccess(false);
            result.setMsg("查询失败！");
        }
        return result;
    }
}
