package com.example.flyWeight;

import com.example.jpa.FieldRepository;
import com.example.template.Field;
import com.example.template.FieldFlyweight;
import com.example.template.FieldFlyweightImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@AllArgsConstructor
public class FieldFlyweightFactory {

    private final FieldRepository fieldRepository;


    private final Map<String, FieldFlyweight> cache =
            new ConcurrentHashMap<>();


    @PostConstruct
    public void initialize() {
        List<Field> allFields = fieldRepository.findAll();
        for (Field field : allFields) {
            cache.put(field.getFieldName(),
                    new FieldFlyweightImpl(field));
        }
    }


    public FieldFlyweight getFlyweight(String fieldName) {
        return cache.computeIfAbsent(fieldName, key ->
                fieldRepository.findByFieldName(key)
                        .map(FieldFlyweightImpl::new)
                        .orElseThrow(() -> new RuntimeException(
                                "Field not found: " + key))
        );
    }

    public List<FieldFlyweight> getAllFlyweights() {
        return Collections.unmodifiableList(
                List.copyOf(cache.values()));
    }

    public int getCacheSize() {
        return cache.size();
    }
}