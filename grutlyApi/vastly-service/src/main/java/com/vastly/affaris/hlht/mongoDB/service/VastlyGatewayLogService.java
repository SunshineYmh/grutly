package com.vastly.affaris.hlht.mongoDB.service;

import com.vastly.ymh.core.affairs.entity.LogFilter;
import com.vastly.ymh.hlht.config.dto.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class VastlyGatewayLogService {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存日志
     * @param logDto
     */
    public void logSave(LogFilter logDto){
        LogFilter result = mongoTemplate.save(logDto);
        if(result != null){
            DataResult.success();
        }else{
            DataResult.error(500,"保存数据失败！");
        }
    }

    /**
     * 根据 id查询
     * @param logDto
     */
    public void getLogById(LogFilter logDto){
        LogFilter result = mongoTemplate.findById(logDto.getId(),LogFilter.class);
        if(result != null){
            DataResult.success(result);
        }else{
            DataResult.error(404,"未找到相关数据！");
        }
    }

    public DataResult getLogQuery(LogFilter logDto){

        // 创建查询对象，然后将条件对象添加到其中
        //根据id字段来排序,如果想根据多个字段进行排序,可以在str字符串数组中添加字段
//        String[] str= {"id"};
//        query.with(Sort.by(Sort.Direction.DESC,str));//DESC降序,ASC是升序
        //创建排序规则
        Sort sort = Sort.by(Sort.Order.desc("startDate"));
        Pageable pageable = PageRequest.of(logDto.getPageNum(), logDto.getPageSize());
        Query query = new Query().with(Sort.by(Sort.Order.desc("startDate")))//DESC降序,ASC是升序
                .with(pageable);
        //查询时间区间
        Criteria dateqj = Criteria.where("startDate").gte(logDto.getStartDate()).lte(logDto.getEndDate());
        // 创建条件对象，将上面条件进行 and 关联
        Criteria criteria = new Criteria().andOperator(dateqj);
        if(StringUtils.isNotEmpty(logDto.getRouteId())){
            Criteria routeId = Criteria.where("routeId").is(logDto.getRouteId());
            query.addCriteria(routeId);
        }
        if(StringUtils.isNotEmpty(logDto.getRequestIp())){
            Criteria requestIp = Criteria.where("requestIp").is(logDto.getRequestIp());
            criteria.andOperator(requestIp);
        }
        if(StringUtils.isNotEmpty(logDto.getRequestPath())){
            Criteria requestPath = Criteria.where("requestPath").regex("*"+logDto.getRequestPath()+"*");
            query.addCriteria(requestPath);
        }
        if(StringUtils.isNotEmpty(logDto.getRequestMethod())){
            Criteria getRequestMethod = Criteria.where("requestMethod").is(logDto.getRequestMethod());
            query.addCriteria(getRequestMethod);
        }

        if(StringUtils.isNotEmpty(logDto.getRequestQueryParams())){
            Pattern pattern = Pattern.compile("^.*" + logDto.getRequestQueryParams() + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria requestQueryParams = Criteria.where("requestQueryParams").regex(pattern);
            query.addCriteria(requestQueryParams);
        }

        if(StringUtils.isNotEmpty(logDto.getRequestBody())){
            Pattern pattern = Pattern.compile("^.*" + logDto.getRequestBody() + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria requestBody = Criteria.where("requestBody").regex(pattern);
            query.addCriteria(requestBody);
        }
        if(logDto.getStatus() > 0){
            Criteria status = Criteria.where("status").is(logDto.getStatus());
            query.addCriteria(status);
        }
        log.info("查询sql【"+query.toString()+"】");
        //查询总数
        Long count = mongoTemplate.count(query,LogFilter.class);
        log.info("查询count="+count);
        if(count != null && count >0){
            List<LogFilter> result = mongoTemplate.find(query,LogFilter.class);
            return DataResult.success(result,count);
        }else{
            return DataResult.error(404,"未找到相关数据！");
        }
    }
}
