package edu.uoc.epcsd.productcatalog.controllers;


import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateProductRequest;
import edu.uoc.epcsd.productcatalog.controllers.dtos.GetProductResponse;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import edu.uoc.epcsd.productcatalog.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Get all products and posible filter by name or category/subcategory")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Product> getAllProducts(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) Long categoryId) {
        log.trace("getAllProducts");

        return productService.findAll(name, categoryId);
    }

    @Operation(summary = "Get product by id")
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetProductResponse> getProductById(@PathVariable @NotNull Long productId) {
        log.trace("getProductById");

        return productService.findById(productId).map(product -> ResponseEntity.ok().body(GetProductResponse.fromDomain(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create product")
    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        log.trace("createProduct");

        log.trace("Creating product " + createProductRequest);

        Long productId;

        try {
            productId = productService.createProduct(
                    createProductRequest.getCategoryId(),
                    createProductRequest.getName(),
                    createProductRequest.getDescription(),
                    createProductRequest.getDailyPrice(),
                    createProductRequest.getBrand(),
                    createProductRequest.getModel()).getId();
        } catch (DataIntegrityViolationException | ProductException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productId)
                .toUri();

        return ResponseEntity.created(uri).body(productId);
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteProduct(@PathVariable @NotNull Long productId) {
        log.trace("deleteProduct");

        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }

}
