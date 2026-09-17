package com.veeteq.documentmngr.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
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

    @DisplayName("Find distinct document names by pattern")
    @Test
    void testFindDistinctNames() {
        var result = documentRepository.findDistinctNames("home");
        assertTrue(result.stream().allMatch(name -> name.toLowerCase().contains("home")));
    }

    @DisplayName("Find distinct document names case insensitive")
    @Test
    void testFindDistinctNamesCaseInsensitive() {
        var lowerCaseResult = documentRepository.findDistinctNames("home");
        var upperCaseResult = documentRepository.findDistinctNames("HOME");
        assertEquals(lowerCaseResult, upperCaseResult);
    }

    @DisplayName("Find distinct document comments by pattern")
    @Test
    void testFindDistinctComments() {
        var result = documentRepository.findDistinctComments("payment");
        assertTrue(result.stream().allMatch(comment -> comment.toLowerCase().contains("payment")));
    }

    @DisplayName("Find distinct document comments case insensitive")
    @Test
    void testFindDistinctCommentsCaseInsensitive() {
        var lowerCaseResult = documentRepository.findDistinctComments("payment");
        var upperCaseResult = documentRepository.findDistinctComments("PAYMENT");
        assertEquals(lowerCaseResult, upperCaseResult);
    }

    @DisplayName("Find distinct document names returns no duplicates")
    @Test
    void testFindDistinctNamesReturnsDistinctValues() {
        var result = documentRepository.findDistinctNames("");
        assertEquals(result.size(), result.stream().distinct().count());
    }

}
