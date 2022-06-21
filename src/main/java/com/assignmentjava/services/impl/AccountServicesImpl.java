package com.assignmentjava.services.impl;

import com.assignmentjava.model.Account;
import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AccountServicesImpl implements AccountServices {
    @Autowired
    private AccountRepository accountRepository;

    @Query
    public Account getAccountByUsernameAndActivated(String username, Boolean activated) {
        return accountRepository.getAccountByUsernameAndActivated(username, activated);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findAll(Sort sort) {
        return accountRepository.findAll(sort);
    }

    public List<Account> findAllById(Iterable<String> strings) {
        return accountRepository.findAllById(strings);
    }

    public <S extends Account> List<S> saveAll(Iterable<S> entities) {
        return accountRepository.saveAll(entities);
    }

    public void flush() {
        accountRepository.flush();
    }

    public <S extends Account> S saveAndFlush(S entity) {
        return accountRepository.saveAndFlush(entity);
    }

    public <S extends Account> List<S> saveAllAndFlush(Iterable<S> entities) {
        return accountRepository.saveAllAndFlush(entities);
    }

    @Deprecated
    public void deleteInBatch(Iterable<Account> entities) {
        accountRepository.deleteInBatch(entities);
    }

    public void deleteAllInBatch(Iterable<Account> entities) {
        accountRepository.deleteAllInBatch(entities);
    }

    public void deleteAllByIdInBatch(Iterable<String> strings) {
        accountRepository.deleteAllByIdInBatch(strings);
    }

    public void deleteAllInBatch() {
        accountRepository.deleteAllInBatch();
    }

    @Deprecated
    public Account getOne(String s) {
        return accountRepository.getOne(s);
    }

    public Account getById(String s) {
        return accountRepository.getById(s);
    }

    public <S extends Account> List<S> findAll(Example<S> example) {
        return accountRepository.findAll(example);
    }

    public <S extends Account> List<S> findAll(Example<S> example, Sort sort) {
        return accountRepository.findAll(example, sort);
    }

    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public <S extends Account> S save(S entity) {
        return accountRepository.save(entity);
    }

    public Optional<Account> findById(String s) {
        return accountRepository.findById(s);
    }

    public boolean existsById(String s) {
        return accountRepository.existsById(s);
    }

    public long count() {
        return accountRepository.count();
    }

    public void deleteById(String s) {
        accountRepository.deleteById(s);
    }

    public void delete(Account entity) {
        accountRepository.delete(entity);
    }

    public void deleteAllById(Iterable<? extends String> strings) {
        accountRepository.deleteAllById(strings);
    }

    public void deleteAll(Iterable<? extends Account> entities) {
        accountRepository.deleteAll(entities);
    }

    public void deleteAll() {
        accountRepository.deleteAll();
    }

    public <S extends Account> Optional<S> findOne(Example<S> example) {
        return accountRepository.findOne(example);
    }

    public <S extends Account> Page<S> findAll(Example<S> example, Pageable pageable) {
        return accountRepository.findAll(example, pageable);
    }

    public <S extends Account> long count(Example<S> example) {
        return accountRepository.count(example);
    }

    public <S extends Account> boolean exists(Example<S> example) {
        return accountRepository.exists(example);
    }

    public <S extends Account, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return accountRepository.findBy(example, queryFunction);
    }
}
