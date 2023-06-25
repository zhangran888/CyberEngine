package com.datacyber.assembly;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author:zr
 * @CreateTime:2023-02-16
 * @Description：hdfs相关操作
 */
public class HdfsTest {

    FileSystem hdfs = null;
//  HDFS组件的active节点的实例地址 ip+固定port:8020
//  配置keeplive之后，可直接指向active节点
    String uri = "hdfs://172.18.1.90:8020";

    @Before
    public void init() throws Exception {
        System.setProperty("java.security.krb5.conf",  "krb5.conf");
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("fs.defaultFS", uri);
//      组件配置中protection 需要一一对应
        conf.set("hadoop.rpc.protection", "authentication");
        conf.set("dfs.data.transfer.protection", "authentication");
        UserGroupInformation.setConfiguration(conf);
//      远程客户端的用户和keytab文件
        UserGroupInformation.loginUserFromKeytab("zuser01/test@KERBEROS.COM", "zrtest.keytab");
        hdfs = FileSystem.get(conf);
    }

    @Test
    public void ls() throws Exception {
        // 查看当前目录  等于 ls命令
        RemoteIterator<LocatedFileStatus> remoteIterator = hdfs.listFiles(new Path("/"), true);
        while (remoteIterator.hasNext()) {
            LocatedFileStatus next = remoteIterator.next();
            System.out.println(next.getPermission() + "  " + next.getPath() + "   " + next.getLen() + "字节");
        }
        hdfs.close();
    }


    @Test
    public void writeData() throws Exception{
        Path path  =  new Path(uri+"/yarnScheduler/yarnSche.txt");
        FSDataOutputStream outputStream = hdfs.create(path);
        outputStream.write("hello HDFS word and say hello!".getBytes());
        outputStream.close();
        hdfs.close();
        System.out.println("文件【"+path+"写入成功");
    }

//  查看文件内容
    @Test
    public void catText() throws Exception{
        FSDataInputStream fs = hdfs.open(new Path("/yarnScheduler/yarnSche.txt"));
        IOUtils.copyBytes(fs,System.out,1024);
    }





    @Test
    public void cat() throws Exception {
        FSDataInputStream in = hdfs.open(new Path(uri + "/yarnScheduler/yarnSche.txt"));
        IOUtils.copyBytes(in, System.out, 1024);
//        hdfs.close();
//        BufferedReader br = new BufferedReader(new InputStreamReader(in));
//        //定义字符串
//        String nextLine = "";
//        //在控制台输出读取的内容
//        while ((nextLine = br.readLine()) != null) {
//            //在控制台输出读取的内容
//            System.out.println(nextLine);
//        }
        //关闭缓冲字符输入流
//        br.close();
//        //关闭文件系统数据字节输出流
//        in.close();
//        //关闭文件系统
//        hdfs.close();
    }

    //创建文件夹
    @Test
    public void mkdir() throws Exception {
        hdfs.mkdirs(new Path("/zrtest"));


    }
//   拷贝本地文件到HDFS
    @Test
    public void copyFromLocalFile() throws Exception {
        hdfs.copyFromLocalFile(new Path("C:\\Users\\zran\\Downloads\\test.txt"), new Path("/zrtest/"));
    }

    //创建文件内容
    @Test
    public void create() throws Exception {
        FSDataOutputStream out = hdfs.create(new Path("/zrtest/a.txt"));
        out.writeUTF("hello hdfs");
        out.flush();
        out.close();
    }
//  将HDFS文件下载到本地
    @Test
    public void download() throws Exception {
        hdfs.copyToLocalFile(new Path("/zrtest/a.txt"), new Path("C:\\Users\\zran\\Downloads\\yarnSche.txt"));
        hdfs.close();
    }
}
