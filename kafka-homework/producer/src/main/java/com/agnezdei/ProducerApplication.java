package com.agnezdei;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.TransactionManager;

@SpringBootApplication
@EnableScheduling
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Bean
    public NewTopic transfersTopic() {
        return TopicBuilder.name("transfers")
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Primary
    @Bean
    public TransactionManager transactionManager(KafkaTransactionManager<?, ?> kafkaTm) {
        return kafkaTm;
    }
}