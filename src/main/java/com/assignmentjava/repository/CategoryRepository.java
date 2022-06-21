package com.assignmentjava.repository;

import com.assignmentjava.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query
    List<Category> findAllByActivated(boolean activaed);
}
