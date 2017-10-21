/*
 * 文件名：Worker.java
 * 版权：Copyright by www.ixuenong.com
 * 描述：
 */

package com.migrate;

import java.io.File;
import java.util.List;

import com.jfinal.kit.PathKit;

/**
 * 
 * 迁移任务
 * 
 * @author yulong.chen@dfs168.com
 * @since 2017年10月21日
 */
public class Worker extends Thread
{
    private List<String> urls;
    private FileUtil srcUtil;
    private FileUtil desUtil;
    
    
    public Worker(List<String> urls, FileUtil srcUtil, FileUtil desUtil) {
        this.urls = urls;
        this.srcUtil = srcUtil;
        this.desUtil = desUtil;
    }
    
    public void run()
    {
        //得到url即文件目录
        //FileUtil srcUtil = new FileUtil(PropKit.get("src_endpoint"), PropKit.get("src_accessKeyId"), PropKit.get("src_accessKeySecret"),PropKit.get("src_bucketName"));
        //FileUtil desUtil = new FileUtil(PropKit.get("des_endpoint"), PropKit.get("sdes_accessKeyId"), PropKit.get("des_accessKeySecret"),PropKit.get("des_bucketName"));
        
        File file = null;
        while(urls.size() > 0) 
        {
            for(String url : urls)
            {
                file = new File(PathKit.getWebRootPath() + url.substring(url.lastIndexOf("/"), url.length()));
                if(srcUtil.getFile(url, file) && desUtil.setFile(url, file)) 
                {
                    System.out.println(url + "转换成功!!!移除队列");
                    urls.remove(url);
                    file.delete();
                }
                else 
                {
                    System.out.println(url + "转换失败~~~等待再次转换");
                }
            }
        }
        
    }
}
