package com.learning.courseacademy.repository;

import com.learning.courseacademy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserEmail(String email);
    boolean existsByUserName(String userName);
    Optional<User> findByUserName(String userName);
}
