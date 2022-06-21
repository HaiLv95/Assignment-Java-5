package com.assignmentjava.repository;

import com.assignmentjava.model.Order;
import com.assignmentjava.model.Orderdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<Orderdetail, Integer> {
    @Query
    List<Orderdetail> findAllByOrderID(Order order);
}
