
package com.vastly.hlht.hlht.config.dto;

/** 

* @fileName 文件名：

* @author 作者 : ShaoSong 

* @createDate 创建时间：2020年6月16日 下午3:30:56 

* @update 修改人 :

* @Description 描述 :

* @classDescription 类说明 : 

* @version 版本号: 4.0

*/

/**
 * 响应 Message 信息定义类
 */
public class ResponseMessageConstant {
    private ResponseMessageConstant() {}

    public static final String REQUEST_SUCCESS                         = "请求成功";
    public static final String REQUEST_FAILED                          = "请求失败";
    public static final String SYSTEM_ERROR                            = "系统错误";
    public static final String APP_EXCEPTION                           = "应用程序异常";
    public static final String OAUTH_TOKEN_MISSING                     = "token 缺失";
    public static final String OAUTH_TOKEN_ILLEGAL                     = "token 格式非法或已失效";
    public static final String OAUTH_TOKEN_DENIED                      = "token 权限不足";
    public static final String OAUTH_TOKEN_CHECK_ERROR                 = "token 验证失败";
    public static final String SIGN_CHECK_ERROR                        = "签名验证失败";
    public static final String LOGOUT_SUCCESS 		                   = "该用户已经注销！";
    public static final String LOGOUT_FAILED                           = "该用户注销失败！";
    public static final String PLEASE_CLIENT_ID_IN 		               = "您所使用的clientID请确认是否存在！";
    public static final String QUEREN_USERINFO		                   = "查询用户信息成功该信息请确实！";
    public static final String YOUR_IS_YOUR 			               = "请确实您使用的用户名是否是您自己的用户名！";
    public static final String QUEREN_NEWS_COLUMN_SUCCESS              = "查询新闻栏目成功！";
    public static final String QUEREN_NEWS_COLUMN_ERROR                = "查询新闻栏目失败！";
    public static final String NEWS_NEWSRELEASE_SUCCESS                = "新闻发布成功！";
    public static final String NEWS_NEWSRELEASE_ERROR                  = "新闻发布失败！";
    public static final String QUERY_CWMAIN_DATA_SUCCESS               = "查询主页财务数据成功！";
    public static final String QUERY_YYMAIN_DATA_SUCCESS               = "查询主页运营数据成功！";
    public static final String QUERY_SHZYXYMAIN_DATA_SUCCESS           = "查询主页社会经济效益数据成功！";
    public static final String QUERY_FXZKMAIN_DATA_SUCCESS             = "查询主页风险状况数据成功！";
    public static final String QUERY_DATA_NULL                         = "查询暂无该数据！";
    public static final String QUERY_PARAM_NULL                        = "查询参数缺失！";
    public static final String QUERY_TYPE_ERROR                        = "查询类型错误或没有该类型！";
    public static final String ZJLDXFX_QUEREN_SUCCESS                  = "资金流动性风险查询成功！";
    public static final String ZJLDXFX_QUEREN_ERROR                    = "资金流动性风险查询失败！";
    public static final String DEZJLRLC_QUEREN_SUCCESS                 = "大额资金流入查询成功！";
    public static final String DEZJLRLC_QUEREN_ERROR                   = "大额资金流入查询失败！";
    public static final String GRZFDKYQL_QUEREN_SUCCESS                = "个人住房贷款逾期率查询成功！";
    public static final String GRZFDKYQL_QUEREN_ERROR                  = "个人住房贷款逾期率查询失败！";
    public static final String ZJLDXFXMX_QUEREN_SUCCESS                = "资金流动性风险查询成功！";
    public static final String ZJLDXFXMX_QUEREN_ERROR                  = "资金流动性风险查询失败！";
    public static final String DEZJLRLCMX_QUEREN_SUCCESS               = "大额资金流动预警明细查询成功！";
    public static final String DEZJLRLCMX_QUEREN_ERROR                 = "大额资金流动预警明细查询失败！";
    public static final String GRZFDKYQLMX_QUEREN_SUCCESS              = "个人贷款逾期预警明细查询成功！";
    public static final String GRZFDKYQLMX_QUEREN_ERROR                = "个人贷款逾期预警明细查询失败！";
    public static final String QUERY_YYJCMAIN_DATA_SUCCESS             = "查询运营缴存期数据成功！";
    public static final String QUERY_YYJCMAIN_CITYDATA_SUCCESS         = "查询运营缴存各城市数据成功！";
    public static final String DELTE_NEWS_SUCCESS					   = "删除新闻成功！";
    public static final String DELTE_NEWS_ERROR					       = "删除新闻失败！";
    public static final String QUERY_YYTQMAIN_DATA_SUCCESS             = "查询运营提取期数据成功！";
    public static final String QUERY_YYTQMAIN_CITYDATA_SUCCESS         = "查询运营提取各城市数据成功！";
    public static final String QUERY_YYTXDKMAIN_DATA_SUCCESS           = "查询运营贴息贷款期数据成功！";
    public static final String QUERY_YYTXDKMAIN_CITYDATA_SUCCESS       = "查询运营贴息贷款各城市数据成功！";
    public static final String QUERY_YYZFXMDKMAIN_DATA_SUCCESS         = "查询运营住房项目贷款期数据成功！";
    public static final String QUERY_YYZFXMDKMAIN_CITYDATA_SUCCESS     = "查询运营住房项目贷款各城市数据成功！";
    public static final String QUERY_CWGZMAIN_DATA_SUCCESS             = "查询财务国债期数据成功！";
    public static final String QUERY_CWGZMAIN_CITYDATA_SUCCESS         = "查询财务国债各城市数据成功！";
    public static final String QUERY_CWRZMAIN_DATA_SUCCESS             = "查询财务融资期数据成功！";
    public static final String QUERY_CWRZMAIN_CITYDATA_SUCCESS         = "查询财务融资各城市数据成功！";
    public static final String QUERY_AUTH_URL_SUCCESS                  = "查询权限成功！";
    public static final String QUERY_AUTH_URL_ERROR                    = "查询权限失败！";
    public static final String INSERT_USERINFO_SUCCESS                 = "用户信息添加成功！";
    public static final String INSERT_USERINFO_ERROR                   = "用户信息添加失败请确认用戶名是否唯一！";
    public static final String INSERT_USERINFO_ID_ERROR                = "用户信息添加失败请确认身份证有关用户是否超过5个！";
    public static final String PASSWORD_ERROR                          = "用户密码错误！";
    public static final String PASSWORD_YX_ERROR                       = "原密码与新密码相同无法修改！";
    public static final String LOGOUT_USE_NYNAME                       = "注销用户使用起他人token尝试注销被人账号！";
    public static final String LOGOUT_USE_NYUSER                       = "注销使用的用户未登陆！";
    public static final String INSERT_YJSJ_SUCCESS                     = "预警数据插入成功！";
    public static final String INSERT_YJSJ_ERROR                       = "预警数据插入失败！";
}
