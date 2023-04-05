package com.vastly.affairs.util;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MultipartFileFile {
	
	/**
	 * MultipartFile转file
	 * */
	public static File transferToFile(MultipartFile multipartFile) {
      File file = null;
      try {
          String originalFilename = multipartFile.getOriginalFilename();
          String[] filename = originalFilename.split("\\.");
          if(filename[0].length() < 3) {
        	  filename[0] += "---";
          }
          file=File.createTempFile(filename[0], filename[1]);
          multipartFile.transferTo(file);
          file.deleteOnExit();
      } catch (IOException e) {
          e.printStackTrace();
      }
      return file;
  }


    public static File transferToFilePart(Map<String, Part> stringPartMap) {
        Part file = stringPartMap.get("file");
        File file1 = null;
        if(file instanceof FilePart){

            try {
                FilePart filePart = (FilePart)file;
                String[] filename = filePart.filename().split("\\.");
                if(filename[0].length() < 3) {
                    filename[0] += "---";
                }
                file1 = File.createTempFile(filename[0], filename[1]);
                filePart.transferTo(file1);
                file1.deleteOnExit();
            } catch (Exception e) {
                throw new RuntimeException("参数分析错误!");
            }finally {
                if(file1.exists()){
                    file1.deleteOnExit();
                    file1.delete();
                }
            }
        }
        return file1;
    }

}
