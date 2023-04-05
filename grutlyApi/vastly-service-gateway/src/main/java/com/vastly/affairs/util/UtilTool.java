package com.vastly.affairs.util;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;


public class UtilTool {
	
	
	  
	/**
	* 判断字符串是否为NULL或空	 * 
	 * @param s
	 *            待判断的字符串数据
	 * @return 判断结果 true-是 false-否
	 */
	public static boolean isEmpty(String s) {
		return null == s || "".equals(s.trim())|| "null".equals(s.trim())|| 
				"{}".equals(s.trim())|| "[]".equals(s.trim());
	}


	/**
     * 对象属性拷贝 <br>
     * 将源对象的属性拷贝到目标对象
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        try {
            BeanUtils.copyProperties(source, target);
        } catch (BeansException e) {
            System.out.println("BeanUtil property copy  failed :BeansException:"+ e);
        } catch (Exception e) {
        	 System.out.println("BeanUtil property copy failed:Exception :"+ e);
        }
    }

	/**
	 * @Title: combineSydwCore
	 * @Description: 该方法是用于相同对象不同属性值的合并，如果两个相同对象中同一属性都有值，
	 *               那么sourceBean中的值会覆盖tagetBean重点的值
	 * @author: WangLongFei
	 * @date: 2017年12月26日 下午1:53:19
	 * @param sourceBean
	 *            被提取的对象bean
	 * @param targetBean
	 *            用于合并的对象bean
	 * @return targetBean 合并后的对象
	 * @return: Object
	 */
	@SuppressWarnings("unused")
	public static Object copyObjAllProperties(Object sourceBean, Object targetBean) {
		Class sourceBeanClass = sourceBean.getClass();
		Class targetBeanClass = targetBean.getClass();

		Field[] sourceFields = sourceBeanClass.getDeclaredFields();
		Field[] targetFields = sourceBeanClass.getDeclaredFields();
		for (int i = 0; i < sourceFields.length; i++) {
			Field sourceField = sourceFields[i];
			Field targetField = targetFields[i];
			sourceField.setAccessible(true);
			targetField.setAccessible(true);
			try {
				if (!(sourceField.get(sourceBean) == null)) {
					targetField.set(targetBean, sourceField.get(sourceBean));
				}
				if (!(targetField.get(sourceBean) == null)) {
					sourceField.set(targetBean, targetField.get(sourceBean));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return targetBean;
	}
	
	/**
	 * *银证通密钥分量异或运算或者新密钥
	 * @param hex1 分量密钥1
	 * @return
	 */
	public static String hexXOR(String hex1, String hex2){
	    BigInteger i1 = new BigInteger(hex1, 16);
	    BigInteger i2 = new BigInteger(hex2, 16);
	    BigInteger res = i1.xor(i2);
	    return res.toString(16).toUpperCase();
	}
	
	
	public static String strToJsons(Object json) {
		try {
			if(json == null) {
				return "";
			}
			if(StringUtils.isEmpty(json.toString())) {
				return "";
			}
			return JSONObject.toJSONString(json).toString();
		} catch (Exception e) {
			return "";
		}
	}
	/**
	 * String 转 double
	 * NumberFormatException  return 0
	 * @param str
	 * @return
	 */
	public static double doublestr(String str){
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	/**
	 * HHmmss
	 * @return
	 */
	public static String formatDateTime2(){
		 SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		  return sdf.format(new Date());
	}
	
	
	/**
	 * yyyymmmdd
	 * @return
	 */
	public static String formatDateTime11(){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		  return sdf.format(new Date());
	}
	
	/**
	 * yyyyMMddHHmmssSSS
	 * @return
	 */
	public static String formatDateTimeYmdhmsS(){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		  return sdf.format(new Date());
	}
	
	
	
	
	/**
	 * yyyyMMddHHmmssSSS
	 * @return
	 */
	public static String formatDateTimeYmdhmss(){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		  return sdf.format(new Date());
	}
	
	
	public static long formatDateTimeYmdhmss_timestamp(){
		long ss = dateToStamp(formatDateTimeYmdhmss(),"yyyyMMddHHmmss");
		  return ss;
	}
	
	public static long formatDateTimeYmdhmss_timestamp2(){
		  return System.currentTimeMillis();
	}
	
	
	/* 
     * 将时间转换为时间戳
     */    
    public static long dateToStamp(String s,String type) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        Date date;
		try {
			date = simpleDateFormat.parse(s);
			long ts = date.getTime();
	        //res = String.valueOf(ts);
	        return ts;
		} catch (ParseException e) {
			return 0;
		}
    }
	
	/**
	 * 判断字符串是否为NULL或不是数字
	 * 
	 * @param s
	 *            待判断的字符串数据
	 * @return 判断结果 true-是 false-否
	 * 数字 true
	 */
	public static boolean isNum(String str){	
		boolean b = false;
		if(str != null ){
			if(!"".equals(str.trim())){
				if(str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
					 b= true;
				}else{
					b= false;
				}
			
			}else{
				b= false;
			}
			 	
		}else{
			
		}
		return b;

    }
	
   /*public static Number isNums(String str){
	   Number num = 0;
	   if(isNum(str)){
		   num = str;
	   }
   }*/
	
	/**
	 * 
	 * @param date 时间字符串
	 * @return yyyy-MM-dd格式
	 * @throws ParseException 
	 */
	public static String dateFormatYMD(String date,String type) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat(type);
			Date d=sdf.parse(date);
			return sdf.format(d);
		} catch (ParseException e) {
			return date;
		}
	}
	
	
    
    
    public static String reqBodySeand(String url,String RequestData){
    	if(isEmpty(RequestData)){
    		return RequestData;
    	}
    	if(url.indexOf("housecontract")!=-1 || url.indexOf("houseExistes")!=-1 || url.indexOf("accumulationFunt")!=-1){
    		if(url.indexOf("housecontract")!=-1 ){
    			JSONObject json = JSONObject.parseObject(RequestData);
        		JSONArray arr = json.getJSONArray("Data");
        		JSONObject json2 =  arr.getJSONObject(0);
        		String ContractId = json2.getString("ContractId");
        		RequestData = RequestData.replace(ContractId, URLEncoder.encode(ContractId));
    		}
    		RequestData = "json="+RequestData;
    	}
    	
    	return RequestData;
    }
	
	
    private static String getDomains(String url){
    	String urll = url.replace("//", "/");
    	String[] urls = urll.split("/");
    	StringBuffer sb = new  StringBuffer();
    	for(int i=4;i<urls.length;i++){
			 System.out.println(i+"<-222-->"+urls[i]);
			 sb.append(urls[i]);
			 if(i != urls.length-1){
				 sb.append("/");
			 }
		 }
        return sb.toString();
    }
    
    
  //随机数工具方法
    public static long getNumber(int length) {
        StringBuilder buf = new StringBuilder();
        Random random = new Random();
        /*开头不为0,建议数据量较少时只放开部分，比如1至3开头的数，等业务达到一定数量时，再逐步放开剩余的号码段，由于是固定位数，总数量一定，生成的数越多，重复的几率越大**/
        int firstNumber = random.nextInt(9) + 1; 
        buf.append(firstNumber);
 
        for(int i = 0; i < length - 1; ++i) {
            buf.append(random.nextInt(10));
        }
 
        return Long.valueOf(buf.toString());
    }
    
   
	

}
