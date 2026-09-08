package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {

    List<ItemDto> getItems();

    ItemsResponseDto getItems(Pageable pageable);

    ItemsResponseDto getItemsWithPattern(String pattern, Pageable pageable);

    ItemDto getItemById(Long id);

    ItemDto save(ItemRequestDto dto);

    ItemDto updateItem(Long id, ItemRequestDto dto);

    void deleteById(Long id);

    List<ItemDto> searchItemsByNameOrCategory(String name, String categoryName);
}
