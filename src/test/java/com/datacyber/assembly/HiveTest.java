package com.datacyber.assembly;

import com.alibaba.fastjson.JSONObject;
import com.datacyber.config.HiveConnUtil;
import com.datacyber.config.HiveJdbcConnParam;
import com.datacyber.config.SqlUtil;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author:zr
 * @CreateTime:2023-03-06
 * @Description：Hive相关操作
 */
public class HiveTest {
    public static void main(String[] args) {
        final HiveJdbcConnParam connParam = new HiveJdbcConnParam();
        connParam.setDriverName("org.apache.hive.jdbc.HiveDriver");
        connParam.setIp("172.18.1.121");
        connParam.setPort(32005);
        connParam.setDbName("");
        // 开启kerbers的账号格式一般为  用户名@域名
        connParam.setUsername("zuser01/test@KERBEROS.COM");
        // 开启kerberos后 不需要密码了
        connParam.setPassword("123456");
        // 是否开启kerberos认证
        connParam.setEnableKerberos(true);
        // 凭证，也就是跟在jdbc url后的;principle=的那一段
        connParam.setPrincipal("hive/hs-5824-26188@KERBEROS.COM");
        // kbr5认证配置文件路径 注意：里面是域名，那么连接的时候也是域名
        connParam.setKbr5FilePath("krb5.conf");
        // 密钥文件路径 用于认证验证
        connParam.setKeytabFilePath("zrtest.keytab");

        final Connection connection = new HiveConnUtil(connParam).getConnection();
        final SqlUtil sqlUtil = SqlUtil.build(connection);

//      查库表
//        final List<LinkedHashMap<String, Object>> showTable = sqlUtil.querySql("show databases");
//        for (LinkedHashMap<String, Object> table : showTable) {
//            final String s = JSONObject.toJSONString(table);
//            System.out.println("获取数据库---->"+s);
//        }
//        建表
//        String createTable = sqlUtil.createTable("create table testHvie(id int ,name string)");
//        System.out.println(createTable);
//        operateData可操作insert、update、delete
//        String insertData = sqlUtil.operateData("insert into testHvie values(1,'zhangsan')");
//        System.out.println(insertData);


        final List<LinkedHashMap<String, Object>> tables = sqlUtil.querySql("select * from testHvie");
        System.out.println("table数据-->" + tables);
        for (LinkedHashMap<String, Object> table : tables) {
            final String s = JSONObject.toJSONString(table);
            System.out.println("查询表数据-->" + s);
        }
    }

}
