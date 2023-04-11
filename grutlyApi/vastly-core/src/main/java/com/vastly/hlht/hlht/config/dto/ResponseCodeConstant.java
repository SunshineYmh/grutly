
package com.vastly.hlht.hlht.config.dto;

/** 

* @fileName 文件名：

* @author 作者 : ShaoSong 

* @createDate 创建时间：2020年6月16日 下午3:28:12 

* @update 修改人 :

* @Description 描述 :

* @classDescription 类说明 : 

* @version 版本号: 4.0

*/

/**
 * 响应 Code 常量定义类
 */
public class ResponseCodeConstant {
    private ResponseCodeConstant() {}

    public static final int REQUEST_SUCCESS = 0; // 请求成功
    public static final int REQUEST_FAILED  = 99; // 请求失败
    public static final int SYSTEM_ERROR    = -1; // 系统错误
    public static final int REQUEST_PARAM   = 88; // 参数缺失
}
