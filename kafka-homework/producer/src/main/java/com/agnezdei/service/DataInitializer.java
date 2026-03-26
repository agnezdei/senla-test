package com.agnezdei.service;

import com.agnezdei.model.Account;
import com.agnezdei.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private AccountRepository accountRepository;

    private final Map<Long, Account> accountsMap = new ConcurrentHashMap<>();

    public DataInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @PostConstruct
    public void init() {
        if (accountRepository.count() == 0) {
            log.info("No accounts found, generating 1000 accounts...");
            for (int i = 0; i < 1000; i++) {
                Account account = new Account();
                account.setBalance(BigDecimal.valueOf(10000 + Math.random() * 90000));
                accountRepository.save(account);
                accountsMap.put(account.getId(), account);
            }
            log.info("Generated 1000 accounts.");
        } else {
            log.info("Loading existing accounts...");
            List<Account> accounts = accountRepository.findAll();
            for (Account account : accounts) {
                accountsMap.put(account.getId(), account);
            }
            log.info("Loaded {} accounts.", accountsMap.size());
        }
    }

    public Map<Long, Account> getAccountsMap() {
        return accountsMap;
    }
}