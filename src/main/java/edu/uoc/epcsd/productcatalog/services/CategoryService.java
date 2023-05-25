package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import edu.uoc.epcsd.productcatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll(String name, String description) {
        return categoryRepository.findByNameAndDescription(name, description);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Long parentId, String name, String description) throws ProductException {

        Category category = Category.builder().name(name).description(description).build();

        if (!findAll(name, null).isEmpty()) {
            throw new ProductException("Category already exists");
        }

        if (parentId != null) {
            Optional<Category> parent = categoryRepository.findById(parentId);

            if (parent.isPresent()) {
                category.setParent(parent.get());
            } else {
                throw new ProductException("Parent category not found");
            }
        }

        return categoryRepository.save(category);
    }

    public List<Category> findAllByParentCategory(Long id) {
        return categoryRepository.findByParentId(id);
    }
}
