package com.datacyber.assembly;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.SaslOutputStream;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:zr
 * @CreateTime:2023-03-08
 * @Description：yarn获取任务列表
 */
public class YarnTest {
    YarnClient yarnClient;

    /**
     * 初始化数据
     *
     * @throws IOException
     */
    @Before
    public void init() throws IOException {
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
//      远程客户端的用户和keytab文件
        UserGroupInformation.loginUserFromKeytab("zuser01/test@KERBEROS.COM", "zrtest.keytab");

        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(conf);
        yarnClient.start();

    }

    /**
     * 获取任务列表
     */
    @Test
    public void getApplicationList() {
        try {
            List<ApplicationReport> app = yarnClient.getApplications();
            System.out.println("任务状态--> " + app.stream().map(ApplicationReport::getYarnApplicationState).collect(Collectors.toList()));
            List<ApplicationId> idList = app.stream().map(ApplicationReport::getApplicationId).collect(Collectors.toList());
            System.out.println("获取任务列表-->" + idList);
        } catch (YarnException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*    @Test
    public void createApplication(){
        //创建客户端application(这一步要在虚拟机开启resource manager   启动方式 yarn resourcemanager
        try {
            YarnClientApplication app = yarnClient.createApplication();
            GetNewApplicationResponse res = app.getNewApplicationResponse();
            System.out.println("获取的任务id-->" + res.getApplicationId());
        } catch (YarnException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 终止任务
     */
    @Test
    public void killApplication(){
        try {
            List<ApplicationReport> app = yarnClient.getApplications();
//            System.out.println("打印 -- >" + app.toString());
//            System.out.println("任务状态--> " + app.stream().map(ApplicationReport::getYarnApplicationState).collect(Collectors.toList()));
            for (int i = 0;i <app.size();i ++){
                if (app.get(i).getYarnApplicationState().toString().equals("RUNNING")){
                    System.out.println("即将终止的任务状态-->" + app.get(i).getYarnApplicationState() + "终止的状态id--->" + app.get(i).getApplicationId());
                    yarnClient.killApplication(app.get(i).getApplicationId());
                }
            }
            System.out.println("终止任务失败" );
        } catch (YarnException | IOException e) {
            e.printStackTrace();
            System.out.println("终止出现异常");
        }
    }



}
