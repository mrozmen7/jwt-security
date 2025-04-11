package com.ozmenyavuz.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


// 	•	JwtService: Token’ı çözmek ve doğrulamak için kullanılır

@Service
@Slf4j
public class JwtService {

    // 🔐 1. Uygulama ayarlarından gelen JWT Secret Key
    @Value("${jwt.key}")
    private String SECRET;

    // 🎯 2. Public: Kullanıcı adıyla token üretir (login sonrası çağrılır)
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        log.info("JWT Secret: {}", SECRET);
        return createToken(claims, userName);
    }

    // 🎯 3. Public: Token geçerli mi? Kullanıcı doğru mu? Süresi geçmiş mi?
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUser(token);
        Date expirationDate = extractExpiration(token);
        return userDetails.getUsername().equals(username) && !expirationDate.before(new Date());
    }

    // 🔓 4. Public: Token içinden subject (kullanıcı adını) çıkarır
    public String extractUser(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ⌛ 5. Private: Token içinden expire tarihi alınır
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // 🛠️ 6. Private: Token oluşturur (içerik + zaman + imza)
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2)) // 2 dk
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🧪 7. Private: Token'ı parse edip içindeki claim’leri döner
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔐 8. Private: Base64 encoded secret key'den Key objesi oluşturur
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}