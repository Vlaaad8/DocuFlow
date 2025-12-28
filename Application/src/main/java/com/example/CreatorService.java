package com.example;

import com.example.jpa.FieldRepository;
import com.example.template.Field;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreatorService {
    private final FieldRepository fieldRepository;
    public CreatorService(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }
}
