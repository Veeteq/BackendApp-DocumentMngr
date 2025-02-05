package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.rest.api.ItemController;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ItemControllerTest extends BaseTest {

    @DisplayName("Test List Items")
    @Test
    void testListItems() throws Exception {
        mockMvc.perform(get(ItemController.BASE_URL.concat("/v1/items"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @DisplayName("Test Get Item By Id")
    @Test
    void testGetItemById() throws Exception {
        mockMvc.perform(get(ItemController.BASE_URL.concat("/v1/items/{itemId}"), item.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(item.getId()));
    }

    @DisplayName("Test Create Item")
    @Test
    void testCreateItem() throws Exception {
        ItemDto dto = new ItemDto()
                .itemName("Test Item")
                .itemCategory(new CategoryDto().categoryId(1L).categoryName("CAT_01"));
        mockMvc.perform(post(ItemController.BASE_URL.concat("/v1/items"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}
