package edu.uoc.epcsd.productcatalog;

import edu.uoc.epcsd.productcatalog.entities.Category;
import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    }

    @Test
    public void testGetAllItems() throws Exception {
        getMockMvc().perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].serialNumber", is(this.item.getSerialNumber())))
                .andExpect(jsonPath("$[0].product.id", is(this.item.getProduct().getId().intValue())));
    }

}
