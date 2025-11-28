package com.example.jpa;

import com.example.template.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    List<Field> getFieldByRequired(boolean required);
}
