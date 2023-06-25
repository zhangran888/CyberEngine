package com.datacyber.assembly;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
/**
 * @Author:zr
 * @CreateTime:2023-02-22
 * @Description：HBase相关操作
 */
public class HBaseTest {
    private Connection connection;
    private Admin admin;

    private Table table;

    @Before
    public void init() throws Exception {
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        System.setProperty("sun.security.krb5.debug", "false");
        System.setProperty("zookeeper.sasl.client.canonicalize.hostname", "false");
        System.setProperty("java.security.auth.login.config", "jaas.conf");

        Configuration conf = HBaseConfiguration.create();
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hbase.security.authentication", "kerberos");

        conf.set("hbase.zookeeper.quorum", "zknode-5753-25804:31473,zknode-5753-25805:30631,zknode-5753-25806:30205");
        conf.set("zookeeper.znode.parent", "/hbase/5774");
        conf.set("hbase.master.kerberos.principal", "hbase/_HOST@KERBEROS.COM");
        conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@KERBEROS.COM");
        conf.set("hbase.rpc.protection", "authentication");

        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab("zuser01/test@KERBEROS.COM", "zrtest.keytab");
        System.out.println(UserGroupInformation.isSecurityEnabled());
        System.out.println(UserGroupInformation.getLoginUser());

        HBaseAdmin.available(conf);
        connection = ConnectionFactory.createConnection(conf);
        System.out.println("connection:" + connection);
        admin = connection.getAdmin();
        System.out.println("admin:" + admin);
//        table = connection.getTable(TableName.valueOf("test01"));
    }


    /**
     * 查询表是否存在
     * @throws Exception
     */
    @Test
    public void exists() throws Exception {
        boolean exists = admin.tableExists(TableName.valueOf("test01"));
        System.out.println("获取数据-->" + exists);
    }

    /**
     * 建表
     * @throws Exception
     */
    @Test
    public void createTable() throws Exception {
        TableName tableName = TableName.valueOf("test01");
        if (admin.tableExists(tableName)) {
            admin.deleteTable(tableName);
        }
        // 2. 构建表描述构建器
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(tableName);

        // 3. 构建列蔟描述构建器
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("C1"));

        // 4. 构建列蔟描述
        ColumnFamilyDescriptor columnFamilyDescriptor = columnFamilyDescriptorBuilder.build();

        // 5. 构建表描述
        // 添加列蔟
        tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
        TableDescriptor tableDescriptor = tableDescriptorBuilder.build();

        // 6. 创建表
        admin.createTable(tableDescriptor);
    }


    /**
     * 插入数据
     * @throws Exception
     */
    @Test
    public void insert() throws Exception {
        Put put = new Put("liu".getBytes());
        put.addColumn("C1".getBytes(), "math".getBytes(), "100".getBytes());
        put.addColumn("C1".getBytes(), "chinese".getBytes(), "80".getBytes());
        table.put(put);
    }

    /**
     * 查询获取数据
     * @throws Exception
     */
    @Test
    public void get() throws Exception {
        Table table_test01 = connection.getTable(TableName.valueOf("test01"));
        String columnFamily = "C1";
        String columnQualifier = "chinese";
        String row = "liu";
        Get get = new Get(row.getBytes());
        get.addColumn(columnFamily.getBytes(), columnQualifier.getBytes());
        Result result = table_test01.get(get);
        System.out.println("获取结果数据--> " + new String(result.getValue(columnFamily.getBytes(), columnQualifier.getBytes())));
        TimeUnit.SECONDS.sleep(3);
    }


    /**
     * 查询表数据
     * @throws Exception
     */
    @Test
    public void showTable() throws Exception {
        TableName[] tableNames = admin.listTableNames();
        for (TableName tableName : tableNames) {
            System.out.println("查询表名-->" + tableName.getNameAsString());
        }
    }

    @After
    public void after() throws Exception {
        if (admin != null) {
            admin.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
