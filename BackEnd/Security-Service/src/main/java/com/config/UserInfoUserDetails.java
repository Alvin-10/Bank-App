package com.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.entity.UserInfo;

import java.util.Collection;
import java.util.Collections;

public class UserInfoUserDetails implements UserDetails {
    private String name;
    private String password;
    // Remove the authorities field
    // private List<GrantedAuthority> authorities;

    public UserInfoUserDetails(UserInfo userInfo) {
        name = userInfo.getName();
        password = userInfo.getPassword();
        // Remove the roles handling
        // authorities = Arrays.stream(userInfo.getRoles().split(","))
        //         .map(SimpleGrantedAuthority::new)
        //         .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return an empty list instead of authorities
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // Return true to indicate the account is enabled
    }
}