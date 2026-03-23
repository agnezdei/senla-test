package com.agnezdei.service;

import com.agnezdei.model.Account;
import com.agnezdei.model.TransferEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TransferProducer {

    private static final Logger log = LoggerFactory.getLogger(TransferProducer.class);
    private static final String TOPIC = "transfers";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private ObjectMapper objectMapper;

    private final AtomicLong transferIdGenerator = new AtomicLong(1);

    @Scheduled(fixedDelay = 200) // 5 сообщений в секунду
    public void generateAndSend() {
        try {
            Map<Long, Account> accountsMap = dataInitializer.getAccountsMap();
            List<Long> accountIds = List.copyOf(accountsMap.keySet());
            if (accountIds.size() < 2) {
                log.warn("Not enough accounts to generate transfer");
                return;
            }

            long fromId = accountIds.get(ThreadLocalRandom.current().nextInt(accountIds.size()));
            long toId = fromId;
            while (toId == fromId) {
                toId = accountIds.get(ThreadLocalRandom.current().nextInt(accountIds.size()));
            }

            BigDecimal amount = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 1000));
            long transferId = transferIdGenerator.getAndIncrement();

            TransferEvent event = new TransferEvent(transferId, fromId, toId, amount);

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, json);
            log.info("Sent transfer: {}", event);
        } catch (Exception e) {
            log.error("Error generating transfer", e);
        }
    }
}