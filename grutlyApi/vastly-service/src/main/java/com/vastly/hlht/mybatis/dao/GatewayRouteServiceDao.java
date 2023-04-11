package com.vastly.hlht.mybatis.dao;


import com.vastly.hlht.core.affairs.entity.GatewayRoute;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * * 路由信息
 * @author ymh
 *
 */
@Mapper
public interface GatewayRouteServiceDao {

	/**
	 * *路由信息列表
	 * @return
	 */
	@Select("select * from `vastly_gateway_route` where `isValid` = 1  order by `createDate` desc")
	public List<GatewayRoute> loadRouteConfig();


	@Select("<script>" +
		      " select * from `vastly_gateway_route`  where 1 = 1 " +
			"        <if test=\"routeId != null and routeId != '' \" > " +
			"            and `routeId` = #{routeId} " +
			"        </if> " +
			"        <if test=\"uid != null and uid != '' \" > " +
			"            and `uid` = #{uid} " +
			"        </if> " +
			"        <if test=\"host != null and host != '' \" > " +
			"            and  locate(#{host},host) " +
			"        </if> " +
			"        <if test=\"routeName != null and routeName != '' \" > " +
			"            and  locate(#{routeName},routeName) " +
			"        </if> " +
			"        <if test=\"path != null and path != '' \" > " +
			"            and  locate(#{path},path) " +
			"        </if> " +
			"        order by `createDate` desc "
			+ " </script>")
	public List<GatewayRoute> routeQuery(GatewayRoute bean);



	/**
	 * *路由信息新增
	 * @param bean
	 * @return
	 */
	@Insert(" INSERT INTO vastly_gateway_route " +
			" (routeId, uid,  routeName, protocol, `host`, `path`, version, timeout, stripPrefix," +
			" burstCapacity, replenishRate, requestedTokens, resolverKey, retry, isValid, isPublic, remarks) " +
			" VALUES(#{routeId}, #{uid},  #{routeName}, #{protocol}, #{host}, #{path}, #{version}, " +
			" #{timeout}, #{stripPrefix}, #{burstCapacity}, #{replenishRate}, #{requestedTokens}, #{resolverKey}, " +
			" #{retry}, 0, #{isPublic}, #{remarks}) ")
	public int addRouteConfig(GatewayRoute bean);


	/**
	 * *路由信息修改
	 * @param bean
	 * @return
	 */
	@Update("<script>" +
			"        UPDATE `vastly_gateway_route` SET " +
			"        <if test=\"routeName != null and routeName != '' \" > " +
			"            `routeName` = #{routeName} , " +
			"        </if> " +
			"        <if test=\"protocol != null and protocol != '' \" > " +
			"            `protocol` = #{protocol} , " +
			"        </if> " +
			"        <if test=\"host != null and host != '' \" > " +
			"            `host` = #{host} , " +
			"        </if> " +
			"        <if test=\"path != null and path != '' \" > " +
			"            `path` = #{path} , " +
			"        </if> " +
			"        <if test=\"stripPrefix != null and stripPrefix != '' and stripPrefix != 0 \" > " +
			"            `stripPrefix` = #{stripPrefix} , " +
			"        </if> " +
			"        <if test=\"burstCapacity != null and burstCapacity != '' and burstCapacity != 0  \" > " +
			"            `burstCapacity` = #{burstCapacity} , " +
			"        </if> " +
			"        <if test=\"replenishRate != null and replenishRate != '' and replenishRate != 0 \" > " +
			"            `replenishRate` = #{replenishRate} , " +
			"        </if> " +
			"        <if test=\"requestedTokens != null and requestedTokens != '' and requestedTokens != 0 \"> " +
			"            `requestedTokens` = #{requestedTokens} , " +
			"        </if> " +
			"        <if test=\"remarks != null and remarks != '' \" > " +
			"            `remarks` = #{remarks} , " +
			"        </if> " +
			"        <if test=\"isValid != null and isValid != '' \" > " +
			"            `isValid` = #{isValid} , " +
			"        </if> " +
			"        <if test=\"resolverKey != null and resolverKey != '' \" > " +
			"            `resolverKey` = #{resolverKey} , " +
			"        </if> " +
			"        <if test=\"retry != null and retry != '' \" > " +
			"            `retry` = #{retry} , " +
			"        </if> " +
			"        <if test=\"isPublic != null and isPublic != '' \" > " +
			"            `isPublic` = #{isPublic} , " +
			"        </if> " +
			"        WHERE   `routeId` = #{routeId}" +
			"</script>")
	public int updateRouteConfig(GatewayRoute bean);


	/**
	 * *路由信息删除
	 * @param bean
	 * @return
	 */
	@Delete("delete from `vastly_gateway_route` where `routeId` = #{routeId}")
	public int deleteRouteConfig(GatewayRoute bean);

	@Delete("<script>" +
			"		delete from `vastly_gateway_route` where `routeId` in " +
			"        <foreach collection=\"list\" item=\"iten\" open=\"(\" separator=\",\" close=\")\"> " +
			"            #{iten.routeId,jdbcType=VARCHAR} " +
			"        </foreach>" +
			"</script>")
	public int deletePulsRoute(List<GatewayRoute> bean);

}
