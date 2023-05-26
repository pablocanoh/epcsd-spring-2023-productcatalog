package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE " +
            "(COALESCE(:name, NULL) IS NULL OR p.name = :name) AND " +
            "(COALESCE(:categoryId, NULL) IS NULL OR p.category.id = :categoryId)")
    List<Product> findByNameOrCategoryId(@Param("name") String name,
                                         @Param("categoryId") Long categoryId);
}
