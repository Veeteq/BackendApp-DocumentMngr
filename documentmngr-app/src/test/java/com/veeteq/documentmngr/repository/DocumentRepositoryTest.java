package com.veeteq.documentmngr.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DocumentRepositoryTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @DisplayName("Find all documents")
    @Test
    void testFindAll() {
        var count = documentRepository.findAll().size();
        assertTrue(count >= 2);
    }

    @DisplayName("Count documents by Account")
    @Test
    void testCountByAccount() {
        var account = accountRepository.findById(3L).get();
        var count = documentRepository.countByAccount(account);
        assertEquals(1, count);
    }
}
