package com.example.jpa;

import com.example.login.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("select u from User u where u.password=:password  and (u.username=:value or u.email=:value)")
    Optional<User> login(@Param("password") String password, @Param("value") String value);
}
