package com.veeteq.documentmngr.service.jpa;

import com.veeteq.documentmngr.exception.ConflictException;
import com.veeteq.documentmngr.exception.NotFoundException;
import com.veeteq.documentmngr.mapper.ItemMapper;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.CategoryRepository;
import com.veeteq.documentmngr.repository.DocumentItemRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.repository.UtilityRepository;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import com.veeteq.documentmngr.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final DocumentItemRepository documentItemRepository;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository, DocumentItemRepository documentItemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.documentItemRepository = documentItemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public List<ItemDto> getItems() {
        //if (1==1) throw new DataIntegrityViolationException("Bla");
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
    public ItemDto getItemById(Long id) {
        var result = itemRepository.findById(id);
        return result.map(itemMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional
    public ItemDto save(ItemRequestDto dto) {
        var category = categoryRepository.getReferenceById(dto.getCategoryId());
        var item = itemMapper.toEntity(dto, category);
        var savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long id, ItemRequestDto dto) {
        var category = categoryRepository.getReferenceById(dto.getCategoryId());
        return itemRepository.findById(id)
                .map(item -> {
                    var updated = itemMapper.updateWith(dto, item, category);
                    var savedItem = itemRepository.save(updated);
                    return itemMapper.toDto(savedItem);
                }).orElseThrow(NotFoundException::new);
    }

    @Override
    public void deleteById(Long id) {
        itemRepository.findById(id).ifPresentOrElse(item -> {
            if (documentItemRepository.countByExpense_Item(item) > 0) {
                throw new ConflictException("Item is referenced by Document items");
            }
            itemRepository.delete(item);
        }, () -> {
            throw new NotFoundException("Item not found");
        });
    }
}
