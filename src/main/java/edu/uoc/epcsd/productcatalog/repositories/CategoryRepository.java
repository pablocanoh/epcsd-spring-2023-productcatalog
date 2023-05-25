package edu.uoc.epcsd.productcatalog.repositories;

import edu.uoc.epcsd.productcatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentId(Long id);

    @Query("SELECT c FROM Category c WHERE " +
            "(:name is null or c.name = :name) and " +
            "(:description is null or c.description = :description)")
    List<Category> findByNameAndDescription(@Param("name") String name, @Param("description") String description);

}
