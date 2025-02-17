package com.veeteq.documentmngr.mapper;

import com.veeteq.documentmngr.model.Category;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "withId", source = "itemId")
    @Mapping(target = "withName", source = "itemName")
    @Mapping(target = "withCategory", source = "itemCategory")
    Item toEntity(ItemDto dto);

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "itemName", source = "name")
    @Mapping(target = "itemCategory", source = "category")
    ItemDto toDto(Item entity);

    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "totalItems", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "currentPage", source = "number")
    @Mapping(target = "data", source = "content")
    ItemsResponseDto toDto(Page<Item> items);

    @Mapping(target = "withId", source = "categoryId")
    @Mapping(target = "withName", source = "categoryName")
    @Mapping(target = "withCategoryType", constant = "Both")
    Category toEntity(CategoryDto dto);

    @Mapping(target = "categoryId", source = "id")
    @Mapping(target = "categoryName", source = "name")
    CategoryDto toDto(Category entity);
}
