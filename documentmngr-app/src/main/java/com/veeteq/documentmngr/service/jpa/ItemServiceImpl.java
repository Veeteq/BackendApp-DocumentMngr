package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.mapper.ItemMapper;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.CategoryRepository;
import com.veeteq.documentmngr.repository.EntityIdMapping;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import com.veeteq.documentmngr.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final UtilityRepository utilityRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository, UtilityRepository utilityRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.utilityRepository = utilityRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<ItemDto> getItems() {
        var result = itemRepository.findAll();
        return result.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemsResponseDto getItems(Pageable pageable) {
        var result = itemRepository.findAll(pageable);
        return itemMapper.toDto(result);
    }

    @Override
    public List<ItemDto> searchItemsByName(String pattern) {
        List<Item> result = itemRepository.findByNameContainingIgnoreCase(pattern);

        return StreamSupport.stream(result.spliterator(), false)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemsResponseDto getItemsWithPattern(String pattern, Pageable pageable) {
        var result = itemRepository.findByNameContainingIgnoreCase(pattern, pageable);
        return itemMapper.toDto(result);
    }

    @Override
    public Optional<ItemDto> getItemById(Long id) {
        var result = itemRepository.findById(id);
        return result.map(itemMapper::toDto);
    }

    @Override
    public ItemDto save(ItemRequestDto dto) {
        if (dto.getItemId() == null) {
            dto.setItemId(utilityRepository.getNextId(EntityIdMapping.ITEM));
        }
        var category = categoryRepository.getReferenceById(dto.getCategoryId());
        var item = itemMapper.toEntity(dto, category);
        var savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }
}
