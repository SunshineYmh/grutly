package com.vastly.affairs.util;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MinioUtils {

    @Resource
    private MinioClient minioClient;
    @Value("${minio.bucketName}")
    public String bucketName;


    /**
     * Description 获取所有桶
     *
     * @return List<Bucket> 桶列表
     **/
    public List<Bucket> listBuckets() {

        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Description 判断桶是否存在
     *
     * @param bucket 桶名称
     **/
    public Boolean bucketExists(String bucket) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("存储桶'" + bucket + "'不存在！");
            return false;
        }
    }

    /**
     * Description 创建一个桶
     *
     * @param bucket 桶名称
     **/
    public Boolean createBucket(String bucket) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 设置桶名称
     *
     * @param bucket 桶名称
     */
    public void setBucketName(String bucket) {
        this.bucketName = bucket;
    }

    /**
     * Description 上传本地文件
     *
     * @param path   本地文件路径
     * @param object 文件名称
     **/
    public Boolean uploadObject(String path, String object) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object)
                            .filename(path)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Description 以流的方式上传文件
     *
     * @param in          文件流
     * @param object      文件名称
     * @param contentType 文件类型
     **/
    public Boolean uploadObject(InputStream in, String object, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object)
                            .stream(in, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean uploadObject(InputStream in, String object, long fileSize,String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object)
                            .stream(in, fileSize, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Description  多文件上传
     *
     * @param objects 文件列表
     **/
//    public Boolean uploadObjects(List<SnowballObject> objects) {
//        try {
//            minioClient.uploadSnowballObjects(
//                    UploadSnowballObjectsArgs.builder().bucket(bucketName).objects(objects).build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    /**
     * Description 文件列表
     *
     * @param limit 范围 1-1000
     * @return java.util.List<io.minio.messages.Item>
     **/
    public List<Item> listObjects(int limit) {
        List<Item> objects = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .maxKeys(limit)
                        .includeVersions(true)
                        .build());
        try {
            for (Result<Item> result : results) {
                objects.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * Description 生成预览链接，最大7天有效期；如果想永久有效，在 minio 控制台设置仓库访问规则总几率
     *
     * @param object      文件名称
     * @param contentType 预览类型 image/gif", "image/jpeg", "image/jpg", "image/png", "application/pdf
     * @return java.lang.String
     * @Params
     **/
    public String getPreviewUrl(String object, String contentType) {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-type", contentType != null ? contentType : "application/pdf");
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(object)
                            .expiry(7, TimeUnit.DAYS)
                            .extraQueryParams(reqParams)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Description 网络文件转储 minio
     *
     * @param httpUrl 文件地址
     * @return void
     * @Params
     **/
    public void netToMinio(String httpUrl) {
        int i = httpUrl.lastIndexOf(".");
        String substring = httpUrl.substring(i);
        URL url;
        try {
            url = new URL(httpUrl);
            URLConnection urlConnection = url.openConnection();
            // agent 模拟浏览器
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            // 临时文件转储
            File tempFile = File.createTempFile(UUID.randomUUID().toString().replace("-", ""), substring);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            // 上传minio
            uploadObject(tempFile.getAbsolutePath(), tempFile.getName());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description 文件转字节数组
     *
     * @param path 文件路径
     * @return byte[] 字节数组
     **/
    public byte[] fileToBytes(String path) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            fis = new FileInputStream(path);
            int temp;
            byte[] bt = new byte[1024 * 10];
            while ((temp = fis.read(bt)) != -1) {
                bos.write(bt, 0, temp);
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(fis)) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }

    /**
     * Description  下载文件到本地
     *
     * @param object 文件名称
     * @param output 输出路径
     **/
    public void download(String object, String output) {

        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(object)
                            .filename(output)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description 下载 minio文件，返回流的方式，同时支持在线预览
     *
     * @param object   文件名称
     * @param isOnline 是否在线预览，不同文件类型请修改 setContentType
     * @param response 响应对象
     **/
    public void download(String object, Boolean isOnline, HttpServletResponse response) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(object)
                        .build())) {
            try {
                BufferedInputStream br = new BufferedInputStream(stream);
                byte[] buf = new byte[1024];
                int len;
                response.reset(); // 非常重要
                if (Objects.nonNull(isOnline) && isOnline) { // 在线打开方式
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "inline; filename=" + object);
                } else { // 纯下载方式
                    response.setContentType("application/x-msdownload");
                    response.setHeader("Content-Disposition", "attachment; filename=" + object);
                }
                OutputStream out = response.getOutputStream();
                while ((len = br.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description 批量删除文件对象
     *
     * @param objects 文件名称集合
     * @return java.util.List<io.minio.messages.DeleteError> 删除失败的文件集合
     **/
    public List<DeleteError> deleteObject(List<String> objects) {
        List<DeleteError> deleteErrors = new ArrayList<>();
        List<DeleteObject> deleteObjects = objects.stream().map(value -> new DeleteObject(value)).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs
                                .builder()
                                .bucket(bucketName)
                                .objects(deleteObjects)
                                .build());
        try {
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                deleteErrors.add(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteErrors;
    }
}
