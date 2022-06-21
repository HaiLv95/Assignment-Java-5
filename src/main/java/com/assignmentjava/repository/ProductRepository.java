package com.assignmentjava.repository;

import com.assignmentjava.model.Category;
import com.assignmentjava.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(value = "select p.categoryID from Product p group by p.categoryID order by count(p.categoryID) desc")
    List<Category> findTop3CategoryID();

    @Query
    Page<Product> findAllByAvailable(boolean available, Pageable pageable);

    @Query
    Page<Product> findAllByNameContaining(String name, Pageable pageable);

    @Query
    Page<Product> findAllByCategoryIDAndAvailable(Category categoryID, Boolean available, Pageable pageable);
}
