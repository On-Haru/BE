package com.example.onharu.user.domain;

import com.example.onharu.global.util.RandomUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", length = 50)
    private String name;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "year")
    private String year;

    @Column(name = "password")
    private String password;

    @Column(name = "code")
    private int code;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    private User(String name, String phone, UserRole role, String year, String password) {
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.year = year;
        this.password = password;
    }

    public static User create(String name, String phone, UserRole role, String year,
            String password) {
        return new User(name, phone, role, year, password);
    }

    public void generateCode() {
        this.code = RandomUtil.generateCode();
    }

    public boolean isCaregiver() {
        return hasRole(UserRole.CAREGIVER);
    }

    public boolean isSenior() {
        return hasRole(UserRole.SENIOR);
    }

    private boolean hasRole(UserRole role) {
        return this.role == role;
    }
}
