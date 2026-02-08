package com.example.jpa;

import com.example.template.FilledTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilledTemplateRepository extends JpaRepository<FilledTemplate, Integer>{
    int countByUserId(int userId);
}
