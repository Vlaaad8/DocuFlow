package com.example.dtoMapper;

import com.example.dto.FieldDTO;
import com.example.template.Field;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FieldMapper {

    FieldMapper INSTANCE = Mappers.getMapper(FieldMapper.class);

    FieldDTO toFieldDTO(Field field);

    Field toFieldDTO(FieldDTO fieldDTO);
}
