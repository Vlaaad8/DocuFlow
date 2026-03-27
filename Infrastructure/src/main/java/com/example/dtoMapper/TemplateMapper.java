package com.example.dtoMapper;

import com.example.dto.TemplateDTO;
import com.example.template.Template;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {FieldMapper.class})
public interface TemplateMapper {
    TemplateMapper INSTANCE = Mappers.getMapper(TemplateMapper.class);

    TemplateDTO toTemplateDTO(Template template);

    Template toTemplate(TemplateDTO templateDTO);


}
