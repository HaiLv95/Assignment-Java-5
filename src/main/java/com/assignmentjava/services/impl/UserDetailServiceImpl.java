package com.assignmentjava.services.impl;

import com.assignmentjava.repository.AccountRepository;
import com.assignmentjava.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.getAccountByUsernameAndActivated(username, true);
        if (account == null) throw new UsernameNotFoundException("Account " + username + " was not found");
        List<GrantedAuthority> authorityList =new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(account.getRole().toUpperCase()));
        UserDetails userDetails = new User(account.getUsername(), account.getPassword(), authorityList);
        return userDetails;
    }

}
