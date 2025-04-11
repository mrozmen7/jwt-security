package com.ozmenyavuz.service;



import com.ozmenyavuz.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Bu sınıf, Spring Security için kullanıcıyı veritabanından getirir.
//Spring Security login işleminde bu sınıfı otomatik olarak kullanır.


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userService.getByUsername(username);
        return (UserDetails) user.orElseThrow(EntityNotFoundException::new);
    }
}