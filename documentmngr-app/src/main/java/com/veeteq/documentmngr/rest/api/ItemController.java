package com.veeteq.documentmngr.rest.api;

import com.veeteq.documentmngr.rest.dto.ItemsResponseDto;
import com.veeteq.documentmngr.service.ItemService;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ItemController implements ItemApi {
    private final Logger log = LoggerFactory.getLogger(ItemController.class);

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

    @GetMapping(path = "/items/search", produces = { "application/json" })
    public ResponseEntity<List<ItemDto>> searchItemsByName(@NotNull @Parameter(name = "name", description = "Name to search for", in = ParameterIn.QUERY) @Valid @RequestParam(value = "name", required = true) String name) {
        log.info("Request to search for accounts starting with name: {}", name);

        var response = itemService.searchItemsByName(name);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @Override
    public ResponseEntity<ItemDto> getItemById(Long id) {
        return itemService.getItemById(id)
                .map(itemDto -> ResponseEntity.ok()
                        //.headers(headers)
                        .body(itemDto))
                .orElse(ResponseEntity.notFound()
                        //.headers(headers)
                        .build());
    }

}
