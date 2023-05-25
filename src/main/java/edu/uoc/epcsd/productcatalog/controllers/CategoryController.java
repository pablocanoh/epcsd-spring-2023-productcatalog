package edu.uoc.epcsd.productcatalog.controllers;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateCategoryRequest;
import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import edu.uoc.epcsd.productcatalog.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Get all categories and filter by name or description")
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllCategories(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String description) {
        log.trace("getAllCategories");

        return categoryService.findAll(name, description);
    }

    @Operation(summary = "create category")
    @PostMapping("/")
    public ResponseEntity<Long> createCategory(@RequestBody CreateCategoryRequest createCategoryRequest) {
        log.trace("createCategory");

        log.trace("Creating category " + createCategoryRequest);
        Long categoryId = null;
        try {
            categoryId = categoryService.createCategory(
                    createCategoryRequest.getParentId(),
                    createCategoryRequest.getName(),
                    createCategoryRequest.getDescription()).getId();
        } catch (ProductException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    e);
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryId)
                .toUri();

        return ResponseEntity.created(uri).body(categoryId);
    }

    @Operation(summary = "Get all categories by parent category")
    @GetMapping("/{id}/subcategories")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllCategoriesByParentCategory(@PathVariable Long id) {
        log.trace("getAllCategoriesByParentCategory");

        return categoryService.findAllByParentCategory(id);
    }
}
