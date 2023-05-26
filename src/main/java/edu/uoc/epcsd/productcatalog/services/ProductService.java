package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import edu.uoc.epcsd.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    public List<Product> findAll(String name, Long categoryId) {
        return productRepository.findByNameOrCategoryId(name, categoryId);
    }

    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public Product createProduct(Long categoryId, String name, String description, Double dailyPrice, String brand, String model) throws ProductException {
        Product product = Product.builder().name(name).description(description).dailyPrice(dailyPrice).brand(brand).model(model).build();

        if (!productRepository.findByNameOrCategoryId(name, categoryId).isEmpty()) {
            throw new ProductException("Product already exists");
        }

        if (categoryId != null) {
            Optional<Category> category = categoryService.findById(categoryId);

            category.ifPresent(product::setCategory);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
