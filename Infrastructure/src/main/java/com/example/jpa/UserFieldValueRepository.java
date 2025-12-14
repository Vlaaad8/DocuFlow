package com.example.jpa;

import com.example.template.UserFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserFieldValueRepository extends JpaRepository<UserFieldValue,Integer> {

    Optional<UserFieldValue> findByUser_IdAndField_id(int userID, int fieldID);

}
