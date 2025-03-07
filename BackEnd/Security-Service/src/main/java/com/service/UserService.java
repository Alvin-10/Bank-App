package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.entity.UserInfo;
import com.repository.UserInfoRepository;

@Service
public class UserService {
    @Autowired
    private UserInfoRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserInfo loadUserByUsername(String username) {
        return repository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addUser(UserInfo userInfo) {
        String name = userInfo.getName();
        UserInfo existingUser = repository.findByName(name).orElse(null);
        if (existingUser == null) {
            userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            repository.save(userInfo);
            return "Registration Successful";
        } else {
            return "This Username is Already Registered.";
        }
    }
}