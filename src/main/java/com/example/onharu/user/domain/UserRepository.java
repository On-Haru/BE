package com.example.onharu.user.domain;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    boolean existsByPhone(String phone);

    List<User> findByRole(UserRole role);

    Optional<User> findByPhoneAndCode(String phone, int code);

    Optional<User> findByPhone(String phone);
}
