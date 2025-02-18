package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.CategoryType;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @DisplayName("Test mapping from Item entity to ItemDto")
    @Test
    public void test_ItemEntity_To_ItemDto() {
        // Arrange
        Category category = Category.builder()
                .withId(1L)
                .withName("Test Category")
                .withCategoryType(CategoryType.Exp)
                .build();

        Item item = Item.builder()
                .withId(1L)
                .withName("Test Item")
                .withCategory(category)
                .build();

        // Act
        ItemDto itemDto = itemMapper.toDto(item);

        // Assert
        assertEquals(item.getId(), itemDto.getItemId());
        assertEquals(item.getName(), itemDto.getItemName());
        assertEquals(category.getId(), itemDto.getItemCategory().getCategoryId());
        assertEquals(category.getName(), itemDto.getItemCategory().getCategoryName());

        assertEquals(3, itemDto.getClass().getDeclaredFields().length);
    }

    @DisplayName("Test mapping from ItemDto to Item entity")
    @Test
    public void test_ItemDto_To_ItemEntity() {
        // Arrange
        Category category = Category.builder()
                .withId(1L)
                .withName("Test Category")
                .withCategoryType(CategoryType.Exp)
                .build();

        ItemRequestDto itemDto = new ItemRequestDto();
        itemDto.setItemId(1L);
        itemDto.setItemName("Test Item");
        itemDto.setCategoryId(3L);

        // Act
        Item item = itemMapper.toEntity(itemDto, category);

        // Assert
        assertEquals(itemDto.getItemId(), item.getId());
        assertEquals(itemDto.getItemName(), item.getName());
        assertEquals(category.getId(), item.getCategory().getId());
        assertEquals(category.getName(), item.getCategory().getName());
        assertEquals(3, item.getClass().getDeclaredFields().length);
    }

    @DisplayName("Test mapping from Page<Item> to ItemsResponseDto")
    @Test
    public void test_EntitiesPage_To_ItemsResponseDto() {
        // Arrange
        Category category = Category.builder()
                .withId(1L)
                .withName("Test Category")
                .withCategoryType(CategoryType.Exp)
                .build();
        Item item1 = Item.builder()
                .withId(1L)
                .withName("Item #1")
                .withCategory(category)
                .build();

        Item item2 = Item.builder()
                .withId(2L)
                .withName("Item #2")
                .withCategory(category)
                .build();

        List<Item> itemList = Arrays.asList(item1, item2);
        Page<Item> itemPage = new PageImpl<>(itemList, Pageable.unpaged(), itemList.size());

        // Act
        ItemsResponseDto responseDto = itemMapper.toDto(itemPage);

        // Assert
        assertEquals(itemList.size(), responseDto.getData().size());
        assertEquals(item1.getId(), responseDto.getData().get(0).getItemId());
        assertEquals(item1.getName(), responseDto.getData().get(0).getItemName());
        assertEquals(item2.getId(), responseDto.getData().get(1).getItemId());
        assertEquals(item2.getName(), responseDto.getData().get(1).getItemName());
        assertEquals(1, responseDto.getTotalPages()); // Since we have only one page
        assertEquals(itemList.size(), responseDto.getTotalItems());
        assertEquals(2, responseDto.getPageSize());
        assertEquals(0, responseDto.getCurrentPage());
        assertEquals(5, responseDto.getClass().getDeclaredFields().length);
    }
}