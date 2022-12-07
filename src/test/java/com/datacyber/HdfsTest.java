package com.datacyber;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author wjc
 * @since 2022/12/07  14:22
 */
public class HdfsTest {

    FileSystem hdfs = null;

    String uri = "hdfs://172.18.5.214:8020";

    @Before
    public void init() throws Exception {
        System.setProperty("java.security.krb5.conf",  "hdfs\\krb5.conf");
        String USER_KEY = "test1/test@CYBEROPS.DATAC.COM";
        String KEY_TAB_PATH =  "hdfs\\test1-test.keytab";
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("fs.defaultFS", uri);
        conf.set("hadoop.rpc.protection", "privacy");
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(USER_KEY, KEY_TAB_PATH);
        hdfs = FileSystem.get(conf);
    }

    @Test
    public void ls() throws Exception {
        // 查看当前目录  等于 ls命令
        RemoteIterator<LocatedFileStatus> remoteIterator = hdfs.listFiles(new Path("/test"), true);
        while (remoteIterator.hasNext()) {
            LocatedFileStatus next = remoteIterator.next();
            System.out.println(next.getPermission() + "  " + next.getPath() + "   " + next.getLen() + "字节");
        }
        hdfs.close();
    }

    @Test
    public void cat() throws Exception {
        FSDataInputStream in = hdfs.open(new Path(uri + "/test/a.txt"));
        IOUtils.copyBytes(in, System.out, 1024);
        hdfs.close();
//        BufferedReader br = new BufferedReader(new InputStreamReader(in));
//        //定义字符串
//        String nextLine = "";
//        //在控制台输出读取的内容
//        while ((nextLine = br.readLine()) != null) {
//            //在控制台输出读取的内容
//            System.out.println(nextLine);
//        }
//        //关闭缓冲字符输入流
//        br.close();
//        //关闭文件系统数据字节输出流
//        in.close();
//        //关闭文件系统
//        hdfs.close();
    }

    @Test
    public void mkdir() throws Exception {
        Path path = new Path("/test");
        if (hdfs.exists(path)) {
            hdfs.delete(path, true);
        }
        hdfs.mkdirs(path);
        hdfs.close();
    }

    @Test
    public void copyFromLocalFile() throws Exception {
        hdfs.copyFromLocalFile(new Path("hello.txt"), new Path("/test/hhh.txt"));
    }

    @Test
    public void create() throws Exception {
        hdfs.create(new Path("/test/1449.txt"));
        hdfs.close();
    }

    @Test
    public void download() throws Exception {
        hdfs.copyToLocalFile(new Path("/test/hello.txt"), new Path("hello.txt"));
        hdfs.close();
    }
}
