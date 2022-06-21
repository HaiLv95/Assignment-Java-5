package com.assignmentjava.repository;

import com.assignmentjava.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query
    Account getAccountByUsernameAndActivated(String username, Boolean activated);

}
