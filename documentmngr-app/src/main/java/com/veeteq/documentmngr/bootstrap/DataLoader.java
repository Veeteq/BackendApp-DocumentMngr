package com.veeteq.documentmngr.bootstrap;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("test")
public class DataLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;

    @Autowired
    public DataLoader(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var list = List.of(
                new Account().setId(1L).setName("ABC"),
                new Account().setId(2L).setName("BCD"),
                new Account().setId(3L).setName("CDE"),
                new Account().setId(4L).setName("DEF"),
                new Account().setId(5L).setName("EFG"),
                new Account().setId(6L).setName("FGH"),
                new Account().setId(7L).setName("GHI"),
                new Account().setId(8L).setName("HIJ"),
                new Account().setId(9L).setName("IJK"),
                new Account().setId(10L).setName("JKL")
        );
        accountRepository.saveAll(list);
    }
}
