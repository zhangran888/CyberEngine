package com.datacyber.assembly;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @Author:zr
 * @CreateTime:2023-02-16
 * @Description：zookeeper
 */
public class ZookeeperTest {

    @Test
    public void test() throws Exception {
        System.setProperty("java.security.auth.login.config", "jaas.conf");
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        System.setProperty("zookeeper.server.principal", "zookeeper/zknode-5753-25804@KERBEROS.COM");

        String url = "172.18.1.121:31473";

        ZooKeeper zooKeeper = new ZooKeeper(url, 10 * 20 * 1000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });

//        System.out.println(zooKeeper.getState());
//        System.out.println("==========================================================");
//        byte[] data = zooKeeper.getData("/hbase/4711/master", new Watcher() {
//            @Override
//            public void process(WatchedEvent watchedEvent) {
//
//            }
//        }, new Stat());
//        System.out.println(new String(data));

        System.out.println("==========================================================");
        byte[] data = zooKeeper.getData("/", true, new Stat());
        System.out.println(new String(data, StandardCharsets.UTF_8));
        System.out.println("获取数据成功！");

//        List<String> children = zooKeeper.getChildren("/hbase/4777", true);
//        for (String child : children) {
//            System.out.println(child);
//        }
    }
}
