package com.vastly.affairs.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

/**http formData 解析
 * auth: WenYF
 * date: 2018/7/11
 */
@Slf4j
public class FormDataAnalysisUtil {


    public static void main(String[] args) throws IOException {
        byte[] data = new byte[]{45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 102, 105, 108, 101, 34, 59, 32, 102, 105, 108, 101, 110, 97, 109, 101, 61, 34, 49, 49, 49, 49, 49, 46, 106, 112, 103, 34, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101, 58, 32, 105, 109, 97, 103, 101, 47, 106, 112, 101, 103, 13, 10, 13, 10, -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -124, 0, 0, 0, 52, 8, 6, 0, 0, 0, 75, -114, -112, -46, 0, 0, 0, 1, 115, 82, 71, 66, 0, -82, -50, 28, -23, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79, -113, 11, -4, 97, 5, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 14, -61, 0, 0, 14, -61, 1, -57, 111, -88, 100, 0, 0, 4, -94, 73, 68, 65, 84, 120, 94, -19, -101, 61, 110, -36, 48, 16, -123, 117, -89, 0, 46, 116, 22, 55, 110, 116, 14, -73, -18, 116, -121, -12, 46, 117, 3, 31, -64, -11, -42, 57, 65, 14, -64, 112, -8, -89, -103, -31, 80, -92, -20, 77, -68, 89, -65, 15, 24, -64, -30, -49, -16, -111, 124, -44, 10, -112, 60, 57, 0, 24, 48, 4, 16, -64, 16, 64, 0, 67, 0, 1, 12, -15, 101, 92, -36, 58, 79, 110, -102, 38, -73, 108, -87, -24, 6, 96, -122, -56, 2, 103, -73, 94, 82, 81, 102, 91, -36, 52, -81, -66, -59, 40, -101, 91, -4, 68, 105, -78, -26, -124, 47, -85, -101, 89, 125, 61, 102, -44, 98, 45, -44, -74, -16, 126, 62, 78, -23, -70, 29, 46, -21, -20, -90, -85, 58, -31, 96, -1, 2, 105, 79, 58, -21, 85, 25, 98, 89, -116, -51, 63, 101, -120, 56, -16, 62, 87, -54, -69, -8, -46, 4, -27, -46, -94, -109, 65, 100, -97, -74, 33, 102, -42, 57, 24, -28, -86, 11, 107, -47, -42, -13, 81, -12, 60, 62, 79, -44, 56, -49, -77, -103, 55, 24, -16, 67, -122, -40, 82, 98, -98, -12, -116, 33, 104, 115, -101, 109, -75, 89, 24, -63, 40, -39, 56, -29, -122, -72, -2, 73, -77, -8, 127, 12, -79, -84, 116, -72, -40, 1, 12, 80, -99, 63, -124, 107, 127, 31, 13, 67, -48, -97, -22, -60, 86, -122, -112, 63, 9, -5, 70, 18, -99, 77, 63, 52, 75, -66, 115, -116, 26, -62, 106, 119, -92, -115, 56, -82, 47, 39, -87, -44, -87, -10, 69, -65, 44, -81, 53, -91, 122, 99, -66, -31, -82, 86, -6, -26, -15, -113, 116, -11, 115, -14, -75, -88, -52, -106, -41, 125, 96, 31, 109, 67, -48, 85, 88, -104, 36, 74, 36, -118, 73, -86, 1, -7, 4, -62, -75, -98, -108, -89, 18, -60, -31, -29, 75, 45, 28, -71, -104, 62, -116, 9, -74, -75, 117, -22, -61, 65, 80, -102, 3, 90, 15, -27, -31, 63, 123, -75, -10, 106, -18, 10, -71, 105, 61, -35, 35, 57, -103, 6, -93, 111, 41, -17, -19, 99, -6, -45, -61, 39, 69, 36, 17, 84, -64, 19, -119, -92, 25, -35, 55, 82, 78, 91, -82, 48, -5, 102, 40, -57, -39, 59, -124, -121, 114, -26, -55, -9, -76, 117, -75, -57, 69, -22, 62, -28, -122, 49, -87, -99, -116, -88, -85, -83, -99, 35, -26, -47, -43, 53, -110, -77, 110, 31, -14, -13, -36, -83, -65, 11, -105, 35, 67, 120, -14, 79, 7, 127, -48, 108, -118, -41, -117, -104, -119, -117, 28, -13, -14, -65, 21, -108, -41, 114, -75, -94, 50, 68, -56, -103, -58, -18, 105, 27, -42, 30, -57, -33, -115, -95, -12, -104, 121, 50, 109, -19, -100, 49, 67, 52, -58, 55, -79, 53, -82, -83, 113, 26, 99, 30, 27, -62, 83, 63, -99, 54, 110, 53, -71, -98, 76, 36, -110, -80, 13, -13, -60, 124, 106, 3, -12, 51, 75, 67, 11, 113, 120, -121, -24, 105, 27, -48, -66, -102, 26, -76, -98, -104, -121, -21, -37, -4, -95, -119, -105, 109, -19, 28, 57, -113, -98, -18, -111, -100, -74, 70, -15, 51, 51, -78, 22, -23, 79, 79, 107, -48, 88, -66, 39, 34, -14, 96, 57, -40, -96, 30, -7, 96, 102, -28, 76, 6, -32, 109, 100, -2, 52, -90, 81, 79, 11, 41, -54, -43, -40, 61, 109, -67, 122, -111, -97, 9, -81, 14, -122, -102, -61, -34, 116, 100, -13, -30, 56, 98, 51, 14, 117, -115, -28, -84, -37, 4, -51, -68, 64, 24, -126, -88, -57, 100, -122, -8, 90, -54, 70, 8, -63, -32, 95, 115, 51, -122, 0, -73, 1, 12, 1, 4, 48, 4, 16, -64, 16, 64, 0, 67, 0, 1, 12, 1, 4, 48, 4, 16, -64, 16, 64, 0, 67, 0, 1, 12, 1, 4, -41, 53, -124, 122, 81, 52, 87, -81, -111, -65, -126, -3, -67, 72, -17, -3, 2, -8, 6, -122, -88, 94, -16, -128, 67, -2, -98, 33, 110, -124, -6, -83, 98, 102, -20, -83, -28, 120, -69, -5, 0, -122, -128, 33, 4, -52, 16, 121, -30, -23, 29, 121, -39, 88, -7, -50, -4, -8, 29, -66, 15, -15, -109, -79, -65, -45, -105, -33, 72, -24, 111, 20, 50, -83, -79, -22, -9, -10, 123, 127, -86, -13, 63, 77, -12, 69, -79, -22, 39, -66, 109, -88, -6, -80, 58, -81, 121, 11, -6, -10, 54, -95, -17, -78, 84, -19, -8, -20, -17, -111, -54, 16, -26, 98, -105, 85, -32, -89, 37, 46, 42, 63, 57, 97, 17, 45, 67, 40, 115, -40, -60, 124, -11, 105, 54, -54, -23, 78, 84, -14, -91, -51, -51, 66, -44, -77, -53, -103, 59, 68, 105, 43, -18, 116, -33, -2, 14, -111, 46, -119, -80, -16, -23, 116, -80, -88, 23, 45, -63, -53, -124, 9, -46, -90, 29, 61, 100, 90, -7, 8, -77, 92, 27, 83, -101, -10, 99, -122, -80, 117, -62, 16, 59, -83, 77, 34, 78, 25, 34, 19, -57, 48, -115, 113, -38, 16, 57, 7, 12, 113, 77, -114, 13, -111, 22, -120, -105, -19, 31, -109, -22, -70, -40, -33, 52, -124, -1, -37, -4, 120, 85, -36, -34, 117, -66, -51, 63, 23, 80, 69, 44, 23, -101, 42, 76, 66, -11, -125, -122, 16, -29, -43, -13, -51, 109, -61, -13, 78, -55, 111, -83, -53, -3, -46, 49, -124, 39, 44, 34, -99, -102, 24, -94, -98, 54, -90, -44, -7, -123, -26, -1, 42, -58, 13, -31, -95, -59, 46, 109, 115, 18, -79, 65, -7, 58, -73, -29, 119, -105, 104, -118, 125, 44, 93, -9, 17, 67, -48, 101, 122, -48, -11, -102, -23, 115, 117, 109, -126, -84, -109, -73, 43, -61, -36, 41, -52, 16, 0, -64, 16, 64, 1, 67, 0, 1, 12, 1, 4, 48, 4, 16, -64, 16, 64, 48, -3, -6, -19, 28, 2, -111, 3, -122, 64, -120, -128, 33, 16, 34, 96, 8, -124, 8, 24, 2, 33, -30, 14, 13, -79, -71, -57, 105, 118, -49, -17, 86, 29, -94, 23, 48, 4, 66, -60, -128, 33, 46, -18, -7, 97, 114, -113, -81, 86, -35, 72, 124, -74, -1, -39, -128, 33, 62, 19, 48, 4, 66, -124, 48, -60, -37, -117, -4, 16, -10, 103, 88, -36, 124, -19, -29, 97, 117, 111, -44, -10, 125, 117, 63, 88, -7, -66, -39, 121, -13, 83, -65, -121, 89, -76, 11, -3, 67, 95, -54, -99, -6, -120, 107, -43, 63, 69, -53, 76, -75, 94, 42, 79, -122, 120, 97, -33, 106, 60, 109, 85, 95, -124, 29, -69, 33, -12, 70, -107, -48, 39, -36, 95, 63, 37, 99, 80, -68, -46, -62, -53, 13, -35, -81, -9, -78, -46, 127, -64, 16, -94, -65, -56, -49, -94, -87, 55, -103, 41, -101, 32, -76, -61, 29, 99, 44, -100, -5, 3, 127, 65, -69, 40, -121, -5, -86, 42, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 106, 103, 98, 109, 34, 13, 10, 13, 10, 83, 104, 105, 110, 101, 121, 117, 101, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 121, 119, 108, 115, 104, 34, 13, 10, 13, 10, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 49, 50, 54, 54, 54, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 102, 115, 114, 73, 100, 34, 13, 10, 13, 10, 50, 49, 50, 49, 51, 50, 52, 50, 52, 52, 49, 50, 52, 49, 50, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 102, 115, 114, 78, 97, 109, 101, 34, 13, 10, 13, 10, -27, -113, -111, -23, -128, -127, -28, -70, -70, -27, -89, -109, -27, -112, -115, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 106, 115, 114, 73, 100, 34, 13, 10, 13, 10, 49, 49, 49, 49, 49, 49, 49, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 106, 115, 114, 78, 97, 109, 101, 34, 13, 10, 13, 10, 50, 49, 49, 51, 49, 50, 49, 50, 49, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 100, 97, 116, 97, 106, 115, 111, 110, 34, 13, 10, 13, 10, 123, 34, 106, 103, 98, 109, 34, 58, 34, -25, -91, -98, -25, -114, -91, -28, -70, -110, -24, -127, -108, -25, -67, -111, 34, 44, 34, 100, 101, 112, 97, 114, 116, 109, 101, 110, 116, 34, 58, 34, -27, -116, -70, -27, -99, -105, -23, -109, -66, -23, -95, -71, -25, -101, -82, -26, -100, -115, -27, -118, -95, -23, -125, -88, 34, 44, 34, 102, 115, 114, 73, 100, 34, 58, 34, 54, 49, 48, 49, 48, 49, 46, 46, 46, 46, 46, 46, 46, 49, 49, 49, 49, 34, 44, 34, 102, 115, 114, 78, 97, 109, 101, 34, 58, 34, -27, -113, -111, -23, -128, -127, -28, -70, -70, -27, -89, -109, -27, -112, -115, 34, 44, 34, 106, 115, 122, 73, 100, 34, 58, 34, 49, 48, 48, 48, 48, 48, 48, 48, 48, 49, 34, 44, 34, 106, 115, 122, 78, 97, 109, 101, 34, 58, 34, -28, -70, -110, -24, -127, -108, -25, -67, -111, -24, -111, -93, -28, -70, -117, -27, -112, -119, -27, -99, -92, -27, -68, -128, -23, -103, -92, 34, 44, 34, 102, 115, 115, 106, 34, 58, 34, 50, 48, 50, 51, 45, 48, 50, 45, 48, 51, 32, 49, 50, 58, 50, 49, 59, 50, 49, 34, 125, 13, 10, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 56, 54, 54, 49, 48, 51, 51, 56, 57, 56, 56, 54, 55, 53, 57, 50, 49, 49, 56, 56, 55, 49, 54, 56, 45, 45, 13, 10};
        //System.out.println(new String(data));
        //List<byte[]> datas = getRawData(data);
//        datas.forEach(dit -> {
//            System.out.println("--》》》"+new String(dit));
//        });
        String wrapperName = "--------------------------866103389886759211887168";
        //List<List<byte[]>> datass =  getRawDataTask(data, wrapperName);
//        JSONObject json = getMultipartFormData(data, wrapperName);
//        log.info(json.toString());
//        List<Map<String,Object>> map = (List<Map<String, Object>>) json.get("file");
//        Map<String,Object> maps = map.get(0);
//        byte[] fileData = (byte[]) maps.get("fileData");
//        System.out.println(fileData);
//        multipartFormData dd = new multipartFormData();
//        dd.setFileData(fileData);
//        log.info(dd.toString());
//        System.out.println("--->>>>>>>>>>>>>>>>");
//        File file =  new File("C:\\Users\\ymh\\Desktop\\11111.jpg");
//        byte[] fileData2 =  FileUtils.readFileToByteArray(file);
//        //byte[] fileData = "\r\n".getBytes();
//        multipartFormData dd2 = new multipartFormData();
//        dd2.setFileData(fileData2);
//        log.info(dd2.toString());
        String filename = "11111.jpg";
        String[] filenameType = filename.split("\\.",-1);
        System.out.println(filenameType[0]);
        System.out.println(filenameType[1]);

    }

    public static JSONObject getMultipartFormData(byte[] fileData,String wrapperName){
        JSONObject jsonObj = new JSONObject();
        //Map<String,Object> jsonObj = new HashMap<>();
        List<List<byte[]>> datass =  getRawDataTask(fileData, wrapperName);
        //List<Map<String,Object>> fileArr = new ArrayList<>();
        JSONArray fileArr = new JSONArray();
        for(int i=0;i<datass.size();i++){
            JSONObject jsondata = new JSONObject();
            List<byte[]> datas = datass.get(i);
            if(datas.size()== 0){
                continue;
            }
            String ContentDisposition = new String(datas.get(0),Charset.defaultCharset());
            String[] ContentDispositions = ContentDisposition.split(";",-1);
            // 解析文件
            if(ContentDispositions.length>=3 && ContentDisposition.contains("filename=")){
                int index = 0;
                //解析文件字段名
                String name = ContentDispositions[1];
                String[] names = name.split("=",-1);
                if(StringUtils.isNotEmpty(names[1])){
                    jsondata.put("name",names[1].replace("\"",""));
                }else{
                    jsondata.put("name","");
                }
                //解析文件名
                String filenameDis = ContentDispositions[2];
                String[] filenames = filenameDis.split("=",-1);
                String filename = "";
                if(StringUtils.isNotEmpty(filenames[1])){
                    filename = filenames[1].replace("\"","")
                            .replace("\r","")
                            .replace("\n","");
                }
                jsondata.put("filename",filename);
                //解析文件格式
                String type = "";
                if(StringUtils.isNotEmpty(filename)){
                    String[] filenameType = filename.split("\\.",-1);
                    type = filenameType[1];
                }
                jsondata.put("type",type);
                //解析Content-Type
                String ContentType = "text/plain";
                String ContentTypeStr = new String(datas.get(1),Charset.defaultCharset());
                if(ContentTypeStr.startsWith("Content-Type:")){
                    String[] ContentTypes = ContentTypeStr.split(":",-1);
                    ContentType = ContentTypes[1].trim()
                            .replace("\r","")
                            .replace("\n","");
                    //index += 1;
                }
                jsondata.put("ContentType",ContentType);
                //解析数据
                index = 3;
                int coundata = 0;
                List<byte[]> Filedatas = new ArrayList<>();
                for(int n=index;n<datas.size();n++){
                    Filedatas.add(datas.get(n));
                    coundata += datas.get(n).length;
                }
                jsondata.put("fileSize",coundata);
                if(Filedatas.size()>0){
                    byte[] fileDatas = new byte[coundata];
                    int startIndex = 0;
                    for(int j=0;j<Filedatas.size();j++){
                        byte[] fileD = Filedatas.get(j);
                        System.arraycopy(fileD,0,fileDatas,startIndex,fileD.length);
                        startIndex += (fileD.length);
                    }
                    String base64encodedString = Base64.getEncoder().encodeToString(fileDatas);
                    jsondata.put("fileData",fileDatas);
                }else{
                    jsondata.put("fileData",null);
                }
                fileArr.add(jsondata);
            }else{
                //解析文件字段名
                String filde = "";
                String name = ContentDispositions[1];
                String[] names = name.split("=",-1);
                if(StringUtils.isNotEmpty(names[1])){
                    filde = names[1].replace("\"","")
                            .replace("\r","")
                            .replace("\n","").trim();
                }
                //解析数据
                int index = 2 ;
                int coundata = 0;
                List<byte[]> Filedatas = new ArrayList<>();
                for(int n=index;n<datas.size();n++){
                    Filedatas.add(datas.get(n));
                    coundata += datas.get(n).length;
                }
                //jsondata.put("dataSize",coundata);
                if(Filedatas.size()>0){
                    byte[] fileDatas = new byte[coundata];
                    int startIndex = 0;
                    for(int j=0;j<Filedatas.size();j++){
                        byte[] fileD = Filedatas.get(j);
                        System.arraycopy(fileD,0,fileDatas,startIndex,fileD.length);
                        startIndex += fileD.length;
                    }
                    String datastr =  new String(fileDatas,Charset.defaultCharset());
                    if(StringUtils.isNotEmpty(datastr)){
                        datastr = datastr.trim().replace("\r","")
                                .replace("\n","");
                    }
                    jsonObj.put(filde,datastr);
                }else{
                    jsonObj.put(filde,null);
                }
            }
        }
        jsonObj.put("file",fileArr);
        return jsonObj;
    };





    public static List<List<byte[]>> getRawDataTask(byte[] fileData,String wrapperName){
        List<List<byte[]>> datas0 = new ArrayList<>();
        List<byte[]> datas = getRawData(fileData);
        System.out.println("-->00>"+datas.size());
        if(datas != null && datas.size()>0){
            List<byte[]> datas2 = new ArrayList<>();
            for(byte[] bit : datas){
                String bitstr =  new String(bit,Charset.defaultCharset());
                if(bitstr.contains(wrapperName)){
                    datas0.add(datas2);
                    datas2  = new ArrayList<>();
                }else{
                    datas2.add(bit);
                }
            }
        }
        return datas0;
    }
    public static byte[] repSprts(byte[] data){
        if(data.length>=2){
            if(data[data.length-2] == '\r' && data[data.length-1] == '\n'){
                byte[] datas = new byte[data.length-2];
                System.arraycopy(data,0,datas,0,data.length-2);
                return datas;
            }else{
                if(data[data.length-1] == '\r' || data[data.length-1] == '\n'){
                    byte[] datas = new byte[data.length-1];
                    System.arraycopy(data,0,datas,0,data.length-1);
                    return datas;
                }
            }
        }else if(data.length>=1){
            if(data[data.length-1] == '\r' || data[data.length-1] == '\n'){
                byte[] datas = new byte[data.length-1];
                System.arraycopy(data,0,datas,0,data.length-1);
                return datas;
            }
        }
        return  data;
    }

    public static List<byte[]> getRawData(byte[] fileData){
        List<byte[]> datas = new ArrayList<>();
        int endIndex = 0;
        boolean isEnd = true;
        while (isEnd){
            if(endIndex>(fileData.length-1)){
                break;
            }
            byte[] bitdata =  readByteLine(fileData,endIndex);
            if(bitdata != null ){
                endIndex +=  bitdata.length;
                datas.add(bitdata);
            }
        }
        return datas;
    }


    private static byte[] readByteLine(byte[] fileData,int  startIndex) {
        int count = 0;
        for(int i=0;i<fileData.length;i++){
            if(i < startIndex){
                continue;
            }
            if((i+1)<fileData.length){
                if(isLineByteEnd(fileData[i])){
                    if(isLineByteEnd(fileData[i+1])){
                        count += 2;
                        break;
                    }else{
                        count += 1;
                        break;
                    }
                }
            }else{
                if(isLineByteEnd(fileData[i])){
                    count += 1;
                    break;
                }
            }
            count += 1;
        }
        if(count>0){
            byte[] databits = new byte[count];
            int jqindex = 0;
            if(startIndex > 0 ){
                jqindex = startIndex-1;
            }
            System.arraycopy(fileData,startIndex,databits,0,count);
            return databits;
        }else{
            return null;
        }
    }


    private static boolean isLineByteEnd(byte data) {
        if(data == '\r' || data == '\n'){
            return true;
        }else{
            return false;
        }
    }

}