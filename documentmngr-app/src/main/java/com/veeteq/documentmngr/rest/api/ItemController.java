package com.veeteq.documentmngr.rest.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import com.veeteq.documentmngr.service.ItemService;

@RestController
@RequestMapping(path = ItemController.BASE_URL)
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class ItemController implements ItemApi {
    public  final static String BASE_URL = "/api";
    private final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public ResponseEntity<ItemsResponseDto> listItems(Integer pageNumber, Integer pageSize, String orderBy, String orderDirection) {
        String pattern = null;
        var direction = Sort.Direction.fromString(orderDirection);
        var sort = Sort.by(direction, orderBy);
        var pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        ItemsResponseDto result;
        if (pattern == null) {
            result = itemService.getItems(pageRequest);
        } else {
            result = itemService.getItemsWithPattern(pattern, pageRequest);
        }
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<ItemDto>> searchItems(UUID transactionId, String name, String category, String acceptLanguage) {
        LOGGER.info("Request to search for items starting with name: {}", name);
        var response = itemService.searchItemsByNameOrCategory(name, category);
        return ResponseEntity
                .ok()
                .body(response);
    }


    @Override
    public ResponseEntity<Void> createItem(ItemRequestDto dto) {
        var savedItem = itemService.save(dto);

        var uriComponents = UriComponentsBuilder.fromPath(BASE_URL.concat("/v1/items".concat("/{item_id}")))
                .buildAndExpand(savedItem.getItemId());
        URI uri = URI.create(uriComponents.getPath());

        return ResponseEntity.created(uri).build();
    }

    @Override
    public ResponseEntity<ItemDto> getItemById(Long id) {
        LOGGER.info("Request received to search for item by Id: {}", id);

        var itemDto = itemService.getItemById(id);
        return ResponseEntity.ok()
                //.headers(headers)
                .body(itemDto);
    }

    @Override
    public ResponseEntity<ItemDto> updateItem(Long id, ItemRequestDto dto) {
        LOGGER.info("Request received to update item: {}. Item Id: {}", dto, id);

        var updated = itemService.updateItem(id, dto);
        return ResponseEntity.ok()
                //.headers(headers)
                .body(updated);
    }

    @Override
    public ResponseEntity<Void> deleteItem(Long id) {
        LOGGER.info("Request received to delete item with Id: {}", id);

        itemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
