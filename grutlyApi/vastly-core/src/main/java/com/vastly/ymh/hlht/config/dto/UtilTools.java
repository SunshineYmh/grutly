package com.vastly.ymh.hlht.config.dto;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * @ClassName UtilTools
 * @Description: TODO
 * @Author yangminghao
 * @Date 2022/2/16 
 **/
public class UtilTools {

    public static String getUuid(){
        return UUID.randomUUID().toString().toLowerCase().replace("-","");
    }


    /**
     * 判断是否为有效url
     *
     * @param urlStr
     * @return boolean
     * @author Torres Liu
     * @description //TODO 判断是否为有效url
     * @date 2020/4/29 5:08 下午
     **/
    public static  boolean ifUrlValidity(String urlStr) {
        URL url;
        HttpURLConnection con;
        int state = -1;
        try {
            url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            state = con.getResponseCode();
            if (state != 200) {
                return false;
            }
        } catch (Exception e1) {
            return false;
        }
        return true;
    }
}
