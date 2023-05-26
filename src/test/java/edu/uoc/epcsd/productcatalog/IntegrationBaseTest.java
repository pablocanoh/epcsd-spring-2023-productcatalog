package edu.uoc.epcsd.productcatalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uoc.epcsd.productcatalog.kafka.KafkaProducerConfig;
import edu.uoc.epcsd.productcatalog.kafka.KafkaTopicConfig;
import edu.uoc.epcsd.productcatalog.kafka.ProductMessage;
import edu.uoc.epcsd.productcatalog.repositories.CategoryRepository;
import edu.uoc.epcsd.productcatalog.repositories.ItemRepository;
import edu.uoc.epcsd.productcatalog.repositories.ProductRepository;
import edu.uoc.epcsd.productcatalog.services.CategoryService;
import edu.uoc.epcsd.productcatalog.services.ItemService;
import edu.uoc.epcsd.productcatalog.services.ProductService;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Getter
public class IntegrationBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private KafkaTemplate<String, ProductMessage> productKafkaTemplate;

    @MockBean
    private KafkaProducerConfig kafkaProducerConfig;

    @MockBean
    private KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    public void tearDown() {
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }
}
