package com.veeteq.documentmngr.service;

import com.veeteq.documentmngr.rest.dto.ItemDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> getItems();

    List<ItemDto> getItems(Pageable pageable);

    List<ItemDto> searchItemsByName(String pattern);

    List<ItemDto> getItemsWithPattern(String pattern, Pageable pageable);

    Optional<ItemDto> getItemById(Long id);

}
