package com.veeteq.documentmngr.controller;

import com.veeteq.documentmngr.DocumentMngrApp;
import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.rest.api.ItemController;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DocumentMngrApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerTest extends BaseTest {

    @Value("${open.api.spec.url}")
    private String apiSpecification;

    @Order(value = 0)
    @DisplayName("Test List Items")
    @Test
    void testListItems_Success() throws Exception {
        mockMvc.perform(get(ItemController.BASE_URL.concat("/v1/items"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(apiSpecification))
                .andExpect(jsonPath("$.pageSize").value(25))
                .andExpect(jsonPath("$.totalItems").value(11))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.data.length()", greaterThan(0)));
    }

    @Order(value = 1)
    @DisplayName("Test Get Item By Id")
    @Test
    void testGetItemById_Success() throws Exception {
        mockMvc.perform(get(ItemController.BASE_URL.concat("/v1/items/{itemId}"), item.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(apiSpecification))
                .andExpect(jsonPath("$.itemId").value(item.getId()));
    }

    @Order(value = 2)
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
                //.andExpect(openApi().isValid(apiSpecification))
                .andExpect(header().exists("Location"));
    }

    @Order(value = 3)
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
                .andExpect(openApi().isValid(apiSpecification))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.itemId").value(itemIdToSearch))
                .andExpect(jsonPath("$.itemName").value(dto.getItemName()))
                .andExpect(jsonPath("$.itemCategory.categoryId").value(dto.getCategoryId()));
    }

    @Order(value = 4)
    @DisplayName("Test Update Item - When Item Does Not Exist")
    @Test
    void testUpdateItem_WhenItemDoesNotExist() throws Exception {
        long itemIdToSearch = 3827;
        ItemRequestDto dto = new ItemRequestDto()
                .itemName("Updated Test Item #3827")
                .categoryId(2L);
        mockMvc.perform(put(ItemController.BASE_URL.concat("/v1/items/{itemId}"), itemIdToSearch)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(apiSpecification));
    }

    @Order(value = 5)
    @DisplayName("Test Delete Item - When Item Exists")
    @Test
    void testDeleteItem_WhenItemExists() throws Exception {
        var item = Item.builder()
                .withId(14L)
                .withName("Test Item #14")
                .withCategory(categoryRepository.getReferenceById(1L))
                .build();
        var entity = itemRepository.save(item);

        mockMvc.perform(delete(ItemController.BASE_URL.concat("/v1/items/{itemId}"), entity.getId()))
                .andExpect(status().isNoContent())
                .andExpect(openApi().isValid(apiSpecification));

        assert itemRepository.findById(entity.getId()).isEmpty();
    }

    @Order(value = 6)
    @DisplayName("Test Delete Item - When Item Does Not Exist")
    @Test
    void testDeleteItem_WhenItemDoesNotExist() throws Exception {
        mockMvc.perform(delete(ItemController.BASE_URL.concat("/v1/items/{itemId}"), 9585))
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(apiSpecification));
    }

    @Order(value = 7)
    @DisplayName("Test Delete Item - When Item Is Referenced By Expense")
    @Test
    void testDeleteItem_WhenReferencedByExpense() throws Exception {
        var entity = itemRepository.findById(2L).get();

        mockMvc.perform(delete(ItemController.BASE_URL.concat("/v1/items/{itemId}"), entity.getId()))
                .andExpect(status().isConflict())
                .andExpect(openApi().isValid(apiSpecification));
    }

}
