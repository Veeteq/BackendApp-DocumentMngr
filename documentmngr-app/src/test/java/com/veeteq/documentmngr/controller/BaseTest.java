package com.veeteq.documentmngr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.DocumentRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class BaseTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentRepository documentRepository;

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
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        item = itemRepository.findById(3L).get();
        account = accountRepository.findById(1L).get();
        document = documentRepository.findById(2L).get();
    }
}
