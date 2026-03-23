package com.agnezdei.service;

import com.agnezdei.model.Account;
import com.agnezdei.model.Transfer;
import com.agnezdei.model.TransferEvent;
import com.agnezdei.model.TransferStatus;
import com.agnezdei.repository.AccountRepository;
import com.agnezdei.repository.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransferConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferConsumer.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "transfers", groupId = "transfer-group")
    public void handleTransfer(String message) {
        log.info("Received message: {}", message);
        TransferEvent event;
        try {
            event = objectMapper.readValue(message, TransferEvent.class);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
            return;
        }

        Optional<Account> fromAccountOpt = accountRepository.findById(event.getFromAccountId());
        Optional<Account> toAccountOpt = accountRepository.findById(event.getToAccountId());
        if (fromAccountOpt.isEmpty() || toAccountOpt.isEmpty()) {
            log.error("Validation failed: one of accounts not found. Transfer: {}", event);
            return;
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        if (fromAccount.getBalance().compareTo(event.getAmount()) < 0) {
            log.error("Validation failed: insufficient funds. Transfer: {}", event);
            return;
        }

        try {
            processTransfer(event, fromAccount, toAccount);
            log.info("Transfer processed successfully: {}", event);
        } catch (Exception e) {
            log.error("Transaction failed, saving transfer with FAILED status", e);
            saveFailedTransfer(event);
        }
    }

    @Transactional
    protected void processTransfer(TransferEvent event, Account fromAccount, Account toAccount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(event.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(event.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transfer transfer = new Transfer();
        transfer.setId(event.getId());
        transfer.setFromAccountId(event.getFromAccountId());
        transfer.setToAccountId(event.getToAccountId());
        transfer.setAmount(event.getAmount());
        transfer.setStatus(TransferStatus.COMPLETED);
        transferRepository.save(transfer);
    }

    protected void saveFailedTransfer(TransferEvent event) {
        Transfer transfer = new Transfer();
        transfer.setId(event.getId());
        transfer.setFromAccountId(event.getFromAccountId());
        transfer.setToAccountId(event.getToAccountId());
        transfer.setAmount(event.getAmount());
        transfer.setStatus(TransferStatus.FAILED);
        transferRepository.save(transfer);
    }
}