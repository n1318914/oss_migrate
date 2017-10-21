/*
 * 文件名：Main.java
 * 版权：Copyright by www.ixuenong.com
 * 描述：
 */

package com.migrate;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.kit.PropKit;

public class Main
{
    
    
    public static void main(String[] args) throws SQLException
    {
        PropKit.use("config.properties");
       /* Connection conn = DriverManager.getConnection(PropKit.get("url"), PropKit.get("user"), PropKit.get("password"));
        Statement statement = conn.createStatement();
        
        String sql = "select url from t_audio_info";
        ResultSet rs = statement.executeQuery(sql);
        
        List<String> urls = new ArrayList<String>();
        while(rs.next())
        {
            urls.add(rs.getString("url"));
        }*/
        FileUtil srcUtil = new FileUtil(PropKit.get("src_endpoint"), PropKit.get("src_accessKeyId"), PropKit.get("src_accessKeySecret"),PropKit.get("src_bucketName"));
        FileUtil desUtil = new FileUtil(PropKit.get("des_endpoint"), PropKit.get("des_accessKeyId"), PropKit.get("des_accessKeySecret"),PropKit.get("des_bucketName"));
        
        List<String> listFiles = desUtil.getListFiles();
        List<String> list = srcUtil.getListFiles();
        System.out.println("总文件数： " + list.size());
        
        //线程数
        Integer threads = PropKit.getInt("threads");
        
        int num = (int)Math.ceil(list.size() / threads);
        
        for(int i = 0;i < threads; i++)
        {
            List<String> urls = list.subList(i * num, (i+1) * num);
            //new Worker(urls, srcUtil, desUtil).start();
        }
    }
}
