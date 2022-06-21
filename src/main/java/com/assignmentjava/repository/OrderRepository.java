package com.assignmentjava.repository;

import com.assignmentjava.model.Account;
import com.assignmentjava.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query
    List<Order> getAllByUsername(Account username);

    @Query
    Page<Order> findAllByStatus(String status, Pageable pageable);
}
