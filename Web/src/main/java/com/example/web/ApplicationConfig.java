package com.example.web;

import com.example.DocumentService;
import com.example.IdService;
import com.example.ocr.DocumentPort;
import com.example.ocr.IdPort;
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
}
