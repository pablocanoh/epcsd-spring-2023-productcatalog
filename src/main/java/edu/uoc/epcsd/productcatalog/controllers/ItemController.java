package edu.uoc.epcsd.productcatalog.controllers;


import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateItemRequest;
import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Operation(summary = "Get all items")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getAllItems() {
        log.trace("getAllItems");

        return itemService.findAll();
    }

    @Operation(summary = "Get item by id")
    @GetMapping("/{serialNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> getItemById(@PathVariable @NotNull String serialNumber) {
        log.trace("getItemById");

        return itemService.findBySerialNumber(serialNumber).map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create item")
    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody CreateItemRequest createItemRequest) {
        log.trace("createItem");

        log.trace("Creating item " + createItemRequest);
        String serialNumber = itemService.createItem(createItemRequest.getProductId(),
                createItemRequest.getSerialNumber()).getSerialNumber();
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{serialNumber}")
                .buildAndExpand(serialNumber)
                .toUri();

        return ResponseEntity.created(uri).body(serialNumber);
    }

    @Operation(summary = "Update item status")
    @PatchMapping("/{serialNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateItemStatus(@PathVariable @NotNull String serialNumber, @RequestBody Boolean status) {
        log.trace("updateItemStatus");

        log.trace("Updating item status " + serialNumber);
        itemService.setOperational(serialNumber, status);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{serialNumber}")
                .buildAndExpand(serialNumber)
                .toUri();

        return ResponseEntity.created(uri).body(serialNumber);
    }
}
