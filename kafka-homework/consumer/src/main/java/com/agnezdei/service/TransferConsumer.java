package com.agnezdei.service;

import com.agnezdei.model.TransferEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;

@Service
public class TransferConsumer {
    private static final Logger log = LoggerFactory.getLogger(TransferConsumer.class);
    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    public TransferConsumer(TransferService transferService, ObjectMapper objectMapper) {
        this.transferService = transferService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transfers", groupId = "transfer-group")
    public void handleTransfer(String message, Acknowledgment ack) {
        log.info("Received message: {}", message);
        TransferEvent event;
        try {
            event = objectMapper.readValue(message, TransferEvent.class);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
            return;
        }

        try {
            boolean success = transferService.processTransfer(event);
            if (!success) {
                transferService.saveFailedTransfer(event);
                log.info("Transfer saved as FAILED: {}", event);
            } else {
                log.info("Transfer processed successfully: {}", event);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Transaction failed, saving transfer with FAILED status", e);
        }
    }
}