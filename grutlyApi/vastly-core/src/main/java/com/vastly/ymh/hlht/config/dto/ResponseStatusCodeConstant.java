
package com.vastly.ymh.hlht.config.dto;

/** 

* @fileName 文件名：

* @author 作者 : ShaoSong 

* @createDate 创建时间：2020年6月16日 下午3:28:49 

* @update 修改人 :

* @Description 描述 :

* @classDescription 类说明 : 

* @version 版本号: 4.0

*/

/**
 * 响应 StatusCode 常量定义类
 */
public class ResponseStatusCodeConstant {
    private ResponseStatusCodeConstant() {}

    public static final int OAUTH_TOKEN_FAILURE = 2001; // token 失效
    public static final int OAUTH_TOKEN_MISSING = 2008; // token 缺失
    public static final int OAUTH_TOKEN_DENIED  = 2009; // token 权限不足
}
