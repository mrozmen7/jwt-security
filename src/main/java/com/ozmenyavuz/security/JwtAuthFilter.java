package com.ozmenyavuz.security;

import com.ozmenyavuz.service.JwtService;
import com.ozmenyavuz.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Kullanıcının gönderdiği JWT token’ı her HTTP isteğinde kontrol eden güvenlik filtresidir.
// Gelen HTTP isteklerinde “Authorization” header’ında JWT var mı?
// Varsa:
//✅ Token geçerli mi?
//✅ Kime ait?
//✅ Token süresi geçmemiş mi?
//✅ Bu kullanıcıyı tanıyıp Spring Security’ye tanıtalım mı?

@Component
@Slf4j
// OncePerRequestFilter: Spring Security’de özel filtre yazmanın doğru yoludur, her istekte bir kere çalışır
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // Token’ı çözmek ve doğrulamak için
    private final UserService userService; // Token içindeki kullanıcı adından yola çıkarak UserDetails nesnesi alınır

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }
//  Bu metod, her HTTP isteğinde otomatik olarak çağrılır.
//Amaç: Authorization başlığına bakmak ve varsa JWT token’ı kontrol etmek.
@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    // ❗️ Giriş/ekleme endpoint'lerinde token kontrolü yapma
    String path = request.getRequestURI();
    if (path.contains("/auth/welcome") || path.contains("/auth/generateToken") || path.contains("/auth/addNewUser")) {
        filterChain.doFilter(request, response);
        return;
    }

    String authHeader = request.getHeader("Authorization");
    String token = null;
    String userName = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        userName = jwtService.extractUser(token);
    }

    if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails user = userService.loadUserByUsername(userName);
        log.info("user loaded " + user);
        if (jwtService.validateToken(token, user)) {
            log.info("token validated " + token);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    filterChain.doFilter(request, response);
}
}