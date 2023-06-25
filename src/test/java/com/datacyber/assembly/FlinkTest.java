package com.datacyber.assembly;

import lombok.val;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.Collector;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


/**
 * @Author:zr
 * @CreateTime:2023-03-10
 * @Description：Flink相关操作
 */
public class FlinkTest {


    /**
     * 基础统计测试
     * @throws Exception
     */
    @Test
    public void flinkCount() throws Exception {

//      创建执行环境
        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.getExecutionEnvironment();
//      读取文件
        DataSource<String> txtDataSource = executionEnvironment.readTextFile("D:\\CyberOps-Project\\keberos-test\\记录.txt");
        // 3. 转换数据格式
        FlatMapOperator<String, Tuple2<String, Long>> wordAndOne = txtDataSource.flatMap((String line, Collector<Tuple2<String, Long>> out) -> {
            String[] strs = line.split(" ");
            for (String str : strs) {
                out.collect(Tuple2.of(str, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG)); // lambda 使用泛型，由于泛型擦除，需要显示的声明类型信息
        // 4. 按照word 进行分组(按照第一个字段分组。 也就是按照String 类型的词分组). 有下面两种方式
        // 第一种，指定属性名称。 f0 是 org.apache.flink.api.java.tuple.Tuple2.f0
//        UnsortedGrouping<Tuple2<String, Long>> tuple2UnsortedGrouping = wordAndOne.groupBy("f0");
        UnsortedGrouping<Tuple2<String, Long>> tuple2UnsortedGrouping = wordAndOne.groupBy(0);
        // 5. 分组聚合统计
        AggregateOperator<Tuple2<String, Long>> sum = tuple2UnsortedGrouping.sum(1);
        // 6. 打印结果
        sum.print();
        System.out.println("-----");

    }


    @Test
    public void flinkStandalone() throws Exception {
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        RestClusterClient<StandaloneClusterId> client = null;
        Configuration configuration = new Configuration();
        configuration.setString(RestOptions.ADDRESS, "jobmanager-5795-26043");
//        configuration.setInteger(JobManagerOptions.PORT, 6123);
        configuration.setInteger(RestOptions.PORT, 30917);
        client = new RestClusterClient<StandaloneClusterId>(configuration, StandaloneClusterId.getInstance());
//      远程客户端的用户和keytab文件
        UserGroupInformation.loginUserFromKeytab("zuser01/test@KERBEROS.COM", "zrtest.keytab");

        String jarFilePath = "D:\\CyberOps-Project\\keberos-test\\testJar\\WordCount.jar";
        File jarFile=new File(jarFilePath);
        SavepointRestoreSettings savepointRestoreSettings=SavepointRestoreSettings.none();
        PackagedProgram program = PackagedProgram.newBuilder()
                .setConfiguration(configuration)
                .setEntryPointClassName("org.apache.flink.examples.java.wordcount.WordCount")
                .setJarFile(jarFile)
                .setSavepointRestoreSettings(savepointRestoreSettings).build();
        int parallelism = 1;
        JobGraph jobGraph= PackagedProgramUtils.createJobGraph(program,configuration,parallelism,false);
        CompletableFuture<JobID> result = client.submitJob(jobGraph);
        JobID jobId=  result.get();
        System.out.println("提交完成");
        System.out.println("jobId:"+ jobId.toString());
    }




}
