package com.vastly.affairs.util;

import java.io.*;
import java.nio.charset.Charset;

public class FileUtils {

    /**
     *  文件写入数据
     * @param filePath
     * @param data
     * @throws IOException
     */
    public static void writeByteArrayToFile(String filePath, byte[] data) throws IOException {
        File file = new File(filePath);
        //判断是否存在文件夹，不存在则新建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //判断是否存在文件，不存在则新建
        if(!file.exists()){
            file.createNewFile();
        }
        org.apache.commons.io.FileUtils.writeByteArrayToFile(file,data);
    }

    public static boolean fileLinesWrite(String filePath, String content, boolean flag, Charset charset){
        boolean filedo = false;
        BufferedWriter out = null;
        try {
            File file=new File(filePath);
            //如果文件夹不存在，则创建文件夹
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if(!file.exists()){//如果文件不存在，则创建文件,写入第一行内容
                file.createNewFile();
            }
            //new OutputStreamWriter(new FileOutputStream(file,true), "UTF-8");
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, flag),charset),1024);
            out.write(content);
            filedo  = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filedo;
    }

    /**
     * 向文件写入byte[]
     *
     * @param filePath 文件
     * @param bytes    字节内容
     * @param append   是否追加
     * @throws IOException
     */
    public static boolean writeFileByBytes(String filePath, byte[] bytes, boolean append) {
        boolean b =false;
        try{
            File file=new File(filePath);
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if(!file.exists()){//如果文件不存在，则创建文件,写入第一行内容
                file.createNewFile();
            }
        }catch (IOException e){
            b = false ;
            e.printStackTrace();
        }
        try(
            OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath, append))){
            out.write(bytes);
            b = true;
            if(out != null){
                out.close();
            }
        }catch (IOException e){
            b = false ;
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 从文件开头向文件写入byte[]
     *
     * @param filePath 文件名
     * @param bytes    字节
     * @throws IOException
     */
    public static boolean writeFileByBytes(String filePath, byte[] bytes)  {
        boolean b =  writeFileByBytes(filePath, bytes, false);
        return b;
    }

}
