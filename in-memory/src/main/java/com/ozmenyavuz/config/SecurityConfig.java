package com.ozmenyavuz.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //  @PreAuthorize veya @Secured gibi anotasyonlar metotlar icin
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users() {

        UserDetails user1 = User.builder()
                .username("yvz")
                .password(passwordEncoder().encode("pass"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("OzmenYavuz")
                .password(passwordEncoder().encode("pass"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                .headers(q -> q.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable) // PASIFSE POST/PUT ISTEKLERI RAHAT YAPAR
                .formLogin(AbstractHttpConfigurer::disable) //
                .authorizeHttpRequests(q -> q.requestMatchers("/public/**", "/auth/**").permitAll())
                .authorizeHttpRequests(q -> q.requestMatchers("/private/user/**").hasRole("USER"))
                .authorizeHttpRequests(q -> q.requestMatchers("/private/admin/**").hasRole("ADMIN"))
                .authorizeHttpRequests(q -> q.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()); // Kullanıcı adı ve şifre ile gelen istekleri Authorization: Basic başlığıyla kabul eder.


        return security.build();
    }
}
