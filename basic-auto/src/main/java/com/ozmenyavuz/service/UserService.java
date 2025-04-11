package com.ozmenyavuz.service;


import com.ozmenyavuz.dto.CreateUserRequest;
import com.ozmenyavuz.model.User;
import com.ozmenyavuz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(CreateUserRequest request) {

        User newUser = User.builder()
                .name(request.name())                         // kullanıcının adı
                .username(request.username())                 // giriş için kullanılacak kullanıcı adı
                .password(passwordEncoder.encode(request.password())) // şifre hashlenir
                .credentialsNonExpired(true)                 // şifresi süresi geçmemiş
                .authorities(request.authorities())          // kullanıcıya ait roller (Set<UserRole>)
                .enabled(true)                                // kullanıcı aktif mi
                .accountNonLocked(true)                       // hesabı kilitli mi
                .build();                                     // Builder nesnesini oluştur


        return userRepository.save(newUser); // Veri tabanina kaydedilir

    }


}
