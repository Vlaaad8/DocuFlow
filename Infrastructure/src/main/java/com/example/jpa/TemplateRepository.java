package com.example.jpa;

import com.example.template.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Integer> {

    Optional<Template> findTemplateByStoragePath(String storagePath);
}
