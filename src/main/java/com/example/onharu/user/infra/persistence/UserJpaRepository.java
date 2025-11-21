package com.example.onharu.user.infra.persistence;

import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import com.example.onharu.user.domain.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

    boolean existsByPhone(String phone);

    List<User> findByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.code = :code")
    Optional<User> findByPhoneAndCode(String phone, int code);

    Optional<User> findByPhone(String phone);
}
