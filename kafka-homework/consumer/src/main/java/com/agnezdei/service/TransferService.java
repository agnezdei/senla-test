package com.agnezdei.service;

import com.agnezdei.model.Account;
import com.agnezdei.model.Transfer;
import com.agnezdei.model.TransferEvent;
import com.agnezdei.model.TransferStatus;
import com.agnezdei.repository.AccountRepository;
import com.agnezdei.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public boolean processTransfer(TransferEvent event) {
        try {
            Optional<Account> fromOpt = accountRepository.findById(event.getFromAccountId());
            Optional<Account> toOpt = accountRepository.findById(event.getToAccountId());

            if (fromOpt.isEmpty() || toOpt.isEmpty()) {
                log.error("Account not found: from={}, to={}", event.getFromAccountId(), event.getToAccountId());
                return false;
            }

            Account from = fromOpt.get();
            Account to = toOpt.get();

            if (from.getBalance().compareTo(event.getAmount()) < 0) {
                log.error("Insufficient funds: account={}, balance={}, amount={}",
                        from.getId(), from.getBalance(), event.getAmount());
                return false;
            }

            from.setBalance(from.getBalance().subtract(event.getAmount()));
            to.setBalance(to.getBalance().add(event.getAmount()));
            accountRepository.save(from);
            accountRepository.save(to);

            Transfer transfer = new Transfer();
            transfer.setId(event.getId());
            transfer.setFromAccountId(event.getFromAccountId());
            transfer.setToAccountId(event.getToAccountId());
            transfer.setAmount(event.getAmount());
            transfer.setStatus(TransferStatus.COMPLETED);
            transferRepository.save(transfer);

            return true;
        } catch (Exception e) {
            log.error("Error processing transfer, saving as FAILED", e);
            saveFailedTransfer(event);
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransfer(TransferEvent event) {
        Transfer transfer = new Transfer();
        transfer.setId(event.getId());
        transfer.setFromAccountId(event.getFromAccountId());
        transfer.setToAccountId(event.getToAccountId());
        transfer.setAmount(event.getAmount());
        transfer.setStatus(TransferStatus.FAILED);
        transferRepository.save(transfer);
        log.info("Saved failed transfer: {}", event);
    }
}