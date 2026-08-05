package com.veeteq.documentmngr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.CategoryRepository;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

public class BaseTest {

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected DocumentRepository documentRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    protected MockMvc mockMvc;

    protected Item item;
    protected Account account;
    protected Document document;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new LogbookFilter(Logbook.create()))
                .build();
        item = itemRepository.findById(3L).get();
        account = accountRepository.findById(1L).get();
        document = documentRepository.findById(1L).get();
    }
}
