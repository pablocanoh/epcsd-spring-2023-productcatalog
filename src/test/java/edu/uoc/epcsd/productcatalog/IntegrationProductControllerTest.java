package edu.uoc.epcsd.productcatalog;


import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateProductRequest;
import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationProductControllerTest extends IntegrationBaseTest {

    Product product;
    Category category;

    @BeforeEach
    public void setup() throws ProductException {
        category = getCategoryService().createCategory(null, "Test Category", "Test Description");
        product = getProductService().createProduct(
                category.getId(),
                "Test Product",
                "Test Description",
                1.0,
                "brand",
                "model" );
    }

    @Test
    public void testGetAllProducts() throws Exception {
        getMockMvc().perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetAllProductsByName() throws Exception {
        getProductService().createProduct(
                category.getId(),
                "Test Product 2",
                "Test Description 2",
                1.0,
                "brand",
                "model" );

        getMockMvc().perform(get("/products?name=Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetAllProductsByCategory() throws Exception {
        Category category1 = getCategoryService().createCategory(null, "Test Category 2", "Test Description");
        getProductService().createProduct(
                category.getId(),
                "Test Product 2",
                "Test Description 2",
                1.0,
                "brand",
                "model" );
        getProductService().createProduct(
                category1.getId(),
                "Test Product 3",
                "Test Description 2",
                1.0,
                "brand",
                "model" );

        getMockMvc().perform(get("/products?categoryId=" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetAllProductsByCategoryAndName() throws Exception {
        Category category1 = getCategoryService().createCategory(null, "Test Category 2", "Test Description");
        getProductService().createProduct(
                category.getId(),
                "Test Product 2",
                "Test Description 2",
                1.0,
                "brand",
                "model" );
        getProductService().createProduct(
                category1.getId(),
                "Test Product 3",
                "Test Description 2",
                1.0,
                "brand",
                "model" );

        getMockMvc().perform(get("/products?categoryId=" + category.getId() + "&name=Test Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testGetProduct() throws Exception {
        getMockMvc().perform(get("/products/{productId}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.dailyPrice").value(product.getDailyPrice()))
                .andExpect(jsonPath("$.brand").value(product.getBrand()))
                .andExpect(jsonPath("$.model").value(product.getModel()));
    }

    @Test
    public void testGetProductNotFound() throws Exception {
        getMockMvc().perform(get("/products/{productId}", 0))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateProduct() throws Exception {
        CreateProductRequest createProductRequest = new CreateProductRequest(
                "Test Product 2",
                "Test Description 2",
                category.getId(),
                1D,
                "brand",
                "model" );
        getMockMvc().perform(post("/products")
                .content(getObjectMapper().writeValueAsString(createProductRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateProductWithInvalidCategoryId() throws Exception {
        CreateProductRequest createProductRequest = new CreateProductRequest(
                "Test Product 2",
                "Test Description 2",
                0L,
                1D,
                "brand",
                "model" );

        getMockMvc().perform(post("/products")
                .content(getObjectMapper().writeValueAsString(createProductRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateProductWithDuplicateName() throws Exception {
        CreateProductRequest createProductRequest = new CreateProductRequest(
                product.getName(),
                "Test Description 2",
                category.getId(),
                1D,
                "brand",
                "model" );

        getMockMvc().perform(post("/products")
                .content(getObjectMapper().writeValueAsString(createProductRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Confirm the product has NOT been deleted
        assertFalse(getProductRepository().findAll().isEmpty());

        getMockMvc().perform(delete("/products/{productId}", product.getId()))
                .andExpect(status().isNoContent());

        // Confirm the product has been deleted
        assertTrue(getProductRepository().findAll().isEmpty());
    }
}
