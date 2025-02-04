package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.AccountRepository;
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
    private WebApplicationContext wac;

    protected MockMvc mockMvc;

    protected Item item;
    protected Account account;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        item = itemRepository.findById(103L).get();
        account = accountRepository.findById(1L).get();
    }
}
