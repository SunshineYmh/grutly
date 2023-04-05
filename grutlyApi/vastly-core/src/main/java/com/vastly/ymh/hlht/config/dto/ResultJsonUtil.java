
package com.vastly.ymh.hlht.config.dto;

/** 

* @fileName 文件名：

* @author 作者 : ShaoSong 

* @createDate 创建时间：2020年6月16日 下午3:26:04 

* @update 修改人 :

* @Description 描述 :

* @classDescription 类说明 : 

* @version 版本号: 4.0

*/

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 返回结果构建处理
 * @param <T>
 */
//@ApiModel("api通用返回数据")
@Getter
@Setter
@AllArgsConstructor //会生成一个包含所有变量
@NoArgsConstructor//生成一个无参数的构造方法
public class ResultJsonUtil<T> implements Serializable {

    //uuid,用作唯一标识符，供序列化和反序列化时检测是否一致
    private static final long serialVersionUID = 7498483649536881777L;

    //标识代码，0表示成功，非0表示出错
    private Integer     code;
    private boolean     Success;
    //提示信息，通常供报错时使用
    private String      msg;
    private int totalcount;
    //正常返回时返回的数据
    private T           data;

    private static final int DEFAULT_CODE = 200;
    private static final boolean DEFAULT_SUCCESS = true;
    private static final int DEFAULT_TOTALCOUNT = 0;
    private static final String DEFAULT_STATUS_MSG = "成功";




    //返回成功数据
    public ResultJsonUtil success(T data) {
        return new ResultJsonUtil(ResultJsonUtil.DEFAULT_CODE,DEFAULT_SUCCESS, ResultJsonUtil.DEFAULT_STATUS_MSG,DEFAULT_TOTALCOUNT, data);
    }
    public ResultJsonUtil success(T data,int totalcount) {
        return new ResultJsonUtil(ResultJsonUtil.DEFAULT_CODE,DEFAULT_SUCCESS, ResultJsonUtil.DEFAULT_STATUS_MSG,totalcount, data);
    }

    public static ResultJsonUtil success(Integer code,String msg) {
        return new ResultJsonUtil(ResultJsonUtil.DEFAULT_CODE,DEFAULT_SUCCESS, ResultJsonUtil.DEFAULT_STATUS_MSG,DEFAULT_TOTALCOUNT, null);
    }

    //返回出错数据
    public static ResultJsonUtil error(Integer code,String msg) {
        return new ResultJsonUtil(code,false, msg,DEFAULT_TOTALCOUNT, null);
    }

}
