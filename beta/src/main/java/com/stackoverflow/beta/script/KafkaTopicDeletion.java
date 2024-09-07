package com.stackoverflow.beta.script;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;

import java.util.Collections;
import java.util.Properties;

public class KafkaTopicDeletion {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        try (AdminClient adminClient = AdminClient.create(props)) {
            DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Collections.singletonList("stackoverflow-demo1"));
            deleteTopicsResult.all().get();  // Wait for the deletion to complete
            System.out.println("Topic deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
