package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> getItems();

    ItemsResponseDto getItems(Pageable pageable);

    List<ItemDto> searchItemsByName(String pattern);

    ItemsResponseDto getItemsWithPattern(String pattern, Pageable pageable);

    ItemDto getItemById(Long id);

    ItemDto save(ItemRequestDto dto);

    ItemDto updateItem(Long id, ItemRequestDto dto);

    void deleteById(Long id);
}
