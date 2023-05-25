package edu.uoc.epcsd.productcatalog;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateCategoryRequest;
import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationCategoryControllerTest extends IntegrationBaseTest {

    Category category;

    @BeforeEach
    public void setup() throws ProductException {
        getCategoryRepository().deleteAll();

        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Description");
        this.category  = getCategoryService().createCategory(null, category.getName(), category.getDescription());
    }

    @Test
    public void testGetAllCategories() throws Exception {
        getMockMvc().perform(get("/categories/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Category")));
    }

    @Test
    public void testGetAllCategoriesByName() throws Exception {
        getCategoryService().createCategory(null, "name-match", category.getDescription());

        getMockMvc().perform(get("/categories/")
                        .param("name", "name-match")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("name-match")));
    }

    @Test
    public void testGetAllCategoriesByDescription() throws Exception {
        getCategoryService().createCategory(null, "name X", "description-match");

        getMockMvc().perform(get("/categories/")
                        .param("description", "description-match")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("description-match")));
    }

    @Test
    public void testGetAllCategoriesByDescriptionAndName() throws Exception {
        getCategoryService().createCategory(null, "name-match", "description-match");
        getCategoryService().createCategory(null, "name-non-match", "description-match");

        getMockMvc().perform(get("/categories/")
                        .param("name", "name-match")
                        .param("description", "description-match")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("description-match")))
                .andExpect(jsonPath("$[0].name", is("name-match")));
    }

    @Test
    public void testCreateCategory() throws Exception {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest(
                "Test Category 2",
                "Test Description 2",
                null
        );

        getMockMvc().perform(post("/categories/")
                        .content(getObjectMapper().writeValueAsString(createCategoryRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateCategoryWithParent() throws Exception {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest(
                "Test Category 2",
                "Test Description 2",
                category.getId()
        );

        getMockMvc().perform(post("/categories/")
                        .content(getObjectMapper().writeValueAsString(createCategoryRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateCategoryWithNonExistingParent() throws Exception {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest(
                "Test Category 2",
                "Test Description 2",
                99911L
        );

        getMockMvc().perform(post("/categories/")
                        .content(getObjectMapper().writeValueAsString(createCategoryRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateExistingCategory() throws Exception {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest(
                "Test Category",
                "Test Description",
                null
        );

        getMockMvc().perform(post("/categories/")
                        .content(getObjectMapper().writeValueAsString(createCategoryRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllCategoriesByParent() throws Exception {
        Category category2 = getCategoryService().createCategory(null, "Test Category 2", "Test Description 2");
        getCategoryService().createCategory(category2.getId(), "Subcategory", "Test Description 3");

        getMockMvc().perform(get("/categories/"+category2.getId()+"/subcategories/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].parent.id", is(category2.getId().intValue())));
    }

}
