package com.stackoverflow.beta.script;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.CreateTopicsResult;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTopicCreator {

    public static void main(String[] args) {
        // Define the properties for the AdminClient
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka server address

        // Create AdminClient instance
        try (AdminClient adminClient = AdminClient.create(props)) {

            // Define the topic configuration
            String topicName = "stackoverflow-demo1";
            int numPartitions = 3;  // Number of partitions
            short replicationFactor = 1;  // Replication factor

            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);

            // Create topic
            CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

            // Wait for the topic creation to complete
            createTopicsResult.all().get();
            System.out.println("Topic created successfully!");

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.err.println("Failed to create topic: " + e.getMessage());
        }
    }
}
