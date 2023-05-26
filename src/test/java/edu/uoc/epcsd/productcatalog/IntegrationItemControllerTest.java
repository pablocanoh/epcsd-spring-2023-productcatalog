package edu.uoc.epcsd.productcatalog;

import edu.uoc.epcsd.productcatalog.controllers.dtos.CreateItemRequest;
import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationItemControllerTest extends IntegrationBaseTest{

    private Item item;

    @BeforeEach
    public void setup() throws ProductException {
        Category category = getCategoryService().createCategory(null, "Test Category", "Test Description");
        Product product = getProductService()
                .createProduct(
                        category.getId(),
                        "Test Product",
                        "Test Description",
                        1.0, "brand",
                        "model" );

        this.item = getItemService().createItem(product.getId(), "122122-1222");

        Mockito.reset(getProductKafkaTemplate());
    }

    @Test
    public void testGetAllItems() throws Exception {
        getMockMvc().perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].serialNumber", is(this.item.getSerialNumber())))
                .andExpect(jsonPath("$[0].product.id", is(this.item.getProduct().getId().intValue())));
    }

    @Test
    public void testGetItemBySerialNumber() throws Exception {
        Item item = getItemService().createItem(this.item.getProduct().getId(), "122122-1223");

        getMockMvc().perform(get("/items/"+item.getSerialNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber", is("122122-1223")));
    }

    @Test
    public void testNotFoundItemBySerialNumber() throws Exception {
        getMockMvc().perform(get("/items/123456789"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateItem() throws Exception {
        CreateItemRequest createItemRequest = new CreateItemRequest(
                this.item.getProduct().getId(),
                "122122-1224"
        );

        getMockMvc().perform(post("/items")
                .content(getObjectMapper().writeValueAsString(createItemRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(getProductKafkaTemplate(), times(1)).send(any(), any());
    }

    @Test
    public void testCreateItemWithInvalidProduct() throws Exception {
        CreateItemRequest createItemRequest = new CreateItemRequest(
                123456789L,
                "122122-1224"
        );

        getMockMvc().perform(post("/items")
                .content(getObjectMapper().writeValueAsString(createItemRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(getProductKafkaTemplate(), never()).send(any(), any());
    }

    @Test
    public void testUpdateItem() throws Exception {
        Item item = getItemService().createItem(this.item.getProduct().getId(), "122122-1225");

        CreateItemRequest createItemRequest = new CreateItemRequest(
                this.item.getProduct().getId(),
                "122122-1226"
        );

        Mockito.reset(getProductKafkaTemplate());

        getMockMvc().perform(patch("/items/"+item.getSerialNumber()+"/status")
                .content("false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(getProductKafkaTemplate(), never()).send(any(), any());

        getMockMvc().perform(patch("/items/"+item.getSerialNumber()+"/status")
                .content("true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(getProductKafkaTemplate(), atMostOnce()).send(any(), any());
    }

    @Test
    public void testUpdateItemWithInvalidSerialNumber() throws Exception {
        getMockMvc().perform(patch("/items/123456789/status")
                .content("false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateItemWithInvalidStatus() throws Exception {
        Item item = getItemService().createItem(this.item.getProduct().getId(), "122122-1227");

        getMockMvc().perform(patch("/items/"+item.getSerialNumber()+"/status")
                .content("true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateItemWithInvalidStatus2() throws Exception {
        Item item = getItemService().createItem(this.item.getProduct().getId(), "122122-1227");

        getMockMvc().perform(patch("/items/"+item.getSerialNumber()+"/status")
                        .content("false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        getMockMvc().perform(patch("/items/"+item.getSerialNumber()+"/status")
                        .content("false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
