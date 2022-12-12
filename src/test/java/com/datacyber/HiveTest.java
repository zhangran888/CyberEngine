package com.datacyber;

import com.alibaba.fastjson.JSONObject;
import com.datacyber.config.HiveConnUtil;
import com.datacyber.config.HiveJdbcConnParam;
import com.datacyber.config.SqlUtil;
import org.junit.Test;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author zxd
 */
public class HiveTest {
    public static void main(String[] args) {
        final HiveJdbcConnParam connParam = new HiveJdbcConnParam();
        connParam.setDriverName("org.apache.hive.jdbc.HiveDriver");
        connParam.setIp("172.18.1.61");
        connParam.setPort(31121);
        connParam.setDbName("test");
        // 开启kerbers的账号格式一般为  用户名@域名
        connParam.setUsername("test1/test@CYBEROPS.DATAC.COM");
        // 开启kerberos后 不需要密码了
        connParam.setPassword("test");
        // 是否开启kerberos认证
        connParam.setEnableKerberos(true);
        // 凭证，也就是跟在jdbc url后的;principle=的那一段
        connParam.setPrincipal("hive/hs-4661-21829@CYBEROPS.DATAC.COM");
        // kbr5认证配置文件路径 注意：里面是域名，那么连接的时候也是域名
        connParam.setKbr5FilePath("hive\\krb5.conf");
        // 密钥文件路径 用于认证验证
        connParam.setKeytabFilePath("hive\\test1-test.keytab");

        final Connection connection = new HiveConnUtil(connParam).getConnection();
        final SqlUtil sqlUtil = SqlUtil.build(connection);
//        final List<LinkedHashMap<String, Object>> tables = sqlUtil.querySql("insert into trans_student values(1,\'zxxxx\',10)");

//        final List<LinkedHashMap<String, Object>> tables = sqlUtil.querySql("update trans_student set name = \'zxxxssssxx\' where id = 1");
        final List<LinkedHashMap<String, Object>> tables = sqlUtil.querySql("select * from trans_student");

        for (LinkedHashMap<String, Object> table : tables) {
            final String s = JSONObject.toJSONString(table);
            System.out.println(s);
        }
    }

}
