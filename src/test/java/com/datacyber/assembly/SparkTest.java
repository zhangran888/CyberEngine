package com.datacyber.assembly;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext$;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.deploy.SparkSubmit;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.apache.spark.rdd.RDD;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author:zr
 * @CreateTime:2023-03-09
 * @Description：spark相关操作
 */
public class SparkTest {


    public static void main(String[] args) throws IOException {
        SparkLauncher launcher = new SparkLauncher();
        SparkAppHandle handle = launcher.setAppName("提交spark任务")
                .setMaster("spark://sparkmaster-5783-25977:7077")
                .setAppResource("hdfs:///spark-examples_2.12-3.2.2.jar")
                .setMainClass("org.apache.spark.examples.SparkPi")
                .setDeployMode("cluster")
//                .addAppArgs(new String[]{"hdfs://192.168.0.181:8020/opt/guoxiang/wordcount.txt", "hdfs://192.168.0.181:8020/opt/guoxiang/wordcount"})
                .setConf("spark.executor.memory","1g")
                .setConf("spark.cores.max","4")
                .setVerbose(true)
                .setSparkHome("D:\\用例\\CyberOPS\\测试数据\\winutils-master\\spark-3.2.2-bin-hadoop3.2")
                .addAppArgs("java.security.auth.login.config","jaas.conf")
                .startApplication(new SparkAppHandle.Listener() {
                    @Override
                    public void stateChanged(SparkAppHandle sparkAppHandle) {
                        System.out.println("stateChanged");

                    }

                    @Override
                    public void infoChanged(SparkAppHandle sparkAppHandle) {
                        System.out.println("infoChanged");

                    }
                });


        System.out.println("提交成功了吗--->" + handle.getAppId());
    }

    //ProcessBuilder执行shell命令
//    SparkLauncher

    /**
     * spark Standalone模式
     * @throws IOException
     */
    @Test
    public void sparkStandalone() throws IOException {
        System.setProperty("hadoop.home.dir","D:\\用例\\CyberOPS\\测试数据\\winutils-master\\hadoop-3.2.2");
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        try {
            System.out.println("11111111111111111");
            SparkConf conf = new SparkConf().setAppName("test").setMaster("spark://sparkmaster-5783-25977:31278");
//                    .setSparkHome("D:\\用例\\CyberOPS\\测试数据\\winutils-master\\spark-3.2.2-bin-hadoop3.2");
            conf.set("spark.submit.deployMode", "cluster");
            conf.set("spark.executor.cores","1");
            conf.set("spark.executor.memory", "800m");
            conf.set("spark.driver.host","172.18.2.54");
//            conf.set("spark.kerberos.principal","zuser01/test@KERBEROS.COM");
//            conf.set("spark.kerberos.keytab","zrtest.keytab");
            conf.set("spark.authenticate.enableSaslEncryption","true");
            conf.set("spark.authenticate","true");
            conf.set("spark.authenticate.secret","5783");
            Configuration configuration = new Configuration();
            UserGroupInformation.setConfiguration(configuration);
            UserGroupInformation.loginUserFromKeytab("zuser01/test@KERBEROS.COM", "zrtest.keytab");

            JavaSparkContext sc = new JavaSparkContext(conf);
            sc.addJar("D:\\CyberOps-Project\\keberos-test\\testJar\\spark-examples_2.12-3.2.2.jar");
            List<Integer> data = Arrays.asList(1,2,3);
            JavaRDD<Integer> distData = sc.parallelize(data);

            System.out.println("Standalone打印结果--> " + distData.count());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * spark on yarn提交任务
     * @throws IOException
     */
    @Test
    public void sparkCluster() throws IOException {
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        Configuration configuration = new Configuration();
        UserGroupInformation.setConfiguration(configuration);
//      远程客户端的用户和keytab文件
        UserGroupInformation.loginUserFromKeytab("zuser01/test@KERBEROS.COM", "zrtest.keytab");

        String[] param = new String[]{
                "--principal","zuser01/test@KERBEROS.COM",
                "--keytab","zrtest.keytab",
                "--master","yarn",
                "--deploy-mode","cluster",
                "--name","ZtestOnYarn",
                "--class","org.apache.spark.examples.SparkPi",
                "D:\\CyberOps-Project\\keberos-test\\testJar\\spark-examples_2.12-3.2.2.jar",
                "1"
        };
        SparkSubmit.main(param);
    }


}
