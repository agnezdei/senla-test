package com.agnezdei.service;

import com.agnezdei.model.TransferEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferConsumer.class);
    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    public TransferConsumer(TransferService transferService, ObjectMapper objectMapper) {
        this.transferService = transferService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "transfers",
            groupId = "transfer-group",
            containerFactory = "batchKafkaListenerContainerFactory",
            batch = "true"
    )
    public void handleTransfers(List<String> messages, Acknowledgment ack) {
        log.info("Received batch of {} messages", messages.size());

        for (String message : messages) {
            try {
                TransferEvent event = objectMapper.readValue(message, TransferEvent.class);
                boolean success = transferService.processTransfer(event);
                if (!success) {
                    transferService.saveFailedTransfer(event);
                    log.info("Transfer saved as FAILED: {}", event);
                } else {
                    log.info("Transfer processed successfully: {}", event);
                }
            } catch (Exception e) {
                log.error("Failed to process message: {}", message, e);
            }
        }

        ack.acknowledge();
        log.info("Batch acknowledged");
    }
}