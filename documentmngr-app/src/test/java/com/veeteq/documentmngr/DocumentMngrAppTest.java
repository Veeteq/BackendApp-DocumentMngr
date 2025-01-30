package com.veeteq.documentmngr;

import com.veeteq.documentmngr.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DocumentMngrAppTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void testDataLoad() {
        assertThat(accountRepository.count()).isGreaterThan(0L);
    }
}
