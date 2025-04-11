package com.ozmenyavuz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import java.util.Set;


@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String password;

    private boolean accountNonExpired;         // Hesabın süresi dolmuş mu?
    private boolean accountNonLocked;          // Hesap kilitli mi?
    private boolean credentialsNonExpired;     // Şifre süresi geçmiş mi?
    private boolean enabled;                   // Kullanıcı aktif mi?

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<UserRole> authorities;



}


// public enum Role implements GrantedAuthority {
//    ROLE_USER("USER"),
//    ROLE_ADMIN("ADMIN"),
//    ROLE_MOD("MOD"),
//    ROLE_FSK("FSK");
//
//    private String value;
//
//    Role(String value) {
//        this.value = value;
//    }
//
//    public String getValue() {
//        return this.value;
//    }
//
//    @Override
//    public String getAuthority() {
//        return name();
//    }
//}
