package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.DocumentMngrApp;
import com.veeteq.documentmngr.rest.api.ItemController;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DocumentMngrApp.class)
public class ItemControllerTest extends BaseTest {

    @DisplayName("Test List Items")
    @Test
    void testListItems_Success() throws Exception {
        mockMvc.perform(get(ItemController.BASE_URL.concat("/v1/items"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @DisplayName("Test Get Item By Id")
    @Test
    void testGetItemById_Success() throws Exception {
        mockMvc.perform(get(ItemController.BASE_URL.concat("/v1/items/{itemId}"), item.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(item.getId()));
    }

    @DisplayName("Test Create Item")
    @Test
    void testCreateItem_Success() throws Exception {
        ItemRequestDto dto = new ItemRequestDto()
                .itemName("Test Item")
                .categoryId(1L);
        mockMvc.perform(post(ItemController.BASE_URL.concat("/v1/items"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @DisplayName("Test Update Item")
    @Test
    void testUpdateItem_Success() throws Exception {
        long itemIdToSearch = 3;
        ItemRequestDto dto = new ItemRequestDto()
                .itemName("Updated Test Item #3")
                .categoryId(2L);
        mockMvc.perform(put(ItemController.BASE_URL.concat("/v1/items/{itemId}"), itemIdToSearch)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value(itemIdToSearch))
                .andExpect(jsonPath("$.itemName").value(dto.getItemName()))
                .andExpect(jsonPath("$.itemCategory.categoryId").value(dto.getCategoryId()));
    }
}
