package com.vastly.affairs.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;


public class SysAffairsUtils {
	
	/**
	 * 获取dsp_service_config 配置信息的请求头信息配置，筛选过滤实际请求头的参数
	 * 如果请求头信息参数和配置表信息参数的字段key同时存在，优先获取实际请求的头信息。
	 * @param req_header
	 * @param req_dsp_header_data
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map<String, String> setHeaders(Map<String, String> req_header, Object req_dsp_header_data) {
		Map<String, String> req_header_ret = new HashMap<String, String>();
		JSONObject jsonObject = null;
		if (req_dsp_header_data == null) {
			return null;
		}
		if (StringUtils.isEmpty(req_dsp_header_data.toString())) {
			return null;
		}
		try {
			//获取dsp_service_config 配置信息的请求头信息配置
			jsonObject = JSONObject.parseObject(JSONObject.toJSONString( req_dsp_header_data));
			Iterator entry = (Iterator) jsonObject.keySet();
			while (entry.hasNext()) {
				String key = String.valueOf(entry.next().toString());
				String value = jsonObject.get(key).toString();
				if (req_header != null) {
					//遍历实际请求头信息
					String req_value = req_header.get(key);
					//如果请求头信息参数和配置表信息参数的字段key同时存在，优先获取实际请求的头信息。
					if (StringUtils.isNotEmpty(req_value)) {
						req_header_ret.put(key, req_value);
					}else {
						req_header_ret.put(key, value);
					}
				}else {
					req_header_ret.put(key, value);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return req_header_ret;
	}

}
