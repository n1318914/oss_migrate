/*
 * 文件名：FileUtil.java
 * 版权：Copyright by www.ixuenong.com
 * 描述：
 */

package com.migrate;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;


public class FileUtil
{

    private String endpoint;

    private String accessKeyid;

    private String accessKeySecret;

    private String bucketName;

    private OSSClient ossClient;

    public FileUtil(String endpoint, String accessKeyid, String accessKeySecret, String bucketName)
    {
        this.endpoint = endpoint;
        this.accessKeyid = accessKeyid;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;

        this.ossClient = new OSSClient(endpoint, accessKeyid, accessKeySecret);
    }

    /**
     * 上传文件
     */
    public boolean setFile(String key, File file)
    {
        try
        {
            //PutObjectResult putObject = ossClient.putObject(ThirdConstants.getOSS_bucketName(), key, file);

            // 设置断点续传请求
            UploadFileRequest uploadFileRequest = new UploadFileRequest(this.bucketName, key);
            // 指定上传的本地文件
            uploadFileRequest.setUploadFile(file.getPath());
            // 指定上传并发线程数
            uploadFileRequest.setTaskNum(5);
            // 指定上传的分片大小
            uploadFileRequest.setPartSize(1 * 1024 * 1024);
            // 开启断点续传
            uploadFileRequest.setEnableCheckpoint(true);

            //设置音频在线播放
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(fmtContentType(file.getName()));

            uploadFileRequest.setObjectMetadata(objectMetadata);
            // 断点续传上传
            UploadFileResult uploadFile = null;
            try
            {
                uploadFile = ossClient.uploadFile(uploadFileRequest);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return false;
            }
            if (null == uploadFile)
            {
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取出文件
     */
    public boolean getFile(String key, File file)
    {
        try
        {
            GetObjectRequest request = new GetObjectRequest(this.bucketName, key);
            ObjectMetadata object = ossClient.getObject(request, file);
            if (null != object)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    
    /**
     * 所有文件
     */
    public List<String> getListFiles() {
        List<String> result = new ArrayList<String>();
        try {
            ObjectListing listObjects = null;
            List<OSSObjectSummary> objectSummaries = null;
            String nextMarker = null;
            do {
                listObjects = ossClient.listObjects(new ListObjectsRequest(bucketName).withMarker(nextMarker));
                objectSummaries = listObjects.getObjectSummaries();
                for (OSSObjectSummary object : objectSummaries) {
                    result.add(object.getKey());
                }
                nextMarker = listObjects.getNextMarker();
            }
            while(listObjects.isTruncated());
                
            return result;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 
     * 关闭链接
     */
    public void close()
    {
        ossClient.shutdown();
    }

    /**
     * 
     * 根据文件名后缀返回上传至云服务器的Content-Type
     * 〈功能详细描述〉
     * @param filename
     * @return
     */
    private String fmtContentType(String filename)
    {
        if (filename.endsWith("mp3"))
        {
            return "audio/mp3";
        }
        if (filename.endsWith("m4a"))
        {
            return "audio/x-m4a";
        }
        return "audio/mepg";
    }
    
    public static void main(String[] args)
    {
        PropKit.use("config.properties");
        FileUtil srcUtil = new FileUtil(PropKit.get("src_endpoint"), PropKit.get("src_accessKeyId"), PropKit.get("src_accessKeySecret"),PropKit.get("src_bucketName"));
        File file = new File(PathKit.getRootClassPath() + "/6071aa3cb2b5487e9db269c54b8b87f3.mp3");
        
        System.out.println(PathKit.getRootClassPath() + "/temp");
        
        srcUtil.getFile("test/201709/6071aa3cb2b5487e9db269c54b8b87f3.mp3", file);
    }
}
