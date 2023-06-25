package com.datacyber.assembly;

import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;

import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.io.IOException;

/**
 * @Author:zr
 * @CreateTime:2023-03-08
 * @Description：OpenLDAP用户创建、列表查询
 */
public class OpenLdapTest {

    @Test
    public void testAddUser() throws IOException {
        String useraddUri = "http://172.18.1.121:30146/api/ops/v1/auth/users/add";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(useraddUri);
        //kerberosClusterId来自kerberosid
        String jsonData = "{\n" +
                "    \"users\": [\n" +
                "        {\n" +
                "            \"cn\": \"test01\",\n" +
                "            \"userPassword\": \"123456\",\n" +
                "            \"realm\": \"KERBEROS.COM\",\n" +
                "            \"instance\":\"test\",\n" +
                "            \"attrs\": [\n" +
                "                {\n" +
                "                    \"type\": \"sn\",\n" +
                "                    \"vals\": [\n" +
                "                        \"test\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"cn\",\n" +
                "                    \"vals\": [\n" +
                "                        \"admin_zr\"\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"userPassword\",\n" +
                "                    \"vals\": [\n" +
                "                        \"123456\"\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"kerberosClusterId\": 5749\n" +
                "}";
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("jwtToken", "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJyb2wiOnsiQUxMIjpbMl0sIkN5YmVyT3Bz5rWL6K-V54mIIjpbMl19LCJqdGkiOiI3MDM0LDE4OSIsImlzcyI6IlNuYWlsQ2xpbWIiLCJpYXQiOjE2NzgyMzc4MDUsInN1YiI6IjY3ZmFhZmIzYjY1ZTQ4ZWZiYzNiLOa1i-ivleW8oCx0ZXN0X3pyIiwiYXVkIjoiW3tcInJvbGVJZFwiOjIsXCJ2ZXJzaW9uTmFtZVwiOlwiQ3liZXJPcHPmtYvor5XniYhcIixcInZlcnNpb25JZFwiOjIsXCJyb2xlTmFtZVwiOlwiQ3liZXJPcHNBZG1pblwifV0ifQ.A7wtDHE88Xoyh8YYb9AEY2vHMI0IFK-DsFDIJnfLN3E");
        StringEntity entity = new StringEntity(jsonData, "UTF-8");
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);

        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        JSONObject object = new JSONObject(result);
        System.out.println("用户新建结果-->：" + object);
    }

    /**
     * 获取用户全部数据
     */
/*    @Test
    public void getUserList() {
        try {
            // 获取初始上下文
            InitialDirContext ctx = new InitialDirContext();
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // 搜索具有指定属性的项目
            // 在搜索树的顶部
            NamingEnumeration objs = ctx.search(
                    "ldap://172.18.1.121:30162/dc=em,dc=com",
                    "(objectClass=*)", searchControls);

            // 循环浏览搜索中返回的对象
            while (objs.hasMoreElements()) {
                // 对象
                SearchResult match = (SearchResult) objs.nextElement();
                // 打印出节点名称
                System.out.println("Found " + match.getName() + ":");
                // 获取节点的属性
                Attributes attrs = match.getAttributes();
                NamingEnumeration e = attrs.getAll();
                // 循环浏览属性
                while (e.hasMoreElements()) {
                    // G获取下一个属性
                    Attribute attr = (Attribute) e.nextElement();
                    // 打印出属性的值
                    System.out.print(attr.getID() + " = ");
                    System.out.println(attr.contains("krbPrincipalName") + "打印了吗");
                    for (int i = 0; i < attr.size(); i++) {
                        if (i > 0) System.out.print(", ");
                        System.out.print(attr.get(i));
                    }
                    System.out.println();
                    break;
                }
                System.out.println("---------------------------------------");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }*/


    /**
     * 获取用户名称列表
     */
    @Test
    public void getUserListName() {
        try {
            // 获取初始上下文
            InitialDirContext ctx = new InitialDirContext();
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // 搜索具有指定属性的项目
            // 在搜索树的顶部
            NamingEnumeration objs = ctx.search(
                    "ldap://172.18.1.121:30162/dc=em,dc=com",
                    "(objectClass=*)", searchControls);

            // 循环浏览搜索中返回的对象
            int i = 0;
            while (objs.hasMoreElements()) {
                // 对象
                SearchResult match = (SearchResult) objs.nextElement();
                // 打印出节点名称
                if (match.getName().contains("krbPrincipalName")) {
                    System.out.println(match.getName());
                    i++;
                }
            }
            System.out.println("统计的用户列表数量-->" + i);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


}
