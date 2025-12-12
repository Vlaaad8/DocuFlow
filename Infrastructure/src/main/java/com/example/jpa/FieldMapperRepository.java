package com.example.jpa;

import com.example.template.Field;
import com.example.template.FieldMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldMapperRepository extends JpaRepository<FieldMapper,Integer> {
    @Query("SELECT f.field FROM FieldMapper f WHERE f.azureFieldName=:field")
    Optional<Field> getByAzureFieldName(@Param("field") String azureFieldName);
}
