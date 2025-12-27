package com.example.web;

import com.example.*;
import com.example.email.EmailPort;
import com.example.jpa.*;
import com.example.ocr.DocumentPort;
import com.example.ocr.IdPort;
import com.example.ocr.MappingPort;
import com.example.template.TemplateTextPort;
import com.example.template.UserFieldValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public IdService idService(IdPort idPort) {
        return new IdService(idPort);
    }

    @Bean
    public DocumentService documentService(DocumentPort documentPort) {
        return new DocumentService(documentPort);
    }

    @Bean
    public TemplateService templateService(TemplateTextPort templateTextPort, FieldRepository fieldRepository, TemplateRepository templateRepository) {
        return new TemplateService(templateTextPort, fieldRepository, templateRepository);
    }

    @Bean
    public MappingService mappingService(FieldMapperRepository fieldMapperRepository, UserRepository userRepository, UserFieldValueRepository userFieldValueRepository) {
        return new MappingService(fieldMapperRepository, userRepository, userFieldValueRepository);
    }
    @Bean
    public GeneratorService generatorService(TemplateRepository templateRepository, UserFieldValueRepository userFieldValueRepository, UserRepository userRepository, FilledTemplateRepository filledTemplateRepository, MappingPort mappingPort, EmailPort emailPort) {
        return new GeneratorService(templateRepository,userFieldValueRepository,userRepository,filledTemplateRepository,mappingPort,emailPort);
    }
}
