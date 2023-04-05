package com.vastly.affairs.hlht.handler;


import com.vastly.ymh.core.affairs.entity.GatewayRoute;
import com.vastly.ymh.hlht.config.dto.DataResult;
import com.vastly.ymh.mybatis.affairs.service.GatewayRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/vastly/gateway/route")
public class RouteController {
 
	@Autowired
	private GatewayServiceHandler gatewayServiceHandler;
 
	//自己的获取数据dao
    @Autowired
    private GatewayRouteService gatewayRouteService;
	/**
	 * 刷新路由配置
	 *
	 * @return
	 */
	@GetMapping("/refresh")
	public DataResult refresh() throws Exception {
		return this.gatewayServiceHandler.loadRouteConfig();
	}
 
	/**
	 * 增加路由记录
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/add")
	public DataResult add(@RequestBody GatewayRoute gatewayRouteDto) throws Exception {
		gatewayRouteService.addRouteConfig(gatewayRouteDto);
		DataResult result = gatewayRouteService.loadRouteQuery(gatewayRouteDto);
		if(result.isSuccess()) {
			List<GatewayRoute> beanList = (List<GatewayRoute>) result.getData();
			GatewayRoute bean = beanList.get(0);
			gatewayServiceHandler.saveRoute(bean);
		}
		return result;
	}
 
	@SuppressWarnings("unchecked")
	@PostMapping("/update")
	public DataResult update(@RequestBody GatewayRoute gatewayRouteDto) throws Exception {
		gatewayRouteService.updateRouteConfig(gatewayRouteDto);
		DataResult result = gatewayRouteService.loadRouteQuery(gatewayRouteDto);
		if(result.isSuccess()) {
			List<GatewayRoute> beanList = (List<GatewayRoute>) result.getData();
			GatewayRoute bean = beanList.get(0);
			gatewayServiceHandler.update(bean);
		}
        return result;
	}
 
	@GetMapping("/delete")
	public DataResult delete(@RequestBody GatewayRoute gatewayRouteDto) throws Exception {
		DataResult result = new DataResult();
		try {
			gatewayRouteService.deleteRouteConfig(gatewayRouteDto);
			gatewayServiceHandler.deleteRoute(gatewayRouteDto.getId()+"");
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMsg("删除失败，"+e.getMessage());
		}
		return result;
	}
 
}