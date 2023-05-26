package edu.uoc.epcsd.productcatalog.services;

import edu.uoc.epcsd.productcatalog.entities.Item;
import edu.uoc.epcsd.productcatalog.entities.ItemStatus;
import edu.uoc.epcsd.productcatalog.entities.Product;
import edu.uoc.epcsd.productcatalog.exceptions.MissingProductException;
import edu.uoc.epcsd.productcatalog.exceptions.ProductException;
import edu.uoc.epcsd.productcatalog.kafka.KafkaConstants;
import edu.uoc.epcsd.productcatalog.kafka.ProductMessage;
import edu.uoc.epcsd.productcatalog.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private KafkaTemplate<String, ProductMessage> productKafkaTemplate;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findBySerialNumber(String serialNumber) {
        return itemRepository.findBySerialNumber(serialNumber);
    }

    public Item setOperational(String serialNumber, @RequestBody Boolean operational) throws ProductException, MissingProductException {
        Item item = findBySerialNumber(serialNumber).orElseThrow(
                () -> new MissingProductException("Could not find the item with serial number: " + serialNumber)
            );

        if (item.getStatus().equals(ItemStatus.OPERATIONAL) && operational) {
            throw new ProductException("Item is already operational");
        } else if (item.getStatus().equals(ItemStatus.NON_OPERATIONAL) && !operational) {
            throw new ProductException("Item is already non-operational");
        }

        item.setStatus(operational ? ItemStatus.OPERATIONAL : ItemStatus.NON_OPERATIONAL);

        item = itemRepository.save(item);

        if (item.getStatus().equals(ItemStatus.OPERATIONAL)) {
            productKafkaTemplate.send(KafkaConstants.PRODUCT_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, ProductMessage.builder().productId(item.getProduct().getId()).build());
        }

        return item;
    }

    public Item createItem(Long productId, String serialNumber) throws ProductException {

        // bu default a new unit is OPERATIONAL
        Item item = Item.builder().serialNumber(serialNumber).status(ItemStatus.OPERATIONAL).build();

        Optional<Product> product = productService.findById(productId);

        if (product.isPresent()) {
            item.setProduct(product.get());
        } else {
            throw new ProductException("Could not find the product with Id: " + productId);
        }

        Item savedItem = itemRepository.save(item);

        productKafkaTemplate.send(KafkaConstants.PRODUCT_TOPIC + KafkaConstants.SEPARATOR + KafkaConstants.UNIT_AVAILABLE, ProductMessage.builder().productId(productId).build());

        return savedItem;
    }
}
