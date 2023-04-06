package com.vastly.ymh.hlht.config.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataResult<T> {

    private boolean success;
    private String msg;//
    private long totalcount;
    private long count;//必输
    private T data;
    private long code;



    private DataResult(boolean success,long code,String msg){
        this.success = success;
        this.code = code;
        this.msg = msg;
    }
    private DataResult(boolean success,long code,T data,String msg){
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private DataResult(boolean success,long code,T data,String msg,long totalcount){
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.totalcount = totalcount;
        this.count = totalcount;
    }

    private DataResult(boolean success,long code,String msg,T data){
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }



    public  static <T> DataResult<T> success(){
        return new DataResult<T>(true,200,"成功");
    }
    public static <T> DataResult<T> success(T data){
        return new DataResult<T>(true,200,data,"成功");
    }
    public static <T> DataResult<T> success(T data,String msg){
        return new DataResult<T>(true,200,data,msg);
    }
    public static <T> DataResult<T> success(T data,long totalcount){
        return new DataResult<T>(true,200,data,"成功",totalcount);
    }
    public static <T> DataResult<T> success(T data,String msg,long totalcount){
        return new DataResult<T>(true,200,data,msg,totalcount);
    }
    public static <T> DataResult<T> success(long code,T data,String msg,long totalcount){
        return new DataResult<T>(true,code,data,msg,totalcount);
    }


    public  static <T> DataResult<T> error(){
        return new DataResult<T>(false,500,"失败");
    }
    public static <T> DataResult<T> error(long code ,String msg){
        return new DataResult<T>(false,code,null,msg);
    }
    public static <T> DataResult<T> error(T data){
        return new DataResult<T>(false,500,data,"失败");
    }
    public static <T> DataResult<T> error(T data,String msg){
        return new DataResult<T>(false,500,data,msg);
    }
    public static <T> DataResult<T> error(T data,long totalcount){
        return new DataResult<T>(false,500,data,"失败",totalcount);
    }
    public static <T> DataResult<T> error(T data,String msg,long totalcount){
        return new DataResult<T>(false,500,data,msg,totalcount);
    }
    public static <T> DataResult<T> error(long code,T data,String msg,long totalcount){
        return new DataResult<T>(false,code,data,msg,totalcount);
    }

}
