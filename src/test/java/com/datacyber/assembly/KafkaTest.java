package com.datacyber.assembly;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaFuture;

import java.time.Duration;
import java.util.*;

import static org.datanucleus.NucleusContextHelper.random;
/**
 * @Author:zr
 * @CreateTime:2023-03-08
 * @Description：kafka获取topic列表、生产消息、消费消息、创建topic、删除topic
 *
 */
public class KafkaTest {


    public static void main(String[] args) {
        ProducerConfig();
    }


    /**
     * 初始化数据
     */
    public static void ProducerConfig(){
        System.setProperty("java.security.auth.login.config","jaas.conf");
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        Properties props = new Properties();
        props.put("bootstrap.servers",  "kafkanode-5823-26185:32428,kafkanode-5823-26186:32468,kafkanode-5823-26187:31125");
        props.put("group.id", "kafkanode-5823-26185,kafkanode-5823-26186,kafkanode-5823-26187");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // sasl
        props.put("sasl.mechanism", "GSSAPI");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.kerberos.service.name", "kafka");

//        String topicName="";
//        Set<String> obj = (Set<String>) getTopicList(props);
//        for (Object object: obj){
//            System.out.println("获取topic--->" + object.toString());
//            topicName = object.toString();
//        }

        MyProducer(props,"ztestTopic");
        MyConsumer(props,"ztestTopic");

//        createTopic(props, topicName);

//        deleteTopic(props,"ztopic");

    }


    /**
     * 获取topic列表
     * @param properties
     * @return
     */
    public static Object getTopicList(Properties properties){
        ListTopicsResult result = KafkaAdminClient.create(properties).listTopics();
        KafkaFuture<Set<String>> set = result.names();
        try {
//            for (Object obj: set.get()){
//                System.out.println("获取topic--->" + obj.toString());
//            }
            return set.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "获取topic失败";
    }

    /**
     * 创建topic
     * @param properties
     */
    public static void createTopic(Properties properties,String topicName){
        //      创建管理者
        AdminClient adminClient = AdminClient.create(properties);
        try{
            // 创建topic前，可以先检查topic是否存在，如果已经存在，则不用再创建了
            Set<String> topics = adminClient.listTopics().names().get();
            if (topics.contains(topicName)) {
                System.out.println("topic名称已经存在");
                return;
            }
            // 创建topic
            NewTopic newTopic = new NewTopic("test"+random.nextInt(999), 3, (short) 1);
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            result.all().get();
            System.out.println("创建Topic成功"+ result.values() );

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * 生产者
     * @param properties
     * @param topicName
     */
    public static void MyProducer(Properties properties,String topicName){
        // 指定key和消息体value的编码方式
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        // 创建并配置生产者
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
        // 创建消息，并指定分区
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topicName,"send message success");
        // 发送消息
        kafkaProducer.send(producerRecord);
        // 关闭生产者客户端
        kafkaProducer.close();

    }

    /**
     * 消费者
     * @param properties
     * @param topicName
     */
    public static void MyConsumer(Properties properties,String topicName){
        // 创建消费者客户端
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        // 订阅主题
        kafkaConsumer.subscribe(Collections.singletonList(topicName));

        ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            // 处理consumerRecord
            System.out.println("获取消费者信息--->" + consumerRecord.toString());
        }
        kafkaConsumer.close();
    }



    /**
     * 删除topic
     * @param properties
     * @param topicName
     */
    public static void deleteTopic(Properties properties,String topicName){
        //      创建管理者
        AdminClient adminClient = AdminClient.create(properties);
        DeleteTopicsResult deleteTopics = adminClient.deleteTopics(Collections.singleton(topicName));
        System.out.println("删除topic数据成功-->" + deleteTopics.toString());

    }







}
