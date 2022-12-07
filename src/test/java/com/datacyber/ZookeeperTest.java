package com.datacyber;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

import java.util.List;

/**
 * @author wjc
 * @since 2022/12/07  15:08
 */
public class ZookeeperTest {

    @Test
    public void test() throws Exception{
        System.setProperty("java.security.auth.login.config", "zookeeper\\zk-client-jaas.conf");
        System.setProperty("java.security.krb5.conf", "zookeeper\\krb5.conf");//Zookeeper 服务的 principal 的 primary 部分
        System.setProperty("zookeeper.server.principal", "zookeeper/zknode-4292-14322@CYBEROPS.DATAC.COM");




        String url = "172.18.1.121:31773,172.18.1.121:31421,172.18.1.121:30734";

        ZooKeeper zooKeeper = new ZooKeeper(url, 10 * 20 * 1000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
        System.out.println(zooKeeper.getState());

        List<String> children = zooKeeper.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
    }
}
