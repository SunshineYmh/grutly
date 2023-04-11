package com.vastly.hlht.hystrix;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class FallbackController {
    private static final Log log = LogFactory.getLog(FallbackController.class);

    @RequestMapping(value = "/fallback")
    public void FallbackAController() {
        throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,"服务熔断降级，服务暂时不可用");
//        log.error("服务熔断降级，服务暂时不可用");
//        JSONObject response = new JSONObject();
//        response.put("code","100");
//        response.put("msg","服务暂时不可用");
//        return response.toString();
    }
}
